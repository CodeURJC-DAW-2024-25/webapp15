package com.stepx.stepx.security.jwt;

import java.time.Duration;

public enum TokenType {

    ACCESS(Duration.ofMinutes(50), "AuthToken"),
    REFRESH(Duration.ofDays(7), "RefreshToken");

    public final Duration duration;
    public final String cookieName;

    TokenType(Duration duration, String cookieName) {
        this.duration = duration;
        this.cookieName = cookieName;
    }
}