package com.jinjin.bidsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjin.bidsystem.config.configProperties.AlimtalkProperties;
import com.jinjin.bidsystem.mapper.BidMapper;
import com.jinjin.bidsystem.mapper.MatchMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 주요 메서드 설명 */
/* 1. getAccessToken():
      - 알림톡 API에서 인증 토큰을 가져오는 메서드
      - 인증 토큰은 이후 메시지 전송에 필요 */

/* 2. sendAlimtalkByMatch(String matchNumber, String ACCESS_TOKEN):
      - 특정 경기 번호와 관련된 모든 낙찰자들에게 알림톡 메시지를 전송
      - 전송 이후 해당 경기의 알림톡 전송 상태 flag를 업데이트 */
      
/* 3. sendOneAlimTalk(String alimMessage, String telno, String token):
      - 단일 사용자에게 알림톡 메시지를 전송
      - 메시지 내용과 사용자 전화번호, 인증 토큰을 사용 */


@Service
public class AlimtalkService {

    private static final Logger logger = LoggerFactory.getLogger(AlimtalkService.class);

    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private AlimtalkProperties alimtalkProperties;

    private RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("deprecation")
    public String getAccessToken() {
        logger.info("getAccessToken 메서드 시작");

        String retToken = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bsid", alimtalkProperties.getBsid());
        jsonObject.put("passwd", alimtalkProperties.getPasswd());

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            logger.info("HttpEntity 생성 완료: {}", entity);

            ResponseEntity<String> response = restTemplate.exchange(alimtalkProperties.getTokenUrl(), HttpMethod.POST, entity, String.class);

            if (response.getStatusCodeValue() != 200) {
                logger.info("getToken 응답 오류: HTTP {}", response.getStatusCodeValue());
                return "";
            } else {
                logger.info("응답 상태 코드 확인 완료: 200 OK");
            }

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            logger.info("response 파싱된 JSON 객체: {}", responseJson);

            String responseCode = responseJson.get("responseCode").asText();
            logger.info("응답 코드(responseCode): {}", responseCode);

            if ("1000".equals(responseCode)) {
                retToken = responseJson.get("token").asText();
                logger.info("토큰 발급 성공: {}", retToken);
            } else {
                String errorMsg = responseJson.get("msg").asText();
                logger.info("토큰 발급 실패: {} - {}", responseCode, errorMsg);
                return "";
            }
        } catch (Exception e) {
            throw new ServerException("시스템 오류 : 알림톡 전송 토큰 획득에 실패하였습니다.", e);
        }
        logger.info("getAccessToken 메서드 종료, 반환할 토큰: {}", retToken);
        return retToken;
    }
    public Map<String, Object> sendAlimtalkByMatch(String matchNumber, String ACCESS_TOKEN) {

        // Logger 인스턴스 생성
        Logger logger = LoggerFactory.getLogger(AlimtalkService.class);
    
        // 경기 정보 구성
        Map<String, Object> matchParams = new HashMap<>();
        matchParams.put("matchNumber", matchNumber);
        try {
            Map<String, Object> matchResults = matchMapper.getMatchStatus(matchParams);
            String match_name = (String) matchResults.get("match_name");
            String round_name = (String) matchResults.get("round");
            String match_info = match_name + " " + round_name + " (" + matchNumber + ")";
    
            // 결제시한 정보 구성
            String pay_due = "";
            Timestamp paydueTimestamp = (Timestamp) matchResults.get("pay_due_datetime");
            if (paydueTimestamp != null) {
                LocalDateTime paydueDatetime = paydueTimestamp.toLocalDateTime();
                pay_due = paydueDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
    
            // 낙찰 정보 구성 - 경기별 여러 사용자의 정보로 구성됨
            List<Map<String, Object>> bidResults = bidMapper.getAwardedBidsByMatch(matchParams);
            logger.info("현재 경기의 낙찰 정보 데이터 건수: {}", bidResults.size());
    
            // 전화번호별로 정보 저장
            Map<String, String> telnoUsername = new HashMap<>();
            Map<String, Integer> telnoBidTotal = new HashMap<>();
            Map<String, StringBuilder> telnoBidsArray = new HashMap<>();
    
            for (Map<String, Object> result : bidResults) {
                // 값 추출
                String bid_telno = result.get("bid_telno").toString();
                String seat_no = result.get("seat_no").toString();
                int bid_amount = Integer.parseInt(result.get("bid_amount").toString());
                int total_bid_amount = Integer.parseInt(result.get("total_bid_amount").toString());
                String username = result.get("username").toString();
    
                // 전화번호별로 사용자 이름 저장
                telnoUsername.putIfAbsent(bid_telno, username);
    
                // 전화번호별로 입찰 총액
                telnoBidTotal.put(bid_telno, total_bid_amount);
    
                // 낙찰 내역 누적
                telnoBidsArray.putIfAbsent(bid_telno, new StringBuilder());
                telnoBidsArray.get(bid_telno).append(seat_no).append("번,  입찰액: ").append(bid_amount).append("원\n");
            }
    
            // 각 전화번호별로 메시지 생성 및 전송
            for (String tel : telnoBidsArray.keySet()) {
                String userName = telnoUsername.get(tel);
                String bidsArray = telnoBidsArray.get(tel).toString();
                int bidTotal = telnoBidTotal.get(tel);
    
                // 알림톡 템플릿에 맞춘 메시지 문자열 생성
                String message = userName + "님의 낙찰 내용을 알려드립니다.\n" +
                                "경기 : " + match_info + "\n" +
                                "좌석   입찰금액\n" +
                                "--------------------------------\n" +
                                bidsArray +
                                "--------------------------------\n" +
                                "총 결제 금액은 " + bidTotal + "원입니다.\n" +
                                "결제시한 : " + pay_due;
    
                logger.info("\n알림톡 전송 전화번호: {}\n\n{}\n", tel, message);
    
                // 전화번호별로 메시지 전송
                sendOneAlimTalk(message, tel, ACCESS_TOKEN);
            }
    
            int affectedRows = matchMapper.updateMatchAlimtalkStatus(matchParams);
            if (affectedRows == 0) {
                throw new NotFoundException("시스템 오류 : 알림톡 송신 정보 갱신에 실패하였습니다.");
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
            return response;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null, e);
        }
    }
    
    public void sendOneAlimTalk(String alimMessage, String telno, String token) {
        logger.info("sendOneAlimTalk 메서드 시작");

        Map<String, Object> requestBody = new HashMap<>();
        String msgIdx = String.valueOf(System.currentTimeMillis());
        logger.info("\n메시지 인덱스(msgIdx) 생성: {}", msgIdx);

        requestBody.put("msgIdx", msgIdx);
        requestBody.put("countryCode",alimtalkProperties.getCountryCode());
        requestBody.put("recipient", telno);
        requestBody.put("senderKey", alimtalkProperties.getSenderKey());
        requestBody.put("message", alimMessage);
        requestBody.put("tmpltCode", alimtalkProperties.getTemplateCode());
        requestBody.put("resMethod", "PUSH");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("bt-token", token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        logger.info("\n알림톡 전송 요청 시작");
        try {
            ResponseEntity<String> response = restTemplate.exchange(alimtalkProperties.getSendUrl(), HttpMethod.POST, entity, String.class);
            logger.info("알림톡 전송 결과: {}", response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("알림톡 전송 성공: {}", response.getBody());
            } else {
                logger.info("알림톡 전송 오류: HTTP {}", response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ServerException("시스템 오류 : 알림톡 전송 실패", e);
        }
    }
}

// // 3. 알림톡 결과 조회 메서드
    // public Map<String, Object> getAlimResult(String[] msgIdxArr, String token) throws IOException {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.set("bt-token", token);

    //     HttpEntity<String> entity = new HttpEntity<>(headers);
    //     ResponseEntity<String> response = restTemplate.exchange(RESULT_URL, HttpMethod.GET, entity, String.class);

    //     Map<String, Object> resultMap = new HashMap<>();
    //     int successCount = 0;
    //     int failCount = 0;
    //     List<Map<String, String>> failDetails = new ArrayList<>();

    //     if (response.getStatusCode() == HttpStatus.OK) {
    //         JsonNode jsonNode = objectMapper.readTree(response.getBody());
    //         if ("1000".equals(jsonNode.path("responseCode").asText())) {
    //             JsonNode responseArray = jsonNode.path("response");
    //             for (JsonNode item : responseArray) {
    //                 String msgIdx = item.path("msgIdx").asText();
    //                 String resultCode = item.path("resultCode").asText();

    //                 boolean isSuccess = Arrays.asList(msgIdxArr).contains(msgIdx) && "1000".equals(resultCode);
    //                 if (isSuccess) {
    //                     successCount++;
    //                 } else {
    //                     failCount++;
    //                     Map<String, String> failDetail = new HashMap<>();
    //                     failDetail.put("msgIdx", msgIdx);
    //                     failDetail.put("reason", item.path("reason").asText());
    //                     failDetails.add(failDetail);
    //                 }
    //             }
    //             resultMap.put("successCount", successCount);
    //             resultMap.put("failCount", failCount);
    //             resultMap.put("failDetails", failDetails);
    //         } else {
    //             throw new RuntimeException("결과 조회 실패: " + jsonNode.path("msg").asText());
    //         }
    //     }
    //     return resultMap;
    // }
// OAuth 2.0 인증 Response Syntax
// {
//     "code": "200",
//     "result": {
//       "detail_code": "NRM00000",
//       "detail_message": "성공"
//     },
//     "access_token": "example-token",
//     "token_type": "bearer",
//     "expires_in": 863999
//   }
  