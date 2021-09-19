package com.inori.games.kalah.controller;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

public class HealthCheckController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "Kalah online at " + new Date() + "\n";
    }

}
