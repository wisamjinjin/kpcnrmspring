/* 주요 클래스 설명 */
/* - JwtProperties:
       jwt설정 */
/* 주요 필드 설명 */
/* - accessTokenExpiration:
       Access Token의 유효 기간(밀리초 단위)
   - refreshTokenExpiration:
       Refresh Token의 유효 기간(밀리초 단위)
   - cookieMaxAge:
       쿠키의 유효 기간(초 단위)
*/

package com.jinjin.bidsystem.config.configProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
    private Integer cookieMaxAge;

    // Getter 및 Setter
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }
}
