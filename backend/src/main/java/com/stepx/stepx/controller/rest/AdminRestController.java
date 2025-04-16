package com.stepx.stepx.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.service.OrderShoesService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    @Autowired
    private OrderShoesService orderShoesService;


    @GetMapping("/order-count-chart")
    public ResponseEntity<Map<String, Object>> getOrderCountChart() {
        Map<String, Object> chartData = orderShoesService.generateOrderCountChartData();
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/money-gained-chart")
    public ResponseEntity<Map<String, Object>> getMoneyGainedChart() {
        Map<String, Object> chartData = orderShoesService.generateMoneyGainedChartData();
        return ResponseEntity.ok(chartData);
    }
}