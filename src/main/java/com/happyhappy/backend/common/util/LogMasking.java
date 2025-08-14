package com.happyhappy.backend.common.util;

public class LogMasking {

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "(blank)";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***" + email.substring(Math.max(0, at));
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    public static String maskUserId(String id) {
        if (id == null || id.isBlank()) {
            return "(blank)";
        }
        if (id.length() <= 2) {
            return id.charAt(0) + "*";
        }
        return id.substring(0, 2) + "***";
    }

}
