package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainKsiazkiController {
    @GetMapping("/MainKsiazki")
    public String mainKsiazki() {
        return "mainksiazki";
    }
}
