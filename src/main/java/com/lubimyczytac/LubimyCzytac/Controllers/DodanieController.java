package com.lubimyczytac.LubimyCzytac.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DodanieController {

    @GetMapping("/DodanieKsiazek")
    public String dodanie(Model model) {
        return "dodanie";
    }
}
