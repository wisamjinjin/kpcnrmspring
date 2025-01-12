package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jinjin.bidsystem.mapper.MatchMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;

/* 주요 메서드 설명 */
/* 1. getMatchBidStatus(Map<String, Object> params):
      - 특정 경기의 입찰 상태 정보를 조회
      - 상태 이름(bid_status_name)을 계산하여 응답에 추가 */

/* 2. getbidStatusName(Map<String, Object> results):
      - 경기의 입찰 개시 및 종료 일시를 기반으로 상태 코드 이름을 결정
      - 상태: "입찰 개시전", "입찰 진행중", "입찰 종료", "낙찰 완료" */

/* 3. getMatchById(Map<String, Object> params):
      - 특정 경기 정보를 조회 */

/* 4. getAllMatches(Map<String, Object> params):
      - 특정 venue 코드에 해당하는 모든 경기 정보를 조회 */

/* 5. getMyMatches(Map<String, Object> params):
      - 특정 사용자가 등록한 경기 정보를 조회 */

/* 6. getAllApprovedMatches(Map<String, Object> params):
      - 특정 venue 코드에 해당하는 승인된 모든 경기 정보를 조회 */

/* 7. addMatch(Map<String, Object> params):
      - 새로운 경기를 등록
      - 최대 match_no를 조회하여 새로운 경기 번호를 생성
      - 날짜 및 시간 파라미터를 검증 및 포맷 후 DB에 저장 */

/* 8. updateMatch(Map<String, Object> params):
      - 경기 정보를 수정
      - 날짜 및 시간 파라미터를 검증 및 포맷 후 DB를 업데이트 */

/* 9. approveMatch(Map<String, Object> params):
      - 특정 경기의 승인 상태를 업데이트 */
      
/* 10. deleteMatch(Map<String, Object> params):
      - 특정 경기 정보를 삭제 */


@Service
public class MatchService {

    @Autowired
    private MatchMapper matchMapper;
    
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    // 경기 비드 상태 조회
    public Map<String, Object> getMatchBidStatus(Map<String, Object> params) {
        try {
    
            // 매퍼 호출 및 결과 출력
            Map<String, Object> results = matchMapper.getMatchStatus(params);
    
            // 결과가 없을 경우 예외 발생
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }
    
            // bidStatusName 계산 및 출력
    
            // 입찰 상태를 계산하여 응답 생성 및 출력
            Map<String, String> bidStatus = getBidStatus(results); // 상태 이름과 코드 받기
            // 기존 results에 새로운 데이터를 추가하여 응답 생성
            Map<String, Object> response = new HashMap<>(results);
            response.put("bid_status_name", bidStatus.get("bidStatusName"));
            response.put("bid_status_code", bidStatus.get("bidStatusCode"));
            return response;
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null, e);
        }
    }
    
    // 입찰 개시 및 종료 일시로 입찰 상태 코드와 이름 생성하는 함수
public Map<String, String> getBidStatus(Map<String, Object> results) {
    String bidStatusName = "초기 상태"; // 기본값: 데이터가 없을 경우
    String bidStatusCode = "INIT"; // 기본 상태 코드

    // 'bid_open_datetime'과 'bid_close_datetime' 값 확인
    Timestamp openTimestamp = (Timestamp) results.get("bid_open_datetime");
    Timestamp closeTimestamp = (Timestamp) results.get("bid_close_datetime");

    // bid_open_status 가져오기
    String bidOpenStatus = (String) results.get("bid_open_status");

    // openTimestamp 또는 closeTimestamp가 null인 경우 처리
    if (openTimestamp == null || closeTimestamp == null) {
        bidStatusName = "데이터 없음"; // 날짜 정보 없음
        bidStatusCode = "NO_DATA";
    } else {
        // Timestamp를 LocalDateTime으로 변환
        LocalDateTime openDateTime = openTimestamp.toLocalDateTime();
        LocalDateTime closeDateTime = closeTimestamp.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기

        // bid_open_status가 'F'이면 낙찰 완료로 설정
        if ("F".equals(bidOpenStatus)) {
            bidStatusName = "낙찰 완료"; // 낙찰 완료 상태
            bidStatusCode = "AWARDED";
        } else {
            // 현재 시간과 비교하여 상태 코드와 이름 설정
            if (now.isBefore(openDateTime)) {
                bidStatusName = "입찰 개시전"; // 입찰 시작 전
                bidStatusCode = "BEFORE_OPEN";
            } else if (now.isAfter(closeDateTime)) {
                bidStatusName = "입찰 종료"; // 입찰 종료 후 낙찰 처리 전
                bidStatusCode = "CLOSED";
            } else {
                bidStatusName = "입찰 진행중"; // 입찰 진행 중
                bidStatusCode = "IN_PROGRESS";
            }
        }
    }

    // 결과를 Map에 담아 반환
    Map<String, String> statusMap = new HashMap<>();
    statusMap.put("bidStatusName", bidStatusName);
    statusMap.put("bidStatusCode", bidStatusCode);

    return statusMap;
}


    // 모든 경기 조회 (venucd에 해당되는)
    public List<Map<String, Object>> getAllMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = matchMapper.getAllMatches(params);
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
               // 각 경기 데이터에 대해 입찰 상태 정보 추가
                for (Map<String, Object> result : results) {
                    try {
                        // 입찰 상태 계산
                        Map<String, String> bidStatus = getBidStatus(result);
                        // 결과 데이터에 상태 정보 추가
                        result.put("bid_status_name", bidStatus.get("bidStatusName"));
                        result.put("bid_status_code", bidStatus.get("bidStatusCode"));
                    } catch (Exception e) {
                        // 특정 경기의 입찰 상태 계산 중 오류 발생 시 처리 (필요하면 로그 추가)
                        result.put("bid_status_name", "상태 없음");
                        result.put("bid_status_code", "NULL");
                    }
                }
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 본인이 등록한 경기 조회 (venucd에 해당되는)
    public List<Map<String, Object>> getMyMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = matchMapper.getMyMatches(params);
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
                // 각 경기 데이터에 대해 입찰 상태 정보 추가
                 for (Map<String, Object> result : results) {
                     try {
                         // 입찰 상태 계산
                         Map<String, String> bidStatus = getBidStatus(result);
                         // 결과 데이터에 상태 정보 추가
                         result.put("bid_status_name", bidStatus.get("bidStatusName"));
                         result.put("bid_status_code", bidStatus.get("bidStatusCode"));
                     } catch (Exception e) {
                         // 특정 경기의 입찰 상태 계산 중 오류 발생 시 처리 (필요하면 로그 추가)
                         result.put("bid_status_name", "상태 없음");
                         result.put("bid_status_code", "NULL");
                     }
                 }
                 return results;
             }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 모든 승인된 경기 조회 (사용자용, venucd에 해당되는)
    public List<Map<String, Object>> getAllApprovedMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = null;
            results =  matchMapper.getAllApprovedMatches(params);
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else  {
                // 각 경기 데이터에 대해 입찰 상태 정보 추가
                 for (Map<String, Object> result : results) {
                     try {
                         // 입찰 상태 계산
                         Map<String, String> bidStatus = getBidStatus(result);
                         // 결과 데이터에 상태 정보 추가
                         result.put("bid_status_name", bidStatus.get("bidStatusName"));
                         result.put("bid_status_code", bidStatus.get("bidStatusCode"));
                     } catch (Exception e) {
                         // 특정 경기의 입찰 상태 계산 중 오류 발생 시 처리 (필요하면 로그 추가)
                         result.put("bid_status_name", "상태 없음");
                         result.put("bid_status_code", "NULL");
                     }
                 }
                 return results;
             }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 일괄 입찰
    public Map<String, Object> batchBid(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.batchBid(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "작업이 성공적으로 수정되었습니다.");
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

    // 일괄 입찰 승인인
    public Map<String, Object> approveBatchBid(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.approveBatchBid(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "작업이 성공적으로 수정되었습니다.");
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


    // 경기 추가
    @Transactional
    public Map<String, Object> addMatch(Map<String, Object> params) {
        try {
            // 첫 번째 쿼리로 최대 match_no 값을 조회
            Integer maxMatchNo = matchMapper.getMaxMatchNo();

            // 조회된 값에 1을 더해서 새로운 match_no 생성
            int newMatchNo = (maxMatchNo == null) ? 1 : maxMatchNo + 1;

            // 두 번째 쿼리로 새 데이터를 삽입
            params.put("matchNumber", newMatchNo);
                        
            // date,혹은 time이 입력되지 않으면 null로 변환 iso를 sql을 위한 String으로
            params = utilService.validateAndFormatDateTime(params,
            "startDate", "startTime", "endTime", "bidOpenTime", "bidCloseTime", "payDue");

            int affectedRows = matchMapper.addMatch(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 등록되었습니다.");
                // response.put("newMatchNumber", newMatchNo);
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;
        } catch (ParseException e) {
            throw new BadRequestException("날짜 파라메터 형식 오류입니다.");
        } catch (DuplicateKeyException e) {                  //DUPKEY를 catch하기 위함
            throw new CustomDuplicateKeyException("중복된 정보입니다. 입력 내용을 확인하세요.");
        } catch (Exception e) {
            throw new DataAccessException("경기장 등록 중 오류가 발생하였습니다.",e);
        }
    }


    // 경기 수정
    public Map<String, Object> updateMatch(Map<String, Object> params) {
        try {
            params = utilService.validateAndFormatDateTime(params,
            "startDate", "startTime", "endTime", "bidOpenTime", "bidCloseTime", "payDue");
            int affectedRows = matchMapper.updateMatch(params);
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

    // 경기 승인
    public Map<String, Object> approveMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.approveMatch(params); 
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "승인 처리가 성공적으로 수행되었습니다.");
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

    // 경기 삭제
    public Map<String, Object> deleteMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.deleteMatch(params); 
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
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
