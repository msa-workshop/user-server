package com.example.userserver.users;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String username;
    private String email;
    private String password;
    private ZonedDateTime lastPostDatetime;
    private String lastPostId;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ZonedDateTime getLastPostDatetime() {
        return lastPostDatetime;
    }

    public String getLastPostId() {
        return lastPostId;
    }

    public void setLastPostDatetime(ZonedDateTime lastPostDatetime) {
        this.lastPostDatetime = lastPostDatetime;
    }

    public void setLastPostId(String lastPostId) {
        this.lastPostId = lastPostId;
    }
}
