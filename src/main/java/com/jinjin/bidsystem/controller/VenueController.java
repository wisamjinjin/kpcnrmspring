package com.jinjin.bidsystem.controller;

import com.jinjin.bidsystem.service.VenueService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venue")
public class VenueController {
@Autowired
    private VenueService venueService;

    // 특정 경기장 조회
    @GetMapping("/getbycode")
    public ResponseEntity<Map<String, Object>> getVenueByCode(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = venueService.getVenueByCode(params); 
        return ResponseEntity.ok(result); 
    }

    // 모든 경기장 조회
    @GetMapping("/getall")
    public ResponseEntity<List<Map<String, Object>>>getAllVenues(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> result = venueService.getAllVenues(params); 
        return ResponseEntity.ok(result); 
    }

    // 경기장 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addVenue(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = venueService.addVenue(request); 
        return ResponseEntity.ok(result);
    }

    // 경기장 수정
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateVenue(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = venueService.updateVenue(request); 
        return ResponseEntity.ok(result);
    }

    // 경기장 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteVenue(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = venueService.deleteVenue(request); 
        return ResponseEntity.ok(result);
    }
}
