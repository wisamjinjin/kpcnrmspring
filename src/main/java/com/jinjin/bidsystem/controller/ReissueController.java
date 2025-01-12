package com.jinjin.bidsystem.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jinjin.bidsystem.service.ReissueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {

    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService) {
        this.reissueService = reissueService;
    }

    //access토큰 재발급
    @PostMapping("/reissue-access-token")
    public ResponseEntity<?> reissueAccessToken(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request,
            HttpServletResponse response) {
        return reissueService.reissueAccessToken(requestBody, request, response);
    }
}
