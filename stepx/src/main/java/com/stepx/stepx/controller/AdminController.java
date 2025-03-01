package com.stepx.stepx.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.UserService;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AdminController {

    @Autowired
    private ShoeService shoeService;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        BigDecimal totalEarnings = shoeService.getTotalEarnings();
        model.addAttribute("totalEarnings", totalEarnings);
        return "admin-pannel"; // Nombre de tu archivo HTML (admin.html)
    }
}