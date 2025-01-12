package com.jinjin.bidsystem.controller;
import com.jinjin.bidsystem.service.MatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    private  MatchService matchService;

    // 입찰 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMatchBidStatus(@RequestParam Map<String, Object> params) {
        Map<String, Object> results = matchService.getMatchBidStatus(params); 
        return ResponseEntity.ok(results); 
    }

    // 모든 경기 조회
    @GetMapping("/getall")
    public ResponseEntity<List<Map<String, Object>>> getAllMatches(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = matchService.getAllMatches(params); 
        return ResponseEntity.ok(results); 
    }

    // 특정 사용자가 등록한 경기 조회(대회 운영자)
    @GetMapping("/getmy")
    public ResponseEntity<List<Map<String, Object>>> getMyMatches(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = matchService.getMyMatches(params); 
        return ResponseEntity.ok(results); 
    }

    // 입찰 가능한 승인된 경기 조회(일반 사용자 용)
    @GetMapping("/getallapproved")
    public ResponseEntity<List<Map<String, Object>>> getAllApprovedMatches(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = matchService.getAllApprovedMatches(params); 
        return ResponseEntity.ok(results); 
    }

    // 경기 일괄 입찰 등록
    @PostMapping("/batch-bid-submit")
    public ResponseEntity<Map<String, Object>> batchBid(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.batchBid(request); 
        return ResponseEntity.ok(results); 
    }
    
    // 경기 일괄 입찰 승인
    @PostMapping("/batch-bid-approve")
    public ResponseEntity<Map<String, Object>> approveBatchBid(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.approveBatchBid(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.addMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 수정
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.updateMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 승인
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.approveMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.deleteMatch(request); 
        return ResponseEntity.ok(results); 
    }
}
