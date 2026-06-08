package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KryminalController {
    @GetMapping("/Kryminal")
    public String kryminal() {
        return "kryminal";
    }
}
