package com.jinjin.bidsystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;
import java.util.List;

@Mapper
public interface VenueMapper {

    // 특정 경기장 조회
    Map<String, Object> getVenueByCode(Map<String, Object> params);

    // 모든 경기장 조회
    List<Map<String, Object>> getAllVenues();

    // 경기장 추가
    int addVenue(Map<String, Object> params);

    // 경기장 수정
    int updateVenue(Map<String, Object> params);

    // 경기장 삭제
    int deleteVenue(Map<String, Object> params);
}
