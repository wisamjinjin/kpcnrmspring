package com.jinjin.bidsystem.controller;

import com.jinjin.bidsystem.config.configProperties.PgProperties;
import com.jinjin.bidsystem.service.PgService;
import com.jinjin.bidsystem.service.PgServiceMobile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class PgController {

@Autowired
    private PgService pgService;
@Autowired
    private PgServiceMobile pgServiceMobile;

    private final PgProperties pgProperties;

    @Autowired
    public PgController(PgProperties pgProperties) {
        this.pgProperties = pgProperties;
    }

    //결제 요청 : PC
    @GetMapping("/pgstart")
    public ModelAndView pgStartGet(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgService.pgStart(request);
        return pgstart;
    }

    //결제 요청에 대한 응답 및 승인 요청 : PC
    @GetMapping("/pgstart-mobile")
    public ModelAndView pgStartGetMobile(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgServiceMobile.pgStartMobile(request);
        return pgstart;
    }

    
    //결제 요청 : mobile
    @PostMapping("/pgreturn")
    public ModelAndView pgReturnPost(@RequestBody String request) {
        ModelAndView pgreturn = pgService.pgReturn(request);
        return pgreturn;
    }

    //결제 요청에 대한 응답 및 승인 요청 : mobile
    @PostMapping("/pgreturn-mobile")
    public ModelAndView pgReturnPostMobile(@RequestBody String request) {
        ModelAndView pgreturn = pgServiceMobile.pgReturnMobile(request);
        return pgreturn;
    }

    //결제 창 close시 리턴 주소
    @GetMapping("/pgclose")
    public ModelAndView closePage() {
        ModelAndView pgclose = new ModelAndView();
        pgclose.setViewName(pgProperties.getView("close"));
        return pgclose;
    }
    
        
}

