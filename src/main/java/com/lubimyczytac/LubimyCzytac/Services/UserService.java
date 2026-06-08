package com.lubimyczytac.LubimyCzytac.Services;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

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
                    user = userRepository.save(user);
                }
                return user;
            }
        }
        return null;
    }

    private void createRememberToken(User user) {
        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        user.setRememberToken(token);
        user.setRememberTokenExpiry(LocalDateTime.now().plusDays(30)); // Token ważny 30 dni
    }

    public User loginWithRememberToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByRememberToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Sprawdź czy token nie wygasł
            if (user.getRememberTokenExpiry() != null &&
                    user.getRememberTokenExpiry().isAfter(LocalDateTime.now())) {
                return user;
            }

            // Token wygasł - wyczyść go
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

    // Metody dla edycji profilu
    public User updateUsername(User user, String newUsername) {
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(user.getUsername())) {
            user.setUsername(newUsername.trim());
            return userRepository.save(user);
        }
        return user;
    }

    public User updateAvatar(User user, MultipartFile avatarFile) throws Exception {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Plik musi być obrazem!");
            }

            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Plik nie może przekraczać 5MB!");
            }

            String oldAvatar = user.getAvatar();
            if (oldAvatar != null && !oldAvatar.equals("📷") && oldAvatar.startsWith("http")) {
                try {
                    cloudinaryService.deleteImage(oldAvatar);
                } catch (Exception e) {
                    System.err.println("Nie udało się usunąć starego avataru: " + e.getMessage());
                }
            }

            String newAvatarUrl = cloudinaryService.uploadAvatar(avatarFile, user.getId());
            user.setAvatar(newAvatarUrl);
            return userRepository.save(user);
        }
        return user;
    }

    public User removeAvatar(User user) {
        String oldAvatar = user.getAvatar();
        if (oldAvatar != null && !oldAvatar.equals("📷") && oldAvatar.startsWith("http")) {
            try {
                cloudinaryService.deleteImage(oldAvatar);
            } catch (Exception e) {
                System.err.println("Nie udało się usunąć avataru: " + e.getMessage());
            }
        }

        user.setAvatar("📷");
        return userRepository.save(user);
    }

    public User updateDescription(User user, String description) {
        if (description != null) {
            user.setDescription(description);
            return userRepository.save(user);
        }
        return user;
    }

    public User getFreshUser(User user) {
        return userRepository.findById(user.getId()).orElse(user);
    }
}