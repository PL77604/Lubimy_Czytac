package com.lubimyczytac.LubimyCzytac.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KatalogController {

    @GetMapping("/Katalog")
    public String katalog(Model model) {
        return "katalog";
    }
}
