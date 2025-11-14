package org.example.cloudpos.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface OAuth2Provider {

    String provider();

    OAuth2User process(OAuth2UserRequest userRequest,
                       Map<String, Object> attributes);
}
