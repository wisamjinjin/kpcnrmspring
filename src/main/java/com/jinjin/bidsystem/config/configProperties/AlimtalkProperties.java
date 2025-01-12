
/* 주요 클래스 설명 */
/* - AlimtalkProperties:
       - 카카오 알림톡 API와 통합하기 위한 설정 값을 관리
       - `@ConfigurationProperties`를 통해 application.yml 파일에서 설정 값을 자동으로 주입받음
       - Spring의 `@Component`로 선언되어 DI 컨테이너에 빈으로 등록 */

/* 주요 필드 설명 */
/* - tokenUrl:
       카카오 알림톡 API의 인증 토큰 요청 URL
   - sendUrl:
       카카오 알림톡 API의 알림톡 전송 요청 URL
   - resultUrl:
       카카오 알림톡 API의 전송 결과 확인 URL
   - bsid:
       API 인증을 위한 비즈니스 ID
   - passwd:
       API 인증을 위한 비밀번호
   - senderKey:
       알림톡 발송에 사용되는 고유 발신자 키
   - templateCode:
       알림톡 발송 시 사용하는 템플릿 코드
   - countryCode:
       수신자의 국가 코드
*/

package com.jinjin.bidsystem.config.configProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alimtalk")
public class AlimtalkProperties {

    private String tokenUrl;
    private String sendUrl;
    private String resultUrl;
    private String bsid;
    private String passwd;
    private String senderKey;
    private String templateCode;
    private String countryCode;

    // Getter와 Setter 생성
    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public String getBsid() {
        return bsid;
    }

    public void setBsid(String bsid) {
        this.bsid = bsid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
