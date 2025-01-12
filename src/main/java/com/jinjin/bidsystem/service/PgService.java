package com.jinjin.bidsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjin.bidsystem.config.configProperties.PgProperties;
import com.jinjin.bidsystem.mapper.BidMapper;
import com.jinjin.bidsystem.mapper.PaymentMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/* 주요 메서드 설명 */
/* 1. pgStart(Map<String, Object> request):
      - 사용자 결제 요청을 처리하는 메서드
      - 주요 기능:
        1) 전화번호 유효성 검사 및 사용자 정보 조회
        2) 고유한 거래 ID(oid) 생성
        3) 결제 요청 데이터를 구성하여 `payments` 테이블에 기록
        4) 입찰 데이터에 거래 ID를 기록(bids 테이블 업데이트)
        5) 결제 요청 데이터를 반환하여 결제 창 호출 */

/* 2. pgReturn(String request):
      - 결제 요청에 대한 응답 데이터를 처리하고, 승인 요청을 진행하는 메서드
      - 주요 기능:
        1) 결제 응답 데이터를 URL 파라미터로부터 추출
        2) 응답 상태(resultCode)가 성공("0000")인지 확인
        3) 결제 승인 요청을 위해 인증 토큰(authToken)과 서명(signature) 생성
        4) 승인 요청 API 호출 및 결과 저장
        5) 승인 결과를 `payments` 테이블에 기록 */

/* 기타:
/* - pgUtil 및 utilService:
      - 결제 요청 서명 생성, URL 파라미터 변환 등의 유틸리티 기능을 제공 */
/* - PgProperties:
      - 결제 서비스 설정(예: MID, 반환 URL, 서명 키)을 관리 */
/* - BidMapper 및 PaymentMapper:
      - 입찰 및 결제정도 데이터베이스 작업 수행 */


@Service
public class PgService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 

    @Autowired
    private PgUtil pgUtil;

    @Autowired
    private UtilService utilService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private PaymentMapper paymentMapper;
    
    private final PgProperties pgProperties;

    @Autowired
    public PgService(PgProperties pgProperties) {
        this.pgProperties = pgProperties;
    }

    
    // 결제 요청
    public ModelAndView pgStart(Map<String, Object> request) {
        try {
            // 전화번호 유효성 검사
            pgUtil.validateRequestParameters(request);
            //결제요청에 필요한 사용자 DB정보 요청
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            Map<String, Object> userInfo = userService.getUserByQuery(request);
            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("전화번호로 사용자 정보를 찾을 수 없습니다.");
            }

            //oid를 MID와 timestamp로 unique하게 구성
            String timestamp = Long.toString(System.currentTimeMillis());
            String oid = pgProperties.getParam("mid") + "_" + timestamp;
            
            // 요청전문 구성
            ModelAndView payRequest = new ModelAndView();
            payRequest.addObject("price", request.get("price"));          //client request정보에서
            payRequest.addObject("goodName", request.get("goodName"));    //client request정보에서 
            payRequest.addObject("buyerName", userInfo.get("username"));  //사용자 DB정보
            payRequest.addObject("buyerTel", userInfo.get("telno"));      //사용자 DB정보
            payRequest.addObject("buyerEmail", userInfo.get("email"));    //사용자 DB정보
            payRequest.addObject("returnUrl", pgProperties.getReturnUrl("return"));     //property
            payRequest.addObject("closeUrl", pgProperties.getReturnUrl("close"));       //property
            payRequest.addObject("mid", pgProperties.getParam("mid"));                  //property
            payRequest.addObject("signKey", pgProperties.getParam("signKey"));          //property
            payRequest.addObject("timestamp", timestamp);
            payRequest.addObject("use_chkfake", pgProperties.getParam("use_chkfake"));  //property
            payRequest.addObject("oid", oid);
            payRequest.addObject("mKey", PgUtil.sha256(pgProperties.getParam("signKey")));  //property
            payRequest.addObject("signature", pgUtil.generateSignature(request.get("price"), oid, timestamp));
            payRequest.addObject("verification", pgUtil.generateVerification(request.get("price"), oid, timestamp));
            payRequest.setViewName(pgProperties.getView("request"));
            Map<String, Object> modelMap = payRequest.getModel();

            // 결제 요청 정보를 payments table 에 기록                          
            paymentMapper.savePcRequest(modelMap);
            
            // bids table oid 에 기록 
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("telno", request.get("telno"));
            updateParams.put("matchNumber", request.get("matchNumber"));
            updateParams.put("oid", oid);
            int affectedRows = bidMapper.updateBidOid(updateParams);
            if (affectedRows == 0) {
                throw new ZeroAffectedRowException("입찰내용에 거래아이디 세팅오류");
            }
            
            // 결제창 호출을 위한 JSP화면 호출        
            return payRequest;

        } catch (Exception e) {
            throw new PgException("시스템 오류 : 결제 요청중 오류가 발생하였습니다.", e);
        }
        
    }
    
    //결제요청 응답 수신 및 승인 요청
    public ModelAndView pgReturn(String request) {

        // URL 인코딩된 문자열을 Map<String, String>으로 변환
        Map<String, String> params;
        try{
            params = utilService.parseQueryString(request);      
        } catch (Exception e) {
            throw new UnsupportedEncodingException("Error in parging querysting in pgReturn", e);
        }

        // key-value 형태로 출력 : Debugging용
        // for (Map.Entry<String, String> entry : params.entrySet()) {
        // logger.info("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        // }

        // 결제요청 응답의  "resultCode"가 0000일 아닐 경우(오류인 경우) 처리
        if (!"0000".equals(params.get("resultCode"))) {
            logger.error("\n\n------------- pgreturn failed. " + params+"\n");
            
            // ModelAndView 생성 및 데이터 추가
            ModelAndView approveData = new ModelAndView();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                approveData.addObject(entry.getKey(), entry.getValue());
            }
            utilService.printModelAndView(approveData);
            approveData.setViewName(pgProperties.getView("return")); 
            return approveData;
        }
        
        // 결제요청 응답의 "resultCode"가 0000일 경우 처리(요청 성공)
        // 승인 요청 옵션 설정
        String mid = params.get("mid");                     // 상점아이디
        String authToken = params.get("authToken");         // 승인요청 검증 토큰
        String netCancelUrl = params.get("netCancelUrl");   // 망취소요청 URL
        String merchantData = params.get("merchantData");
        long timestamp = System.currentTimeMillis();        // 타임스탬프 [TimeInMillis(Long형)]
        String charset = "UTF-8";                           // 리턴 형식 [UTF-8, EUC-KR]
        String format = "JSON";                             // 리턴 형식 [XML, JSON, NVP]

        // 승인 요청 API URL 설정
        String idc_name = params.get("idc_name");
        String authUrl = params.get("authUrl");

        //== inicis에서 제공한 샘플 코드는 property에서 가져오지만 오류가 발생하여 함수로 변경함 ==  
        String authUrl2 = pgProperties.getUrl("auth",idc_name);

        logger.info("\n\n------------------- pgReturnt authUrl 값: " + authUrl + " pgreturnpost authUrl2 값 : " + authUrl2 + "\n");
        String signature = null;
        String verification = null;
        try {
            // SHA256 해시값 생성 [대상: authToken, timestamp]
            signature = pgUtil.createSignature(authToken, timestamp);

            // SHA256 해시값 생성 [대상: authToken, signKey, timestamp]
            verification = pgUtil.createVerification(authToken, timestamp);
        } catch (NoSuchAlgorithmException | java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Cannot fine SHA-256 algorithm.",e);
        }

        // 결제 승인 요청 옵션 설정
        Map<String, Object> options = new HashMap<>();
        options.put("mid", mid);
        options.put("authToken", authToken);
        options.put("timestamp", timestamp);
        options.put("signature", signature);
        options.put("verification", verification);
        options.put("charset", charset);
        options.put("format", format);
        try{
            //----------------------- 승인 요청 하기
            String urlEncodedOptions = utilService.convertToUrlEncodedString(options);
            if (!authUrl.equals(authUrl2)) {
                return pgUtil.handleNetCancel(netCancelUrl, idc_name, options);
            }

            // 승인요청을 위해 HttpClient 생성 및 POST 요청 전송
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestToInicis = HttpRequest.newBuilder()
                    .uri(URI.create(authUrl2))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(urlEncodedOptions.toString()))
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(requestToInicis, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
   
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> approveData = objectMapper.readValue(responseBody, Map.class);

            // 승인 결과 데이터베이스에 저장
            paymentMapper.savePCApproval(approveData);

            // Debugging 용    
            // pgUtil.printMap(approveData);

            ModelAndView errorView = new ModelAndView("Pc approval results");
            String resultCode = (String) approveData.get("resultCode");

            // 승인결과 코드가 0000이 아닌 경우 오류 페이지로 리다이렉트
            if (!"0000".equals(resultCode)) {
                String errorMsg = (String) approveData.get("resultMsg");
                errorView.addObject("resultCode", resultCode);
                errorView.addObject("errorMessage", errorMsg);
                errorView.setViewName(pgProperties.getView("error")); 
                return errorView;
            }

            // 승인 요청 성공 후  회신 데이터를 jsp용 modelAndView에 저장
            ModelAndView modelAndView = new ModelAndView("paymentResultPage"); // 원하는 페이지 이름
            for (Map.Entry<String, Object> entry : approveData.entrySet()) {
                modelAndView.addObject(entry.getKey(), entry.getValue());
            }

            // 입찰결과에 결제 승인 내용 기록
            try {
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("oid", approveData.get("MOID"));
                int affectedRows = bidMapper.updateBidPayment(updateParams);
                if (affectedRows == 0) {
                    logger.error("\n\n++ 승인은 완료되었으며, 입찰정보에 갱신 중 오류가 발생하였습니다.");
                    modelAndView.addObject("errorMsg", "승인 되었습니다.  완료 처리 중 오류가 발생하였습니다. 관리자에게 문의하세요.");
                    modelAndView.setViewName(pgProperties.getView("error")); 
                    return modelAndView;
                }
                
            } catch (Exception e) {
                // 예외 처리 로직
                String errorMessageSub = e.getMessage();
                logger.error("\n\n++ 승인은 완료되었으며, 입찰정보에 승인 정보를 갱신하는 중 오류가 발생하였습니다."+ errorMessageSub, e);
                modelAndView.addObject("errorMsg", "승인 되었습니다.  완료 처리 중 오류가 발생하였습니다. 관리자에게 문의하세요.");
                modelAndView.setViewName(pgProperties.getView("error")); 
                return modelAndView;
            }

            //jsp view name
            modelAndView.setViewName(pgProperties.getView("return")); 
       
            // jsp용 modelAndView pring(debug용)
            // pgUtil.printModelAndView(modelAndView);   
            return modelAndView;

        } catch (Exception e) {
            ModelAndView errorAndView = new ModelAndView();
            logger.error("\n\n------------------- Error in pgReturn " , e);
            errorAndView.addObject("errorMessage", "승인요청중 오류가 발생하였습니다.");
            errorAndView.setViewName(pgProperties.getView("error"));
            return errorAndView;
        }
    }  
    public String pgStartPost(Map<String, Object> request, HttpServletRequest request2, HttpServletResponse response) {
   
        try {
            // 전화번호 유효성 검사
            // pgUtil.validateRequestParameters(request);
            // 결제요청에 필요한 사용자 DB정보 요청
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            Map<String, Object> userInfo = userService.getUserByQuery(request);
        
            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("전화번호로 사용자 정보를 찾을 수 없습니다.");
            }
        
            // oid를 MID와 timestamp로 unique하게 구성
            String timestamp = Long.toString(System.currentTimeMillis());
            String oid = pgProperties.getReturnUrl("mid") + "_" + timestamp;
        
            // 요청전문 구성
            ModelAndView payRequest = new ModelAndView();
            payRequest.addObject("price", request.get("price"));
            payRequest.addObject("goodName", request.get("goodName"));
            payRequest.addObject("buyerName", userInfo.get("username"));
            payRequest.addObject("buyerTel", userInfo.get("telno"));
            payRequest.addObject("buyerEmail", userInfo.get("email"));
            payRequest.addObject("returnUrl", pgProperties.getReturnUrl("return"));
            payRequest.addObject("closeUrl", pgProperties.getReturnUrl("close"));
    
            payRequest.addObject("mid", pgProperties.getParam("mid"));
            payRequest.addObject("signKey", pgProperties.getParam("signKey"));
            payRequest.addObject("timestamp", timestamp);
            payRequest.addObject("use_chkfake", pgProperties.getParam("use_chkfake"));
            payRequest.addObject("oid", oid);
            payRequest.addObject("mKey", PgUtil.sha256(pgProperties.getParam("signKey")));
            payRequest.addObject("signature", pgUtil.generateSignature(request.get("price"), oid, timestamp));
            payRequest.addObject("verification", pgUtil.generateVerification(request.get("price"), oid, timestamp));
            payRequest.setViewName(pgProperties.getView("request"));
        
            Map<String, Object> modelMap = payRequest.getModel();
        
            // 결제 요청 정보를 payments table 에 기록                          
            paymentMapper.savePcRequest(modelMap);
        
            // bids table oid 에 기록 
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("telno", request.get("telno"));
            updateParams.put("matchNumber", request.get("matchNumber"));
            updateParams.put("oid", oid);
            int affectedRows = bidMapper.updateBidOid(updateParams);
        
            // 결제창 호출을 위한 JSP화면 호출        
            RequestDispatcher dispatcher = request2.getRequestDispatcher("/WEB-INF/views/" + payRequest.getViewName() + ".jsp");
            dispatcher.forward(request2, response);
            return "success";
        
        } catch (Exception e) {
            throw new PgException("시스템 오류 : 결제 요청중 오류가 발생하였습니다.", e);
        }
} 

}
// ---------------------------pgreturn 결제 요청 회신결과
// Key: cp_yn, Value: 
// Key: charset, Value: UTF-8
// Key: orderNumber, Value: INIpayTest_1731929432040
// Key: authToken, Value: +yyiuysaqtzVAwvitOoA8MIvYA9dZREBMwKebgXr3pBShgu/Ny9TkYb+kQBbX192.....................
// Key: resultCode, Value: 0000
// Key: checkAckUrl, Value: https://stgstdpay.inicis.com/api/checkAck
// Key: netCancelUrl, Value: https://stgstdpay.inicis.com/api/netCancel
// Key: mid, Value: INIpayTest
// Key: idc_name, Value: stg
// Key: merchantData, Value:
// Key: resultMsg, Value: 성공
// Key: authUrl, Value: https://stgstdpay.inicis.com/api/payAuth
// Key: cardnum, Value:
// Key: cardUsePoint, Value:
// Key: returnUrl, Value:

// ---------------------------pgreturn 승인 요청 결과입니다.
// Key: CARD_Quota, Value: 00
// Key: CARD_ClEvent, Value:
// Key: CARD_CorpFlag, Value: 9
// Key: buyerTel, Value: 11111111111
// Key: parentEmail, Value:
// Key: applDate, Value: 20241117
// Key: buyerEmail, Value: ihls2oon@naver.com
// Key: OrgPrice, Value:
// Key: p_Sub, Value:
// Key: resultCode, Value: 0000
// Key: mid, Value: INIpayTest
// Key: CARD_UsePoint, Value:
// Key: CARD_Num, Value: *********
// Key: authSignature, Value: 35e157d083a19aa0616f734586a7132275088e102292577dd0b60e2431ff37ff
// Key: tid, Value: StdpayCARDINIpayTest20241117211120693165
// Key: EventCode, Value:
// Key: goodName, Value: 좌석입찰 총 2 건
// Key: TotPrice, Value: 2000
// Key: payMethod, Value: Card
// Key: CARD_MemberNum, Value:
// Key: MOID, Value: INIpayTest_1731845419536
// Key: CARD_Point, Value:
// Key: currency, Value: WON
// Key: CARD_PurchaseCode, Value:
// Key: CARD_PrtcCode, Value: 1
// Key: applTime, Value: 211121
// Key: goodsName, Value: 좌석입찰 총 2 건
// Key: CARD_CheckFlag, Value: 1
// Key: FlgNotiSendChk, Value:
// Key: CARD_Code, Value: 97
// Key: CARD_BankCode, Value: 97
// Key: CARD_TerminalNum, Value:
// Key: P_FN_NM, Value: 카카오머니
// Key: buyerName, Value: 까망이
// Key: p_SubCnt, Value:
// Key: applNum, Value:
// Key: resultMsg, Value: 정상처리되었습니다.
// Key: CARD_Interest, Value: 0
// Key: CARD_SrcCode, Value: O
// Key: CARD_ApplPrice, Value: 2000
// Key: CARD_GWCode, Value: K
// Key: custEmail, Value:
// Key: CARD_Expire, Value:
// Key: CARD_PurchaseName, Value: 카카오머니
// Key: CARD_PRTC_CODE, Value: 1
// Key: payDevice, Value: PC


