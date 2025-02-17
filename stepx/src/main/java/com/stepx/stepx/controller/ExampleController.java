
package com.stepx.stepx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExampleController {

    @GetMapping("/example")
    public String example(Model model) {
        model.addAttribute("message", "Welcome to Spring Boot with Mustache!");
        return "index";  // nombre de la plantilla Mustache sin la extensión .html
    }
}
