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
public class LogowanieController {

    @Autowired
    private UserService userService;

    @GetMapping("/Logowanie")
    public String showLogowaniePage(Model model, HttpSession session,
                                    @CookieValue(value = "rememberToken", required = false) String rememberToken) {

        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/";
        }

        if (rememberToken != null && !rememberToken.isEmpty()) {
            User user = userService.loginWithRememberToken(rememberToken);
            if (user != null) {
                session.setAttribute("loggedUser", user);
                return "redirect:/";
            }
        }

        return "logowanie";
    }

    // Obsługa formularza logowania
    @PostMapping("/Logowanie")
    public String processLogowanie(
            @RequestParam String email,
            @RequestParam String haslo,
            @RequestParam(defaultValue = "false") boolean rememberMe,
            Model model,
            HttpSession session,
            HttpServletResponse response) {

        User user = userService.login(email, haslo, rememberMe);

        if (user == null) {
            model.addAttribute("error", "Nieprawidłowy adres e-mail lub hasło!");
            return "logowanie";
        }

        session.setAttribute("loggedUser", user);

        if (rememberMe) {
            User freshUser = userService.getFreshUser(user);
            if (freshUser != null && freshUser.getRememberToken() != null) {
                Cookie cookie = new Cookie("rememberToken", freshUser.getRememberToken());
                cookie.setMaxAge(30 * 24 * 60 * 60); // 30 dni
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                response.addCookie(cookie);
            }
        }

        return "redirect:/";
    }

    @GetMapping("/Wyloguj")
    public String logout(HttpSession session, HttpServletResponse response,
                         @CookieValue(value = "rememberToken", required = false) String rememberToken) {

        if (rememberToken != null && !rememberToken.isEmpty()) {
            User user = userService.loginWithRememberToken(rememberToken);
            if (user != null) {
                userService.clearRememberToken(user);
            }
        }

        Cookie cookie = new Cookie("rememberToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        session.invalidate();

        return "redirect:/";
    }
}