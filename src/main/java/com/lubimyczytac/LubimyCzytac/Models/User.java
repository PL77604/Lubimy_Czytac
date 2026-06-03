package com.lubimyczytac.LubimyCzytac.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String username;

    @Column(length = 255)
    private String avatar;

    @Column(name = "dodane_ksiazki")
    private Integer dodaneKsiazki;

    @Column(name = "pobrane_ksiazki")
    private Integer pobraneKsiazki;

    @Column(name = "data_rejestracji")
    private LocalDateTime dataRejestracji;

    @Column(name = "aktywny")
    private boolean aktywny;

    private String rememberToken;
    private LocalDateTime rememberTokenExpiry;

    public User() {
        this.dodaneKsiazki = 0;
        this.pobraneKsiazki = 0;
        this.aktywny = true;
        this.dataRejestracji = LocalDateTime.now();
    }

    public User(String email, String password, String username) {
        this();
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getRememberToken() { return rememberToken; }
    public void setRememberToken(String rememberToken) { this.rememberToken = rememberToken; }

    public LocalDateTime getRememberTokenExpiry() { return rememberTokenExpiry; }
    public void setRememberTokenExpiry(LocalDateTime rememberTokenExpiry) { this.rememberTokenExpiry = rememberTokenExpiry; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatar() { return avatar != null ? avatar : "📷"; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getDodaneKsiazki() { return dodaneKsiazki != null ? dodaneKsiazki : 0; }
    public void setDodaneKsiazki(Integer dodaneKsiazki) { this.dodaneKsiazki = dodaneKsiazki; }

    public Integer getPobraneKsiazki() { return pobraneKsiazki != null ? pobraneKsiazki : 0; }
    public void setPobraneKsiazki(Integer pobraneKsiazki) { this.pobraneKsiazki = pobraneKsiazki; }

    public LocalDateTime getDataRejestracji() { return dataRejestracji; }
    public void setDataRejestracji(LocalDateTime dataRejestracji) { this.dataRejestracji = dataRejestracji; }

    public boolean isAktywny() { return aktywny; }
    public void setAktywny(boolean aktywny) { this.aktywny = aktywny; }
}