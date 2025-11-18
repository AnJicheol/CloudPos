package org.example.cloudpos.auth.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface ProviderUserHandler {

    boolean supports(String registrationId);

    OAuth2User handle(OAuth2UserRequest userRequest,
                      Map<String, Object> attributes);
}