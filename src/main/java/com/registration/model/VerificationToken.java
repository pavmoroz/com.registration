package com.registration.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String token;

    @Column
    private LocalDateTime expirationTime;


    public VerificationToken() {
        expirationTime = LocalDateTime.now().plusHours(3);
    }

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        expirationTime = LocalDateTime.now().plusHours(3);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void updateToken(final String token) {
        this.token = token;
        expirationTime = LocalDateTime.now().plusHours(3);
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", expiretime=" + expirationTime +
                '}' + "isValid -> " + !expirationTime.isBefore(LocalDateTime.now());
    }
}