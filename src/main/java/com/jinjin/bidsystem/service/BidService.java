package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jinjin.bidsystem.mapper.BidMapper;
import com.jinjin.bidsystem.mapper.MatchMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

/* 주요 메서드 설명 */
/* 1. getBidsBySeatArray(Map<String, Object> params):
      - 특정 좌석 배열에 대한 입찰 정보를 조회 */

/* 2. getMyBids(Map<String, Object> params):
      - 특정 사용자의 입찰 내역 조회 */

/* 3. getBidTallies(Map<String, Object> params):
      - 좌석별 입찰 통계 정보 조회 */

/* 4. getAllBids(Map<String, Object> params):
      - 전체 입찰 정보 조회 */

/* 5. getHighestBids(Map<String, Object> params):
      - 좌석별 최고 입찰 내역 조회 */

/* 6. getMyLastBids(Map<String, Object> params):
      - 특정 사용자의 좌석별 마지막 입찰 내역 조회 */

/* 7. submitBids(Map<String, Object> params):
      - 사용자로부터 전달받은 입찰 데이터를 처리하고, 각 좌석에 대해 성공 여부를 반환
      - DB 갱신시점 직전에 좌석별 최고 입찰 금액을 조회하여 금액액보다 낮은 입찰은 실패로 처리
      - 입찰 성공 시 데이터를 DB에 기록 */
      
/* 8. awardBids(Map<String, Object> params):
      - 특정 경기의 입찰을 낙찰 처리하고, 관련 상태를 업데이트
      - 경기의 낙찰 처리 상태 flag를를 업데이트
      - 낙찰된 입찰 건수 반환 */

@Service
public class BidService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 
    
    @Autowired
    private BidMapper bidMapper;


    @Autowired
    private MatchMapper matchMapper;

    public List<Map<String, Object>> getBidsBySeatArray(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getBidsBySeatArray(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    
    public List<Map<String, Object>> getMyBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getMyBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    public List<Map<String, Object>> getBidTallies(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getBidTallies(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    public List<Map<String, Object>> getAllBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getAllBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    public List<Map<String, Object>> getHighestBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>>  results =  bidMapper.getHighestBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    public List<Map<String, Object>> getMyLastBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getMyLastBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> submitBids(Map<String, Object> params) {
        List<Map<String, Object>> bidArray = (List<Map<String, Object>>) params.get("bidArray");
        String telno = (String) params.get("telno");
        String matchNumber = (String) params.get("matchNumber");

        List<Map<String, Object>> resultsArray = new ArrayList<>();

        if (bidArray == null || !(bidArray instanceof List)) {
            throw new BadRequestException(null);
        }
        
        //입찰된 좌석건별로 데이터  반복 수행
        int bidAmount = 0;
        for (Map<String, Object> bid : bidArray) {
            Object seatNoObj = bid.get("seatNo");
            String seatNo ="";
            if (seatNoObj instanceof String) {
                    seatNo = (String) seatNoObj; 
            } else if (seatNoObj instanceof Integer) {
                seatNo = Integer.toString((int) seatNoObj);
            } 

            Object bidAmountObj = bid.get("bidAmount");
            if (bidAmountObj instanceof String) {
                bidAmount = Integer.parseInt((String) bidAmountObj);
            } else if (bidAmountObj instanceof Integer) {
                bidAmount = (Integer) bidAmountObj;
            } else if (bidAmountObj instanceof Double) {
                bidAmount = ((Double) bidAmountObj).intValue();
            } 

            //입찰된 좌석건별로 반복 수행
            Map<String, Object> getparams = new HashMap<>();
            getparams.put("matchNumber", matchNumber);
            getparams.put("seatNo", seatNo);
            try {
                //해당 seatNo의 현재 시점의 최고 입찰금액을 조회
                Map<String, Object> results = bidMapper.getMaxBidAmount(getparams);
                int maxBidAmount =0;
                if (results != null) {
                    Object maxBidAmountObject = results.get("max_bid_amount");
                    if (maxBidAmountObject instanceof String) {
                        try {
                            maxBidAmount = Integer.parseInt((String) maxBidAmountObject);
                        } catch (NumberFormatException e) {
                            throw new NumberFormatException();
                        }
                    } else if (maxBidAmountObject instanceof Integer) {
                        maxBidAmount = (Integer) maxBidAmountObject;
                    } else if (bidAmountObj instanceof Double) {
                        maxBidAmount = ((Double) maxBidAmountObject).intValue();
                    } else {
                        throw new NumberFormatException();
                    }
                }
                
                //해당 seatNo의 현재 시점의 최고 입찰금액보다 금액이 크지 않으면 등록 실패, 아니면 성공으로 반환
                if (bidAmount <= maxBidAmount) {
                    Map<String, Object> resultEach = new HashMap<>();
                    resultEach.put("status", "fail");
                    resultEach.put("seat_no", seatNo);
                    resultEach.put("message", "등록 실패: 입찰액이 현재 최대 입찰액보다 작습니다.");
                    resultsArray.add(resultEach);
                    continue;
                }

                Map<String, Object> bidParams = new HashMap<>();
                bidParams.put("bidAt", new Timestamp(System.currentTimeMillis()));
                bidParams.put("telno", telno);
                bidParams.put("matchNumber", matchNumber);
                bidParams.put("seatNo", seatNo);
                bidParams.put("bidAmount", bidAmount);

                bidMapper.submitBid(bidParams);

                Map<String, Object> resultEach = new HashMap<>();
                resultEach.put("status", "success");
                resultEach.put("seat_no", seatNo);
                resultEach.put("message", "등록 성공");
                resultsArray.add(resultEach);

            } catch (NumberFormatException | BadRequestException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(null,e);
            }
        }

        return resultsArray;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> awardBids(Map<String, Object> params) {
        try {

            int affectedRowsStatus = matchMapper.updateMatchAwardStatus(params);  // updateBidStatus 호출 (낙찰 처리 flag =F' set)
            if (affectedRowsStatus == 0) {
                throw new NotFoundException(null);
            }
            int affectedRowsBids = bidMapper.awardBids(params);  // awardBids 호출

            if (affectedRowsBids > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRowsBids+"건이 성공적으로 낙찰 처리되었습니다.");
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
