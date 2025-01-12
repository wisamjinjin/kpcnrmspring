package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jinjin.bidsystem.mapper.SeatPriceMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/* 주요 메서드 설명 */
/* 1. getSeatPrices(Map<String, Object> params):
      - 좌석별 가격 정보를 조회하는 메서드
      - 주요 기능:
        1) `SeatPriceMapper`를 통해 DB에서 좌석 가격 데이터를 조회
        2) 조회된 데이터가 없으면 `NoDataException` 발생
        3) 조회 성공 시 데이터 리스트를 반환 */

/* 2. updateSeatPriceArray(Map<String, Object> params):
      - 여러 좌석의 가격 정보를 갱신하는 메서드
      - 주요 기능:
        1) `SeatPriceMapper`를 통해 좌석 가격 데이터를 업데이트
        2) 업데이트된 행의 개수를 반환
        3) 업데이트된 행이 없을 경우 `ZeroAffectedRowException` 발생 */

/* 3. deleteSeatPriceArray(Map<String, Object> params):
      - 여러 좌석의 가격 정보를 삭제하는 메서드
      - 주요 기능:
        1) `SeatPriceMapper`를 통해 좌석 가격 데이터를 삭제
        2) 삭제된 행의 개수를 반환
        3) 삭제된 행이 없을 경우 `ZeroAffectedRowException` 발생 */

@Service
public class SeatPriceService {

    @Autowired
    private SeatPriceMapper seatPriceMapper;

    // 좌석별 가격 조회
    public List<Map<String, Object>> getSeatPrices(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = seatPriceMapper.getSeatPrices(params);
            if (results == null || results.isEmpty()) {
                throw new NoDataException("좌석 정보가 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 갱신
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.updateSeatPriceArray(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRows + "개의 정보가 갱신되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;    
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 삭제
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.deleteSeatPriceArray(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRows + "개의 정보가 삭제되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e; 
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
