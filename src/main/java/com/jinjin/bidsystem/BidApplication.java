package com.jinjin.bidsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BidApplication {

    private static final Logger logger = LoggerFactory.getLogger(BidApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BidApplication.class, args);
        logger.info("\n\n---------------------------- BidApplication started ----------------------------");
    }
}   