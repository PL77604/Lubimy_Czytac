package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HorrorController {
    @GetMapping("/Horrory")
    public String horrory() {
        return "horrorksiazka";
    }
}
