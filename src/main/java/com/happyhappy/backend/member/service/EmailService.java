package com.happyhappy.backend.member.service;

public interface EmailService {

    void sendCode(String email);

    boolean verifyCode(String email, String code);

    boolean isEmailVerified(String email);

}