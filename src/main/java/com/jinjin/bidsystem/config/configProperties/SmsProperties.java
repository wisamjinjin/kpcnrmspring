
/* 주요 클래스 설명 */
/* - SmsProperties:
       Spring Boot의 `@ConfigurationProperties`와 `@Configuration`을 활용하여
       CoolSMS 서비스 연동에 필요한 설정 정보를 관리 */

/* - 주요 필드:
       . apiKey: CoolSMS API 키
       . apiSecretKey: CoolSMS API 비밀 키
       . provider: CoolSMS 서비스 제공자 URL
       . sender: SMS 발신자 번호
       . expirationTimeInMinutes: 인증 코드 유효 기간 (분 단위) */

package com.jinjin.bidsystem.config.configProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "coolsms")
public class SmsProperties {

    private String apiKey;
    private String apiSecretKey;
    private String provider;
    private String sender;
    private int expirationTimeInMinutes;

    // Getter와 Setter
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecretKey() {
        return apiSecretKey;
    }

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
    }
    
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getExpirationTimeInMinutes() {
        return expirationTimeInMinutes;
    }

    public void setExpirationTimeInMinutes(int expirationTimeInMinutes) {
        this.expirationTimeInMinutes = expirationTimeInMinutes;
    }
}
