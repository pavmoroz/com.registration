package com.registration.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    private String roles = "";

    private String authorities = "";

    public User(String username,String email, String password, String roles, String authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.authorities = authorities;
        this.enabled = false;
    }

    public User(String username,String email, String password, String roles, String authorities, boolean enabled) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    protected User(){
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoleList(){
        if(roles.length() > 0){
            return Arrays.asList(roles.split(","));
        }
        return new ArrayList<>();
    }

    public List<String> getAuthoritiesList(){
        if(authorities.length() > 0){
            return Arrays.asList(roles.split(","));
        }
        return new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", roles='" + roles + '\'' +
                ", authorities='" + authorities + '\'' +
                '}';
    }
}