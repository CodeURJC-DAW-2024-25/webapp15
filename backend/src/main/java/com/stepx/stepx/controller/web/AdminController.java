package com.stepx.stepx.controller.web;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.stepx.stepx.service.OrderShoesService;

import org.springframework.ui.Model;

@Controller
public class AdminController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderShoesService orderShoesService;


 // In AdminRestController.java
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

@GetMapping("/admin")
public String adminDashboard(Model model) throws JsonProcessingException {

    // Get dashboard stats from service
    Map<String, Object> stats = orderShoesService.getAdminDashboardStats();
    
    // Add stats to model
    model.addAttribute("userCount", stats.get("userCount"));
    model.addAttribute("shoeCount", stats.get("shoeCount"));
    model.addAttribute("processedOrderCount", stats.get("processedOrderCount"));
    model.addAttribute("totalMoneyGained", stats.get("totalMoneyGained"));

    // Get order count chart data from service
    Map<String, Object> orderChartData = orderShoesService.generateOrderCountChartData();
    String chartDataJson = objectMapper.writeValueAsString(orderChartData);
    model.addAttribute("data", "var shoeOrderData = " + chartDataJson + ";");
    
    // Get money gained chart data from service
    Map<String, Object> moneyChartData = orderShoesService.generateMoneyGainedChartData();
    String moneyChartDataJson = objectMapper.writeValueAsString(moneyChartData);
    model.addAttribute("moneyData", "var moneyGainedData = " + moneyChartDataJson + ";");
    
    return "admin-pannel";
}
}
