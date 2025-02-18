
package com.stepx.stepx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class GeneralController { //todas las solicitudes "/...." son con el controlador

    @GetMapping("/index")
    public String showIndex(Model model) {
        return "index";  // nombre de la plantilla Mustache sin la extensión .html
    }

    @GetMapping("/shop")
    public String showShop(Model model) {
        return "shop";
    }
    

}
