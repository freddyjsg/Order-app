package com.example.elbaeat.ui.login;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
@SuppressWarnings("serial")
public //With this annotation we are going to hide compiler warnings

class LoggedInUserView implements Serializable {
    private String displayName;
    private String sessionKey;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String sessionKey) {
        this.displayName = displayName;
        this.sessionKey = sessionKey;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getSessionKey() {
        return sessionKey;
    }
}