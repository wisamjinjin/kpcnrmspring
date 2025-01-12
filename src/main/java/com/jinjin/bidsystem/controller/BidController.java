package com.jinjin.bidsystem.controller;
import com.jinjin.bidsystem.service.BidService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/bid")
public class BidController {
    @Autowired
    private BidService bidService;

    // 좌석 배열에 따른 입찰 정보를 가져오는 API
    @PostMapping("/get-by-seatarray")
    public ResponseEntity<List<Map<String, Object>>> getBidsBySeatArray(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getBidsBySeatArray(params); 
        return ResponseEntity.ok(results); 
    }

    // 사용자(telno)의 모든 입찰 내역을 조회하는 API
    @GetMapping("/get-mybids")
    public ResponseEntity<List<Map<String, Object>>> getMyBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getMyBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 사용자(telno)의 최고 입찰 내역을 조회하는 API
    @GetMapping("/get-mylastbids")
    public ResponseEntity<List<Map<String, Object>>> getMyLastBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getMyLastBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 좌석별 입찰 집계 데이터를 조회하는 API
    @GetMapping("/get-bid-tallies")
    public ResponseEntity<List<Map<String, Object>>> getBidTallies(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getBidTallies(params); 
        return ResponseEntity.ok(results); 
    }

    // 좌석별 최고 입찰 정보를 조회하는 API
    @GetMapping("/get-highest-bids")
    public ResponseEntity<List<Map<String, Object>>> getHighestBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getHighestBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 모든 입찰 데이터를 조회하는 API
    @GetMapping("/get-all-bids")
    public ResponseEntity<List<Map<String, Object>>> getAllBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getAllBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 낙찰 처리를 수행하는 API
    @PostMapping("/award")
    public ResponseEntity<Map<String, Object>> awardBids(@RequestBody Map<String, Object> request) {
        Map<String, Object> results = bidService.awardBids(request); 
        return ResponseEntity.ok(results); 
    }

    // 사용자가 입찰을 제출하는 API
    @PostMapping("/submit")
    public ResponseEntity<List<Map<String, Object>>> submitBids(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> results = bidService.submitBids(request); 
        return ResponseEntity.ok(results); 
    }
}
