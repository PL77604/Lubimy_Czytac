package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KsiazkiController {
    @GetMapping("/Ksiazka")
    public String ksiazka() {
        return "ksiazka";
    }
}
