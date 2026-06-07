package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EdytujController {
    @GetMapping("/Edytuj")
    public String edytuj(Model model) {
        return "edytuj";
    }
}
