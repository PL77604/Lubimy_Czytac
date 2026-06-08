package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InneController {
    @GetMapping("/Inne")
    public String inne() {
        return "inne";
    }
}
