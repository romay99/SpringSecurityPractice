package com.subin.spring.practice.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class kakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate  = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest); // 사용자 정보

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao'
        log.info("OAuth2 Login Start : " + registrationId);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = ((long)attributes.get("id")); // 회원 번호

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        String email = account != null ? (String) account.get("email") : null; // 이메일

        Map<String, Object> profile = account != null ? (Map<String, Object>) account.get("profile") : null;
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : null; // 프사
        String nickname = profile != null ? (String) profile.get("nickname") : null; // 닉네임

        // DB 에 유저 정보 insert , update 하는 부분
        // 여기서는 생략

        // 권한
        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // 표준화된 속성 맵(서비스 전반에서 쓰기 쉽도록)
        Map<String, Object> mapped = new HashMap<>();
        mapped.put("provider", "kakao");
        mapped.put("providerId", kakaoId);
        mapped.put("email", email);
        mapped.put("nickname", nickname);
        mapped.put("profileImage", profileImage);

        return new DefaultOAuth2User(authorities, mapped, "email");
    }
}
