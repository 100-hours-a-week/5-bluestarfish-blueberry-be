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
            authCodeRepository.deleteByEmail(email);
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

        authCodeRepository.deleteByEmail(mailAuthRequest.getEmail());
    }


    private String createCode(String email) throws MessagingException {
        String code = generateRandomCode();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, TRUE, "UTF-8");

        String htmlContent = "<div style='width: 600px; padding: 20px; font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; border-radius: 10px;'>"
                + "    <div style='text-align: center; margin-bottom: 20px;'>"
                + "        <img src='cid:blueberryLogo' alt='Blueberry Logo' style='width: 100px;'/>"
                + "        <h2 style='color: #3366CC; margin: 20px 0;'>이메일 인증 안내</h2>"
                + "    </div>"
                + "    <div style='padding: 20px; background-color: #fff; border-radius: 10px;'>"
                + "        <p>이메일 인증 안내드립니다.<br>아래 발급된 이메일 인증코드를 회원가입 입력창에 입력해서 인증을 진행해주세요.</p>"
                + "        <p style='font-size: 16px; color: #333;'>해당 인증코드는 <strong>5분</strong> 간 유효합니다.</p>"
                + "        <div style='text-align: center; margin-top: 30px;'>"
                + "            <span style='display: inline-block; padding: 10px 20px; background-color: #3366CC; color: #fff; font-size: 24px; border-radius: 5px;'>"
                + code
                + "            </span>"
                + "        </div>"
                + "    </div>"
                + "    <p style='margin-top: 20px; font-size: 12px; color: #666;'>"
                + "        궁금하신 사항은 <a href='https://blueberry826.com' style='color: #3366CC; text-decoration: none;'>블루베리 사이트 링크</a> 들어오셔서 문의주시면 감사하겠습니다."
                + "    </p>"
                + "</div>";


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
