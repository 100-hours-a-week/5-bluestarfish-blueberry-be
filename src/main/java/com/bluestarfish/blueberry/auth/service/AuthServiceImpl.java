package com.bluestarfish.blueberry.auth.service;

import com.bluestarfish.blueberry.auth.dto.LoginRequest;
import com.bluestarfish.blueberry.auth.dto.LoginSuccessResult;
import com.bluestarfish.blueberry.auth.dto.MailAuthRequest;
import com.bluestarfish.blueberry.auth.dto.MailRequest;
import com.bluestarfish.blueberry.auth.entity.AuthCode;
import com.bluestarfish.blueberry.auth.entity.RefreshToken;
import com.bluestarfish.blueberry.auth.enumeration.MailAuthType;
import com.bluestarfish.blueberry.auth.exception.AuthException;
import com.bluestarfish.blueberry.auth.repository.AuthCodeRepository;
import com.bluestarfish.blueberry.auth.repository.RefreshTokenRepository;
import com.bluestarfish.blueberry.jwt.JWTTokens;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JWTUtils jwtUtils;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCodeRepository authCodeRepository;

    @Override
    public LoginSuccessResult login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequest.getEmail())
                .orElseThrow(() -> new AuthException("A user with " + loginRequest.getEmail() + " does not exist", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("The password is not match", HttpStatus.UNAUTHORIZED);
        }

        JWTTokens jwtTokens = jwtUtils.createJwt(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(jwtTokens.refreshToken())
                        .build()
        );

        return LoginSuccessResult.builder()
                .userId(user.getId())
                .accessToken(jwtTokens.accessToken())
                .build();
    }

    @Override
    public void logout(String accessToken) {
        accessToken = URLDecoder.decode(accessToken, StandardCharsets.UTF_8);
        refreshTokenRepository.deleteByUserId(jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8)));
    }

    @Override
    public void sendMail(MailRequest mailRequest) {

        String email = mailRequest.getEmail();

        if (mailRequest.getType().equals(MailAuthType.JOIN.getType())) {
            userRepository.findByEmailAndDeletedAtIsNotNull(email)
                    .ifPresent(user -> {
                        throw new AuthException("The email address already exists", HttpStatus.CONFLICT);
                    });
        }

        try {
            authCodeRepository.save(
                    AuthCode.builder()
                            .email(email)
                            .code(createCode(email))
                            .build()
            );
        } catch (MessagingException messagingException) {
            throw new AuthException(
                    "Internal Server Error",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    @Override
    public void authenticateCode(MailAuthRequest mailAuthRequest) {

        AuthCode authCode = authCodeRepository.findByEmail(mailAuthRequest.getEmail())
                .orElseThrow(() -> new AuthException("No auth code found for the requested email address", HttpStatus.NOT_FOUND));

        if (Duration.between(authCode.getCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
            throw new AuthException("The auth code has expired", HttpStatus.REQUEST_TIMEOUT);
        }

        if (!mailAuthRequest.getCode().equals(authCode.getCode())) {
            throw new AuthException("Invalid code", HttpStatus.UNAUTHORIZED);
        }
    }


    private String createCode(String email) throws MessagingException {
        String code = generateRandomCode();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, TRUE, "UTF-8");

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<style>" +
                "body { margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #F4F6FA; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #F4F6FA; padding: 20px; }" +
                ".header { text-align: left; padding: 10px; background-color: #F4F6FA; }" +
                ".header img { max-width: 60px; }" +
                ".title { font-size: 24px; font-weight: bold; color: #333333; margin-bottom: 20px; }" +
                ".content { font-size: 16px; color: #333333; margin-bottom: 20px; }" +
                ".code-box { background-color: #E2E8F5; padding: 15px; text-align: center; margin-top: 20px; margin-bottom: 20px; border-radius: 10px; }" +
                ".code-box span { font-size: 32px; font-weight: bold; color: #4F5DCB; }" +
                ".footer { font-size: 12px; color: #7D7D7D; text-align: center; margin-top: 30px; }" +
                ".footer a { color: #4F5DCB; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<img src=\"https://your-logo-url-here.com/logo.png\" alt=\"Blueberry Logo\" />" +
                "</div>" +
                "<div class=\"title\">이메일 인증 안내</div>" +
                "<div class=\"content\">" +
                "안녕하세요, 고객님 :)<br />" +
                "이메일 인증 안내드립니다.<br />" +
                "아래 발급된 이메일 인증번호를 복사하거나 직접 입력하여 인증을 완료해주세요.<br />" +
                "개인정보 보호를 위해 인증번호는 <strong>5분</strong> 간 유효합니다." +
                "</div>" +
                "<div class=\"code-box\">" +
                "인증 번호 <span>" + code + "</span>" +
                "</div>" +
                "<div class=\"footer\">" +
                "본 메일은 발신 전용입니다.<br />" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";


        helper.setTo(email);
        helper.setSubject("메일인증");
        helper.setText(htmlContent, TRUE);
        javaMailSender.send(message);

        return code;
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int randomNumber = random.nextInt(10);
            code.append(randomNumber);
        }

        return code.toString();
    }
}
