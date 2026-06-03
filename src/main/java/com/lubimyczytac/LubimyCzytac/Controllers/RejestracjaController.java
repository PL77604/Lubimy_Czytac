package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RejestracjaController {

    @Autowired
    private UserService userService;

    @GetMapping("/Rejestracja")
    public String showRejestracjaPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/";
        }
        return "rejestracja";
    }

    @PostMapping("/Rejestracja")
    public String processRejestracja(
            @RequestParam String email,
            @RequestParam String haslo,
            @RequestParam String powtorzHaslo,
            @RequestParam(defaultValue = "false") boolean rememberMe,
            Model model,
            HttpSession session,
            HttpServletResponse response) {

        if (!haslo.equals(powtorzHaslo)) {
            model.addAttribute("error", "Hasła nie są identyczne!");
            return "rejestracja";
        }

        if (haslo.length() < 4) {
            model.addAttribute("error", "Hasło musi mieć co najmniej 4 znaki!");
            return "rejestracja";
        }

        if (email == null || !email.contains("@")) {
            model.addAttribute("error", "Podaj poprawny adres e-mail!");
            return "rejestracja";
        }

        String username = email.substring(0, email.indexOf("@"));
        User newUser = userService.register(email, haslo, username);

        if (newUser == null) {
            model.addAttribute("error", "Użytkownik z tym adresem e-mail już istnieje!");
            return "rejestracja";
        }

        session.setAttribute("loggedUser", newUser);

        if (rememberMe) {
            userService.login(email, haslo, true);
            if (newUser.getRememberToken() != null) {
                Cookie cookie = new Cookie("rememberToken", newUser.getRememberToken());
                cookie.setMaxAge(30 * 24 * 60 * 60);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
        }

        return "redirect:/";
    }
}