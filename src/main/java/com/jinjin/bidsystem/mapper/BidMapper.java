
package com.jinjin.bidsystem.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BidMapper {
    // 좌석 배열로 입찰 내역 조회
    List<Map<String, Object>> getBidsBySeatArray(Map<String, Object> params);

    // 사용자ID로 입찰 내역 조회
    List<Map<String, Object>> getMyBids(Map<String, Object> params);

    // 사용자ID로 마지막 입찰 내역 조회
    List<Map<String, Object>> getMyLastBids(Map<String, Object> params);

    // 입찰 합산 정보 조회
    List<Map<String, Object>> getBidTallies(Map<String, Object> params);

    // 전체 입찰 내역 조회
    List<Map<String, Object>> getAllBids(Map<String, Object> params);

    // 최고 입찰 리스트 조회
    List<Map<String, Object>> getHighestBids(Map<String, Object> params);

    // 사용자ID로 낙찰 내용과 금액 조회
    List<Map<String, Object>> getAwardedBidsByMatch(Map<String, Object> params);

    // 좌석에 대해 최대 입찰 금액 조회(입찰 최저가 산정용)
    Map<String, Object> getMaxBidAmount(Map<String, Object> params);

    // 입찰 제출
    int submitBid(Map<String, Object> params);

    // 낙찰 처리
    int awardBids(Map<String, Object> params);

    // 낙찰건에 결제 요청 oid 기록
    int updateBidOid(Map<String, Object> params);

    // 지불 후 입찰 내역에 지불 정보 갱신
    int updateBidPayment(Map<String, Object> params);

    // 입찰 기록 삭제
    int deleteAllBids(Map<String, Object> params);

}
