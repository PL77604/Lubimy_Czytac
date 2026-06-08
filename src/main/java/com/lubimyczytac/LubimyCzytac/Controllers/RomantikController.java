package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RomantikController {

    @GetMapping("/Romantyka")
    public String romantyka() {
        return "romantik";
    }
}
