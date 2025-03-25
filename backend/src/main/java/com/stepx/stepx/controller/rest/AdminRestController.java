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
    public ResponseEntity<Map<String, Integer>> getOrderCountChart() {
        // Get order counts by month from repository
        List<Map<String, Object>> orderCounts = orderShoesService.getOrderCountsByMonth();

        // Create a map for the chart data
        Map<String, Integer> chartData = new HashMap<>();

        // Initialize with default zero values
        String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        for (String month : monthNames) {
            chartData.put(month, 0);
        }

        // Fill in the actual data
        for (Map<String, Object> entry : orderCounts) {
            String monthStr = (String) entry.get("month");
            Long count = ((Number) entry.get("orders_count")).longValue();

            // Debugging: Log the raw month and count
            System.out.println("Processing month: " + monthStr + ", count: " + count);

            // Convert month string to zero-based index (01 -> 0, 02 -> 1, etc.)
            int monthIndex = Integer.parseInt(monthStr) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                String monthName = monthNames[monthIndex];
                chartData.put(monthName, count.intValue());
            } else {
                System.out.println("Invalid month index: " + monthIndex + " for month string: " + monthStr);
            }
        }

        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/money-gained-chart")
    public ResponseEntity<Map<String, Double>> getMoneyGainedChart() {
        // Get money gained by month
        List<Map<String, Object>> moneyGained = orderShoesService.getMoneyGainedByMonth();

        // Create a map for the money data
        Map<String, Double> moneyChartData = new HashMap<>();

        // Initialize with default zero values
        String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        for (String month : monthNames) {
            moneyChartData.put(month, 0.0);
        }

        // Fill in the actual money data
        for (Map<String, Object> entry : moneyGained) {
            String monthStr = (String) entry.get("month");
            Number amount = (Number) entry.get("total_money");
            Double totalMoney = amount != null ? amount.doubleValue() : 0.0;

            // Debugging: Log the raw month and amount
            System.out.println("Processing month: " + monthStr + ", amount: " + totalMoney);

            // Convert month string to zero-based index
            int monthIndex = Integer.parseInt(monthStr) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                String monthName = monthNames[monthIndex];
                moneyChartData.put(monthName, totalMoney);
            } else {
                System.out.println("Invalid month index: " + monthIndex + " for month string: " + monthStr);
            }
        }

        return ResponseEntity.ok(moneyChartData);
    }
}