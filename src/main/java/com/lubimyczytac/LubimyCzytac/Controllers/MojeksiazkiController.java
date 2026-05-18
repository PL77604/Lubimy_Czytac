package com.lubimyczytac.LubimyCzytac.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MojeksiazkiController {

    @GetMapping("/MojeKsiazki")
    public String mojeksiazki(Model model) {
        return "mojeksiazki";
    }
}
