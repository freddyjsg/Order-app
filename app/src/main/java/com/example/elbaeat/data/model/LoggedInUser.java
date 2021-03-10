package com.example.elbaeat.data.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

public class LoggedInUser{

    //private String userId;
    private String displayName;
    private String sessionKey;

    public LoggedInUser(String displayName, String sessionKey) {
        //this.userId = userId;
        this.displayName = displayName;
        this.sessionKey = sessionKey;
    }

    //public String getUserId() { return userId; }

    public String getDisplayName() {
        return displayName;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}