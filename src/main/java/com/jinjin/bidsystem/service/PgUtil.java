package com.jinjin.bidsystem.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.jinjin.bidsystem.config.configProperties.PgProperties;
import com.jinjin.bidsystem.service.ExceptionService.*;

/* 주요 메서드 설명 */
/* 1. PgUtil 생성자:
      - `PgProperties` 객체를 초기화하여 결제 서비스 관련 설정을 로드 */

/* 2. getParam(String key):
      - 주어진 키(key)에 해당하는 결제 서비스 파라미터 값을 반환 */

/* 3. getIdcName(String key):
      - IDC 이름과 관련된 설정 값을 반환 */

/* 4. getReturnUrl(String key):
      - 반환 URL 설정 값을 반환 */

/* 5. getView(String key):
      - JSP 뷰 이름을 반환 */

/* 6. getUrl(String category, String key):
      - 주어진 카테고리와 키(key)에 해당하는 URL 값을 반환 */

/* 7. validateRequestParameters(Map<String, Object> request):
      - 요청 파라미터에서 필수 값(예: 전화번호)이 있는지 검증 */

/* 8. generateSignature(Object price, String oid, String timestamp):
      - 가격, 거래 ID, 타임스탬프를 기반으로 SHA-256 서명을 생성 */

/* 9. generateVerification(Object price, String oid, String timestamp):
      - 가격, 거래 ID, 서명 키, 타임스탬프를 기반으로 검증 서명을 생성 */

/* 10. handleException(Exception e):
      - 결제 요청 중 발생한 예외를 처리하고 오류 페이지를 반환 */

/* 11. createSignature(String authToken, long timestamp):
      - 인증 토큰과 타임스탬프를 기반으로 SHA-256 서명을 생성 */

/* 12. createVerification(String authToken, long timestamp):
      - 인증 토큰, 서명 키, 타임스탬프를 기반으로 검증 서명을 생성 */

/* 13. handleNetCancel(String netCancelUrl, String idcName, Map<String, Object> options):
      - 네트워크 취소 요청을 처리하고 결과를 로깅 */
      
/* 14. sha256(String input):
      - 입력 문자열을 SHA-256 해시 값으로 변환 */


@Service
public class PgUtil {
    private static final Logger logger = LoggerFactory.getLogger(PgUtil.class);
    private final PgProperties pgProperties;
    public static final String UrlService = null;

    public PgUtil(PgProperties pgProperties) {
        this.pgProperties = pgProperties;
    }

    public String getParam(String key) {
        return pgProperties.getParams().get(key);
    }

    public String getIdcName(String key) {
        return pgProperties.getIdcName().get(key);
    }

    public String getReturnUrl(String key) {
        return pgProperties.getReturnUrls().get(key);
    }

    public String getView(String key) {
        return pgProperties.getViews().get(key);
    }

    public String getUrl(String category, String key) {
        Map<String, String> categoryUrls = pgProperties.getUrls().get(category);
        return categoryUrls != null ? categoryUrls.get(key) : null;
    }


    
    // 결제 요청 파라미터에서 전화번호가 있는지 확인
    public void validateRequestParameters(Map<String, Object> request) {
        if (!request.containsKey("telno")) {
            throw new BadRequestException("결제요청 파라메터에 전화번호가 필요합니다.");
        }
    }

    // SHA-256 해시 알고리즘을 사용해 서명 생성
    public String generateSignature(Object price, String oid, String timestamp) {
        return sha256("oid=" + oid + "&price=" + price + "&timestamp=" + timestamp);
    }

    // SHA-256 해시 알고리즘을 사용해 검증 서명 생성 
    public String generateVerification(Object price, String oid, String timestamp) {
        return sha256("oid=" + oid + "&price=" + price + "&signKey=" + pgProperties.getParam("signKey") + "&timestamp=" + timestamp);
    }
    
    // 결제 요청 시 발생한 예외 처리 및 오류 페이지 반환
    public ModelAndView handleException(Exception e) {
        ModelAndView modelAndView = new ModelAndView(pgProperties.getView("error"));
        if (e instanceof NotFoundException) {
            logger.error("++ 결제 사전 요청에서 오류가 발생하였습니다. (사용자 정보 조회 실패)", e);
        } else {
            logger.error("++ 결제 사전 요청에서 오류가 발생하였습니다.", e);
        }
        modelAndView.addObject("errorMessage", "결제 사전 요청에서 오류가 발생하였습니다");
        return modelAndView;
    }

    // 인증 서명 생성
    public String createSignature(String authToken, long timestamp) throws NoSuchAlgorithmException {
        return sha256("authToken=" + authToken + "&timestamp=" + timestamp);
    }

    // 검증 서명 생성
    public String createVerification(String authToken, long timestamp) throws NoSuchAlgorithmException {
        String data = "authToken=" + authToken + "&signKey=" + pgProperties.getParam("signKey") + "&timestamp=" + timestamp;
        return sha256(data);
    }

    // 네트워크 취소 요청 처리
    public ModelAndView handleNetCancel(String netCancelUrl, String idcName, Map<String, Object> options) {
        String netCancelUrl2 = getUrl("netCancel",idcName);
        if (netCancelUrl.equals(netCancelUrl2)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(netCancelUrl2, options, Map.class);
            logger.info("망취소 요청 결과: {}", response.getBody());
        }

        ModelAndView modelAndView = new ModelAndView(pgProperties.getView("erroe"));
        modelAndView.addObject("errorMessage", "handleNetCancel 하였습니다");
        return modelAndView;
    }

    // SHA-256 해시를 생성
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다", e);
        }
    }

}
