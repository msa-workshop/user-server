package com.example.userserver.users;

import java.time.ZonedDateTime;

public class PostActivity {
    private int userId;
    private ZonedDateTime lastUpdatedDatetime;
    private String lastUpdatedId;

    public int getUserId() {
        return userId;
    }

    public ZonedDateTime getLastUpdatedDatetime() {
        return lastUpdatedDatetime;
    }

    public String getLastUpdatedId() {
        return lastUpdatedId;
    }
}