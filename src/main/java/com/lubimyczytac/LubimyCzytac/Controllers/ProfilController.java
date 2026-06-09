package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.Book;
import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Models.UserBookHistory;
import com.lubimyczytac.LubimyCzytac.Repositories.BookLikeRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.CommentRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.UserBookHistoryRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.UserBookListRepository;
import com.lubimyczytac.LubimyCzytac.Services.BookService;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProfilController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserBookHistoryRepository userBookHistoryRepository;

    @Autowired
    private UserBookListRepository userBookListRepository;

    @GetMapping("/Profil")
    public String profil(Model model, HttpSession session, HttpServletResponse response) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        loggedUser = userService.getFreshUser(loggedUser);
        session.setAttribute("loggedUser", loggedUser);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        List<UserBookHistory> history = userBookHistoryRepository.findByUserIdOrderByLastViewedDesc(loggedUser.getId());
        List<Book> recentBooks = new ArrayList<>();
        for (int i = 0; i < Math.min(history.size(), 4); i++) {
            Book book = bookService.getBookById(history.get(i).getBookId());
            if (book != null) {
                book.setLikesCount(bookLikeRepository.countByBookId(book.getId()));
                book.setCommentsCount(commentRepository.countByBookId(book.getId()));
                recentBooks.add(book);
            }
        }

        List<Book> recommendedBooks = bookService.getAllBooks().stream()
                .sorted((b1, b2) -> {
                    int likes1 = bookLikeRepository.countByBookId(b1.getId());
                    int likes2 = bookLikeRepository.countByBookId(b2.getId());
                    return Integer.compare(likes2, likes1);
                })
                .limit(6)
                .collect(Collectors.toList());

        for (Book book : recommendedBooks) {
            book.setLikesCount(bookLikeRepository.countByBookId(book.getId()));
            book.setCommentsCount(commentRepository.countByBookId(book.getId()));
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("recentBooks", recentBooks);
        model.addAttribute("recommendedBooks", recommendedBooks);

        return "profil";
    }

    @GetMapping("/EdytujProfil")
    public String showEditForm(HttpSession session, Model model, HttpServletResponse response) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

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

    @GetMapping("/api/user/refresh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> refreshUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            User freshUser = userService.getFreshUser(loggedUser);
            session.setAttribute("loggedUser", freshUser);

            response.put("success", true);
            response.put("dodaneKsiazki", freshUser.getDodaneKsiazki());
            response.put("pobraneKsiazki", freshUser.getPobraneKsiazki());
            response.put("username", freshUser.getUsername());
            response.put("email", freshUser.getEmail());
            response.put("avatar", freshUser.getAvatar());
            response.put("description", freshUser.getDescription());
            response.put("isAdmin", freshUser.isAdmin());
        } else {
            response.put("success", false);
        }

        return ResponseEntity.ok(response);
    }
}