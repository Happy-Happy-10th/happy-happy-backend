package com.happyhappy.backend.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long EXPIRE_MINUTES = 5;
    private static final String EMAIL_CODE_KEY_PREFIX = "email:code:";
    private static final String EMAIL_VERIFIED_KEY_PREFIX = "email:verified:";

    @Override
    public void sendCode(String username) {
        String code = generateCode();

        // 로그 추가 위치 ⬇️
        log.info(" 이메일 전송 시도 중: {}", username);
        log.info(" 발송할 인증코드: {}", code);

        // Redis에 저장 (5분간 유효)
        redisTemplate.opsForValue().set(getKey(username), code, EXPIRE_MINUTES, TimeUnit.MINUTES);

        String subject = "[HappyHappy] 이메일 인증코드 안내";
        String content = String.format("""
        안녕하세요, [요때요]입니다.

        아래 6자리 인증 코드를 입력하면 이메일 인증이 완료됩니다.  
        ✔ 인증 코드: %s  
        ✔ 유효 시간: %d분

        ― 어떻게 해야 하나요?
        1) 요때요 로그인(또는 가입) 화면으로 돌아가기  
        2) ‘이메일 인증 코드’ 입력란에 위 숫자 6자리를 그대로 입력  
        3) ‘이메일 인증하기’ 버튼 누르기 → 인증 완료!

        ※ 요때요는 인공지능으로 일정 키워드만 입력하면 자동으로 맞춤형 캘린더를 생성합니다. 곧 더 편리한 일정 관리 경험을 만나보세요!

        혹시 내가 요청한 메일이 아니라면 무시해주세요.
        """, code, EXPIRE_MINUTES);


        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(username);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(message);

            // 전송 성공 시 로그
            log.info("이메일 전송 완료: {}", username);

        } catch (MessagingException e) {
            log.error("이메일 전송 실패", e);
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }


    @Override
    public boolean verifyCode(String username, String inputCode) {
        String key = EMAIL_CODE_KEY_PREFIX + username;
        String storedCode = redisTemplate.opsForValue().get(key);

        boolean isValid = storedCode != null && storedCode.equals(inputCode);
        if (isValid) {
            // 인증 성공 → 인증 완료 상태 저장 (1시간 유효)
            redisTemplate.opsForValue().set(EMAIL_VERIFIED_KEY_PREFIX + username, "true", 1, TimeUnit.HOURS);
            redisTemplate.delete(key);
        }

        return isValid;
    }

    @Override
    public boolean isUsernameVerified(String username) {
        String key = EMAIL_VERIFIED_KEY_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);

        if (!"true".equals(value)) return false;

        Long expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expireSeconds != null && expireSeconds > 0;
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private String getKey(String username) {
        return "email:code:" + username;
    }


}
