package com.lubimyczytac.LubimyCzytac.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {
    @GetMapping("/Profil")
    public String profil() {
        return "profil";
    }
}
