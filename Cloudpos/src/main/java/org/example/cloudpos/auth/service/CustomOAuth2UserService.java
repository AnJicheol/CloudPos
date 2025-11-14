package org.example.cloudpos.auth.service;

import lombok.RequiredArgsConstructor;

import org.example.cloudpos.auth.oauth.OAuth2Provider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final List<OAuth2Provider> oAuth2Providers;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();


        OAuth2Provider oAuth2 = oAuth2Providers.stream()
                .filter(p -> p.provider().equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "지원하지 않는 OAuth2 provider: " + registrationId
                ));

        return oAuth2.process(userRequest, attributes);
    }
}
