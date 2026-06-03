package com.lubimyczytac.LubimyCzytac.Services;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String email, String password, String username) {
        if (userRepository.existsByEmail(email)) {
            return null;
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(email, hashedPassword, username);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword, boolean rememberMe) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                if (rememberMe) {
                    createRememberToken(user);
                }
                return user;
            }
        }
        return null;
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);
        return true;
    }

    private void createRememberToken(User user) {
        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        user.setRememberToken(token);
        user.setRememberTokenExpiry(LocalDateTime.now().plusDays(30));
        userRepository.save(user);
    }

    public User loginWithRememberToken(String token) {
        Optional<User> userOpt = userRepository.findByRememberToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRememberTokenExpiry() != null &&
                    user.getRememberTokenExpiry().isAfter(LocalDateTime.now())) {
                return user;
            }
            user.setRememberToken(null);
            user.setRememberTokenExpiry(null);
            userRepository.save(user);
        }
        return null;
    }

    public void clearRememberToken(User user) {
        user.setRememberToken(null);
        user.setRememberTokenExpiry(null);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateStatistics(Long userId, int dodaneDelta, int pobraneDelta) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setDodaneKsiazki(user.getDodaneKsiazki() + dodaneDelta);
            user.setPobraneKsiazki(user.getPobraneKsiazki() + pobraneDelta);
            userRepository.save(user);
        });
    }
}