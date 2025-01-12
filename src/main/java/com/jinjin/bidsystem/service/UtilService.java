package com.jinjin.bidsystem.service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 주요 메서드 설명 */
/* 주요 메서드 설명 */
/* - convertQueryStringToJson(String queryString):
       URL-encoded 쿼리 문자열을 JSON 형식으로 변환
       주로 결제 요청 처리 시 유용하게 사용 */

/* - convertToUrlEncodedString(Map<String, Object> data):
       Map 데이터를 URL-encoded 쿼리 문자열로 변환
       POST 요청 전송 시 유용 */

/* - parseQueryString(String queryString):
       URL-encoded 쿼리 문자열을 Map으로 변환
       GET 요청의 파라미터 처리에 유용 */

/* - convertJsonToMap(String jsonString):
       JSON 문자열을 Map으로 변환
       JSON 응답 처리 시 유용 */

/* - printModelAndView(ModelAndView modelAndView):
       `ModelAndView` 객체의 내용을 출력 (디버깅 용도) */

/* - printMap(Map<String, Object> modelMap):
       Map의 내용을 출력 (디버깅 용도) */

/* - isValidDateTime(String value):
       DATETIME 값의 유효성을 검증 (ISO, DATE_TIME, SQL 형식 지원) */

/* - isValidDate(String value):
       DATE 값의 유효성을 검증 */

/* - isValidTime(String value):
       TIME 값의 유효성을 검증 */

/* - toSqlStandardFormat(String value):
       ISO 또는 DATE_TIME 형식을 SQL 표준 형식으로 변환
       형식 변환 실패 시 `ParseException` 발생 */

/* - validateAndFormatDateTime(Map<String, Object> params, String... keys):
       Map의 특정 키에 대해 DATE_TIME, DATE, TIME 값을 유효성 검증 및 포맷 변환
       잘못된 값은 null로 설정 */

@Service
public class UtilService {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 

    // 쿼리 문자열을 JSON 형식으로 변환
    public String convertQueryStringToJson(String queryString) {
        try {
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // 쿼리 스트링을 JSON 객체로 변환
            Map<String, String> jsonMap = new HashMap<>();
            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : ""; // 값이 없을 경우 빈 문자열 사용
                jsonMap.put(key, value);
            }

            // JSON으로 변환
            return objectMapper.writeValueAsString(jsonMap);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Map<String, Object> 데이터를 URL-encoded 쿼리 문자열로 변환
    public String convertToUrlEncodedString(Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            try {
                if (result.length() > 0) {
                    result.append("&");
                }
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                result.append(key).append("=").append(value);
            } catch (java.io.UnsupportedEncodingException e) {
                logger.error("URL 인코딩 중 오류 발생: {}", e.getMessage());
            }
        }
        return result.toString();
    }

    // URL 인코딩된 쿼리 문자열을 Map으로 변환
    public Map<String, String> parseQueryString(String queryString) throws java.io.UnsupportedEncodingException {
        Map<String, String> resultMap = new HashMap<>();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
            resultMap.put(key, value);
        }
        return resultMap;
    }

    // JSON 문자열을 Map으로 변환
    public Map<String, String> convertJsonToMap(String jsonString) {
        try {
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열을 Map 객체로 변환
            return objectMapper.readValue(jsonString, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ModelAndView 객체 내용을 출력  : 디버깅 용도임
    public void printModelAndView(ModelAndView modelAndView) {
        logger.info("View Name: " + modelAndView.getViewName());
        logger.info("Model Contents:");

        for (Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
            logger.info("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // Map 내용을 출력 : 디버깅 용도임
    public void printMap(Map<String, Object> modelMap) {
        if (modelMap == null || modelMap.isEmpty()) {
            logger.info("Map is empty or null.");
            return;
        }

        for (Entry<String, Object> entry : modelMap.entrySet()) {
            logger.info("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // DATETIME 유효성 검증
    private boolean isValidDateTime(String value) {
        return isValidFormat(value, DATE_TIME_FORMAT) || isValidFormat(value, ISO_FORMAT) || isValidFormat(value, SQL_FORMAT);
    }

    // DATE 유효성 검증
    private boolean isValidDate(String value) {
        return isValidFormat(value, DATE_FORMAT);
    }

    // TIME 유효성 검증
    private  boolean isValidTime(String value) {
        return isValidFormat(value, TIME_FORMAT);
    }

    // 포맷별 유효성 검증 메서드
    private  boolean isValidFormat(String value, SimpleDateFormat format) {
        try {
            format.setLenient(false); // 엄격한 검증
            format.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // ISO 또는 DATE_TIME 형식을 SQL 표준 형식으로 변환
    public  String toSqlStandardFormat(String value) throws ParseException {
        if (isValidFormat(value, ISO_FORMAT)) {
            return SQL_FORMAT.format(ISO_FORMAT.parse(value));
        } else if (isValidFormat(value, DATE_TIME_FORMAT)) {
            return SQL_FORMAT.format(DATE_TIME_FORMAT.parse(value));
        }
        throw new ParseException("Invalid datetime format: " + value, 0);
    }

    // 메인 메서드
    public  Map<String, Object> validateAndFormatDateTime(Map<String, Object> params, String... keys) throws ParseException {
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }
        for (String key : keys) {
            Object value = params.get(key);


            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                // 값이 null이거나 빈 문자열이면 null로 설정
                params.put(key, null);
            } else if (value instanceof String) {
                // 문자열 값 처리
                String stringValue = (String) value;
                if (isValidDateTime(stringValue)) {
                    String convertedValue = toSqlStandardFormat(stringValue);
                    params.put(key, convertedValue);
                } else if (isValidDate(stringValue)) {
                } else if (isValidTime(stringValue)) {
                } else {
                    params.put(key, null);
                }
            } else if (value instanceof LocalDate) {
                // LocalDate 값 처리
                try {
                    LocalDate date = (LocalDate) value; // 유효한 LocalDate 객체
                } catch (DateTimeParseException e) {
                    params.put(key, null);
                }
            } else if (value instanceof LocalTime) {
                // LocalTime 값 처리
                try {
                    LocalTime time = (LocalTime) value; // 유효한 LocalTime 객체
                } catch (DateTimeParseException e) {
                    params.put(key, null);
                }
            }
        }
        // 최종 변환된 Map 출력
        return params;
    }
}


