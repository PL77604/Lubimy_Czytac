package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfilController {

    @Autowired
    private UserService userService;

    @GetMapping("/Profil")
    public String showProfil(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        User freshUser = userService.getFreshUser(loggedUser);
        session.setAttribute("loggedUser", freshUser);
        model.addAttribute("loggedUser", freshUser);

        return "profil";
    }

    @GetMapping("/EdytujProfil")
    public String showEditForm(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        model.addAttribute("loggedUser", loggedUser);
        return "edytuj";
    }

    @PostMapping("/EdytujProfil")
    public String editProfile(@RequestParam(value = "nazwa", required = false) String username,
                              @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                              @RequestParam(value = "opis", required = false) String description,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        boolean hasChanges = false;

        try {
            if (username != null && !username.trim().isEmpty() && !username.equals(loggedUser.getUsername())) {
                loggedUser = userService.updateUsername(loggedUser, username);
                redirectAttributes.addFlashAttribute("success", "Nazwa użytkownika została zmieniona!");
                hasChanges = true;
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                loggedUser = userService.updateAvatar(loggedUser, avatarFile);
                redirectAttributes.addFlashAttribute("success", "Zdjęcie profilowe zostało zmienione!");
                hasChanges = true;
            }

            if (description != null) {
                String currentDescription = loggedUser.getDescription() != null ? loggedUser.getDescription() : "";
                if (!description.equals(currentDescription)) {
                    loggedUser = userService.updateDescription(loggedUser, description);
                    if (!hasChanges) {
                        redirectAttributes.addFlashAttribute("success", "Opis profilu został zaktualizowany!");
                    }
                    hasChanges = true;
                }
            }

            if (!hasChanges) {
                redirectAttributes.addFlashAttribute("info", "Nie wprowadzono żadnych zmian.");
            }

            session.setAttribute("loggedUser", loggedUser);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/EdytujProfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Błąd podczas aktualizacji profilu: " + e.getMessage());
            return "redirect:/EdytujProfil";
        }

        return "redirect:/Profil";
    }

    @PostMapping("/UsunAvatar")
    public String removeAvatar(HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        try {
            loggedUser = userService.removeAvatar(loggedUser);
            session.setAttribute("loggedUser", loggedUser);
            redirectAttributes.addFlashAttribute("success", "Zdjęcie profilowe zostało usunięte!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Błąd podczas usuwania avataru: " + e.getMessage());
        }

        return "redirect:/Profil";
    }
}