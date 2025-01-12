package com.jinjin.bidsystem.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
import java.util.List;

@Mapper
public interface SeatPriceMapper {

    // 좌석별 가격 조회
    List< Map<String, Object>> getSeatPrices(Map<String, Object> params);

    // 좌석별 가격 입력 또는 갱신
    int updateSeatPrice(Map<String, Object> params);

    // 좌석배열로 업데이트/혹은 Insrt
    int updateSeatPriceArray(Map<String, Object> params);

    // 좌석배열로 삭제
    int deleteSeatPriceArray(Map<String, Object> params);
}
