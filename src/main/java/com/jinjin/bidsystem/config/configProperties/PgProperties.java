/* 주요 클래스 설명 */
/* - PgProperties:
       Inicis 결제 연동을 위한 설정 정보를 관리 */

/* - 주요 필드:
       . params: 결제 요청 관련 파라미터
       . idcName: IDC 이름 관련 정보
       . returnUrls: 결제 후 반환 URL 정보
       . views: 뷰 파일 경로 정보
       . urls: 결제 API의 URL 정보 (카테고리별 분류) */

package com.jinjin.bidsystem.config.configProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "inicis")
public class PgProperties {
    private Map<String, String> params;
    private Map<String, String> idcName;
    private Map<String, String> returnUrls;
    private Map<String, String> views;
    private Map<String, Map<String, String>> urls;

    // Individual Getters for each field
    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getParam(String key) {
        return params != null ? params.get(key) : null;
    }

    public Map<String, String> getIdcName() {
        return idcName;
    }

    public void setIdcName(Map<String, String> idcName) {
        this.idcName = idcName;
    }

    public String getIdcNameByKey(String key) {
        return idcName != null ? idcName.get(key) : null;
    }

    public Map<String, String> getReturnUrls() {
        return returnUrls;
    }

    public void setReturnUrls(Map<String, String> returnUrls) {
        this.returnUrls = returnUrls;
    }

    public String getReturnUrl(String key) {
        return returnUrls != null ? returnUrls.get(key) : null;
    }

    public Map<String, String> getViews() {
        return views;
    }

    public void setViews(Map<String, String> views) {
        this.views = views;
    }

    public String getView(String key) {
        return views != null ? views.get(key) : null;
    }

    public Map<String, Map<String, String>> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, Map<String, String>> urls) {
        this.urls = urls;
    }

    public String getUrl(String category, String key) {
        if (urls == null) return null;
        Map<String, String> categoryUrls = urls.get(category);
        return categoryUrls != null ? categoryUrls.get(key) : null;
    }
}
