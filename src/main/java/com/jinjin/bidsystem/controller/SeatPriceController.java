package com.jinjin.bidsystem.controller;

import com.jinjin.bidsystem.service.SeatPriceService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seatprice")
public class SeatPriceController {
@Autowired
    private SeatPriceService seatPriceService;

    // 특정 경기의 좌석 가격을 조회
    @GetMapping("/getall")
    public ResponseEntity<List<Map<String, Object>>> getSeatPrices(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = seatPriceService.getSeatPrices(params); // 서비스에서 데이터 반환
        return ResponseEntity.ok(results); // 데이터를 ResponseEntity로 감싸서 반환
    }

    // 좌석 가격을 배열로 업데이트
    @PostMapping("/updatearray")
    public ResponseEntity<Map<String, Object>>  updateSeatPriceArray(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = seatPriceService.updateSeatPriceArray(params); // 서비스에서 배열 업데이트 처리
        return ResponseEntity.ok(result); 
    }

    // 좌석 가격 배열 삭제
    @PostMapping("/deletearray")
    public ResponseEntity<Map<String, Object>> deleteSeatPriceArray(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = seatPriceService.deleteSeatPriceArray(params); // 서비스에서 배열 삭제 처리
        return ResponseEntity.ok(result); 
    }
}
