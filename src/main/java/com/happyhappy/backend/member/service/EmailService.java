package com.happyhappy.backend.member.service;

public interface EmailService {

    void sendCode(String username);

    boolean verifyCode(String username, String code);

    boolean isUsernameVerified(String username);
}