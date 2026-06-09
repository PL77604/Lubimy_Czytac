package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/panel")
    public String adminPanel(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !loggedUser.isAdmin()) {
            return "redirect:/";
        }

        List<User> allUsers = userService.getAllUsers();

        List<User> regularUsers = allUsers.stream()
                .filter(user -> !user.isAdmin())
                .collect(Collectors.toList());

        model.addAttribute("users", allUsers);
        model.addAttribute("regularUsers", regularUsers);
        model.addAttribute("loggedUser", loggedUser);
        return "admin-panel";
    }

    @PostMapping("/api/admin/make-admin")
    @ResponseBody
    public Map<String, Object> makeAdmin(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnien do nadawania roli administratora!");
            return response;
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Uzytkownik o podanym emailu nie istnieje!");
            return response;
        }

        if (user.isAdmin()) {
            response.put("success", false);
            response.put("message", "Ten uzytkownik jest juz administratorem!");
            return response;
        }

        user.setRole("ADMIN");
        userService.updateUser(user);

        response.put("success", true);
        response.put("message", "Uzytkownik " + user.getUsername() + " zostal administratorem!");
        return response;
    }

    @PostMapping("/api/admin/remove-admin")
    @ResponseBody
    public Map<String, Object> removeAdmin(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnien!");
            return response;
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Uzytkownik nie istnieje!");
            return response;
        }

        if (user.getId().equals(currentUser.getId())) {
            response.put("success", false);
            response.put("message", "Nie mozesz odebrac sobie wlasnych uprawnien administratora!");
            return response;
        }

        user.setRole("USER");
        userService.updateUser(user);

        response.put("success", true);
        response.put("message", "Odebrano uprawnienia administratora uzytkownikowi " + user.getUsername());
        return response;
    }
}