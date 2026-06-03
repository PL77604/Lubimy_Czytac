package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("loggedUser")
    public User addUserToModel(HttpSession session, HttpServletRequest request) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("rememberToken".equals(cookie.getName())) {
                        User user = userService.loginWithRememberToken(cookie.getValue());
                        if (user != null) {
                            session.setAttribute("loggedUser", user);
                            return user;
                        }
                    }
                }
            }
        }

        return loggedUser;
    }
}