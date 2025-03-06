package com.stepx.stepx.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.UserService;
import com.stepx.stepx.repository.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;



    @Controller
    public class AdminController {
    
        @Autowired
        private ObjectMapper objectMapper;
        
        @Autowired
        private OrderShoesRepository orderShoesRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private ShoeRepository shoeRepository;
      
      
        @GetMapping("/admin")
        public String adminDashboard(Model model) throws JsonProcessingException {
        
            // Get total number of users
        long userCount = userRepository.count();
        model.addAttribute("userCount", userCount);

        // Get total number of shoes
        long shoeCount = shoeRepository.count();
        model.addAttribute("shoeCount", shoeCount);

        // Get total number of processed orders
        long processedOrderCount = orderShoesRepository.countProcessedOrders();
        model.addAttribute("processedOrderCount", processedOrderCount);


        // Get total money gained from processed orders
        BigDecimal totalMoneyGained = orderShoesRepository.getTotalMoneyGained();
        model.addAttribute("totalMoneyGained", totalMoneyGained);




        // Get order counts by month from repository
        List<Map<String, Object>> orderCounts = orderShoesRepository.getOrderCountsByMonth();
        
        // Convert to a map for the chart
        Map<String, Object> chartData = new HashMap<>();
        
        // Arrays for labels and data
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Integer[] orderData = new Integer[12];
        
        // Initialize with zeros
        for (int i = 0; i < 12; i++) {
            orderData[i] = 0;
        }
        
        // Fill in the actual data
        for (Map<String, Object> entry : orderCounts) {
            String monthStr = (String) entry.get("month");
            Long count = ((Number) entry.get("orders_count")).longValue();
            
            // Convert month string to zero-based index (01 -> 0, 02 -> 1, etc.)
            int monthIndex = Integer.parseInt(monthStr) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                orderData[monthIndex] = count.intValue();
            }
        }
        
        chartData.put("labels", monthNames);
        chartData.put("data", orderData);
        
        String chartDataJson = objectMapper.writeValueAsString(chartData);
        model.addAttribute("data", "var shoeOrderData = " + chartDataJson + ";");
        
        // Get money gained by month
        List<Map<String, Object>> moneyGained = orderShoesRepository.getMoneyGainedByMonth();
        
        // Create a new map for money data
        Map<String, Object> moneyChartData = new HashMap<>();
        Double[] moneyData = new Double[12];
        
        // Initialize with zeros
        for (int i = 0; i < 12; i++) {
            moneyData[i] = 0.0;
        }
        
        // Fill in the actual money data
        for (Map<String, Object> entry : moneyGained) {
            String monthStr = (String) entry.get("month");
            Number amount = (Number) entry.get("total_money");
            Double totalMoney = amount != null ? amount.doubleValue() : 0.0;
            
            // Convert month string to zero-based index
            int monthIndex = Integer.parseInt(monthStr) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                moneyData[monthIndex] = totalMoney;
            }
        }
        
        moneyChartData.put("labels", monthNames);
        moneyChartData.put("data", moneyData);
        
        String moneyChartDataJson = objectMapper.writeValueAsString(moneyChartData);
        model.addAttribute("moneyData", "var moneyGainedData = " + moneyChartDataJson + ";");
        
        return "admin-pannel"; // This matches your HTML file name
    }
    }
