package com.lubimyczytac.LubimyCzytac.Config;

import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdminUsers();
    }

    private void createAdminUsers() {
        if (!userRepository.existsByEmail("PL77604@lubimyczytac.pl")) {
            User PL66504 = new User();
            PL66504.setEmail("PL77604@lubimyczytac.pl");
            PL66504.setPassword(passwordEncoder.encode("0962241408@Aa"));
            PL66504.setUsername("Arsen - PL77604");
            PL66504.setRole("ADMIN");
            PL66504.setAktywny(true);
            PL66504.setDodaneKsiazki(0);
            PL66504.setPobraneKsiazki(0);
            userRepository.save(PL66504);
        }

        if (!userRepository.existsByEmail("PL77316@lubimyczytac.pl")) {
            User PL77316 = new User();
            PL77316.setEmail("PL77316@lubimyczytac.pl");
            PL77316.setPassword(passwordEncoder.encode("Starcraft_009"));
            PL77316.setUsername("Oleksandr - PL77316");
            PL77316.setRole("ADMIN");
            PL77316.setAktywny(true);
            PL77316.setDodaneKsiazki(0);
            PL77316.setPobraneKsiazki(0);
            userRepository.save(PL77316);
        }

        System.out.println("Aktualni administratorzy:");
        userRepository.findAll().stream()
                .filter(User::isAdmin)
                .forEach(admin -> System.out.println("   - " + admin.getEmail() + " (" + admin.getUsername() + ")"));
    }
}