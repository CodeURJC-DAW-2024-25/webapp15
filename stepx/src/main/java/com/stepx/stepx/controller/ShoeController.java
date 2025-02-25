package com.stepx.stepx.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Shoe;

import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;

public class ShoeController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShoeService shoeService;

    
    
}
