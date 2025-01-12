package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.mapper.VenueMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;
import org.springframework.dao.DuplicateKeyException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/* 주요 메서드 설명 */
/* - getVenueByCode(Map<String, Object> params):
       특정 경기장 정보를 코드 기반으로 조회
       조회된 결과가 없으면 `NotFoundException` 발생 */

/* - getAllVenues(Map<String, Object> params):
       모든 경기장 정보를 조회
       결과가 없으면 `NoDataException` 발생 */

/* - addVenue(Map<String, Object> params):
       새로운 경기장을 추가
       주요 기능:
         . 성공적으로 추가되면 확인 메시지를 반환
         . 중복된 정보가 있으면 `DuplicateKeyException` 발생
         . 추가 실패 시 `ZeroAffectedRowException` 발생 */

/* - updateVenue(Map<String, Object> params):
       기존 경기장 정보를 수정
       주요 기능:
         . 수정 성공 시 확인 메시지를 반환
         . 수정 실패 시 `NotFoundException` 발생 */

/* - deleteVenue(Map<String, Object> params):
       특정 경기장을 삭제
       주요 기능:
         . 삭제 성공 시 확인 메시지를 반환
         . 삭제 실패 시 `NotFoundException` 발생 */

@Service
public class VenueService {

    @Autowired
    private VenueMapper venueMapper;

    // 특정 경기장 조회
    public Map<String, Object> getVenueByCode(Map<String, Object> params) {
        try {
            Map<String, Object> results = venueMapper.getVenueByCode(params);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }
            return results;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 모든 경기장 조회
    public List<Map<String, Object>> getAllVenues(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = venueMapper.getAllVenues();
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            }
            return results;
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기장 추가
    public Map<String, Object> addVenue(Map<String, Object> params) {

        try {
            int affectedRows =  venueMapper.addVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 등록되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;

        } catch (DuplicateKeyException e) { 
            throw new CustomDuplicateKeyException("중복된 정보입니다. 입력 내용을 확인하세요.");
        } catch (Exception e) {
            throw new DataAccessException("경기장 등록 중 오류가 발생하였습니다.",e);
        }
    }

    // 경기장 수정
    public Map<String, Object> updateVenue(Map<String, Object> params) {
        try {
            int affectedRows =  venueMapper.updateVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "정보가 성공적으로 수정되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기장 삭제
    public Map<String, Object> deleteVenue(Map<String, Object> params){
        try {
            int affectedRows =  venueMapper.deleteVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 삭제되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
