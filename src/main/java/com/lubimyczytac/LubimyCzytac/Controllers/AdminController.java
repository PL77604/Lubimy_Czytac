package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        model.addAttribute("users", allUsers);
        model.addAttribute("loggedUser", loggedUser);
        return "admin-panel";
    }

    @PostMapping("/api/admin/make-admin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> makeAdmin(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnien do nadawania roli administratora!");
            return ResponseEntity.status(403).body(response);
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Uzytkownik o podanym emailu nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        if (user.isAdmin()) {
            response.put("success", false);
            response.put("message", "Ten uzytkownik jest juz administratorem!");
            return ResponseEntity.badRequest().body(response);
        }

        user.setRole("ADMIN");
        userService.updateUser(user);

        if (user.getId().equals(currentUser.getId())) {
            session.setAttribute("loggedUser", user);
        }

        response.put("success", true);
        response.put("message", "Uzytkownik " + user.getUsername() + " zostal administratorem!");
        response.put("userId", user.getId());
        response.put("isAdmin", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/admin/remove-admin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeAdmin(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnien!");
            return ResponseEntity.status(403).body(response);
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Uzytkownik nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        if (user.getId().equals(currentUser.getId())) {
            response.put("success", false);
            response.put("message", "Nie mozesz odebrac sobie wlasnych uprawnien administratora!");
            return ResponseEntity.badRequest().body(response);
        }

        user.setRole("USER");
        userService.updateUser(user);

        response.put("success", true);
        response.put("message", "Odebrano uprawnienia administratora uzytkownikowi " + user.getUsername());
        response.put("userId", user.getId());
        response.put("isAdmin", false);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/admin/delete-user/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnien!");
            return ResponseEntity.status(403).body(response);
        }

        if (currentUser.getId().equals(id)) {
            response.put("success", false);
            response.put("message", "Nie mozesz usunac samego siebie!");
            return ResponseEntity.badRequest().body(response);
        }

        User userToDelete = userService.findById(id).orElse(null);
        if (userToDelete == null) {
            response.put("success", false);
            response.put("message", "Uzytkownik nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        try {
            userService.deleteUser(id);
            response.put("success", true);
            response.put("message", "Uzytkownik " + userToDelete.getUsername() + " zostal usuniety!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Blad podczas usuwania uzytkownika: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }
}