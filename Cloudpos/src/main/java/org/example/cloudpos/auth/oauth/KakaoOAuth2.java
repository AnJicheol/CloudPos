package org.example.cloudpos.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2 implements OAuth2Provider {
    @Override
    public String provider() {
        return "kakao";
    }

    @Override
    public OAuth2User process(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        return null;
    }


}
