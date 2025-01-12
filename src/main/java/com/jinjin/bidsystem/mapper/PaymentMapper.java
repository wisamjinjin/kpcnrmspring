package com.jinjin.bidsystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

/**
 * 결제와 관련된 데이터베이스 작업을 처리하는 MyBatis Mapper 인터페이스
 * 모바일과 PC 환경에 따라 구분되어 데이터베이스 작업을 수행
 */
@Mapper
public interface PaymentMapper {

    /**
     * 모바일 결제 요청 데이터를 저장
     */
    void saveMobileRequest(Map<String, Object> paymentData);

    /**
     * 모바일 결제 승인 데이터를 저장
     */
    void saveMobileApproval(Map<String, Object> paymentData);

    /**
     * PC 결제 요청 데이터를 저장
     */
    void savePcRequest(Map<String, Object> paymentData);

    /**
     * PC 결제 승인 데이터를 저장
     */
    void savePCApproval(Map<String, Object> paymentData);
}
