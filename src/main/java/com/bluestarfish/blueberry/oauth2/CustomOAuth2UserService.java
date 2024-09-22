package com.bluestarfish.blueberry.oauth2;

import com.bluestarfish.blueberry.auth.entity.RefreshToken;
import com.bluestarfish.blueberry.auth.repository.RefreshTokenRepository;
import com.bluestarfish.blueberry.jwt.JWTTokens;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.enumeration.AuthType;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        KakaoResponse kakaoResponse = new KakaoResponse(oAuth2User.getAttributes());

        String email = kakaoResponse.getEmail(); // 식별자 붙인 이메일

        // 이메일 찾고 없으면 저장 및 조회
        // 있으면 조회
        // 중복이메일
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .authType(AuthType.KAKAO)
                                .build()
                ));

        JWTTokens token = jwtUtils.createJwt(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(token.refreshToken())
                        .build()
        );

        return new CustomOAuth2User(OAuth2UserDTO.from(user), token.accessToken());
    }
}
