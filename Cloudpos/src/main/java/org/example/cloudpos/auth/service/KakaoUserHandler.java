package org.example.cloudpos.auth.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.auth.domain.Users;
import org.example.cloudpos.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KakaoUserHandler implements ProviderUserHandler {

    private final UserRepository usersRepository;

    @Override
    public boolean supports(String registrationId) {
        return "kakao".equals(registrationId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User handle(OAuth2UserRequest userRequest,
                             Map<String, Object> attributes) {


        Long kakaoId = ((Number) attributes.get("id")).longValue();
        String provider = "kakao";
        String providerUserId = String.valueOf(kakaoId);

        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile =
                kakaoAccount != null
                        ? (Map<String, Object>) kakaoAccount.get("profile")
                        : Map.of();

        String email = kakaoAccount != null
                ? (String) kakaoAccount.get("email")
                : null;

        String name = profile != null
                ? (String) profile.get("nickname")
                : null;

        if (name == null) {
            throw new OAuth2AuthenticationException("카카오 프로필 닉네임이 없습니다.");
        }

        //  최초 로그인 -> 회원 가입
        Users user = usersRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> {
                    String newUserId = UlidCreator.getUlid().toString();
                    Users newUser = new Users(
                            newUserId,
                            provider,
                            providerUserId,
                            name,
                            email
                    );
                    return usersRepository.save(newUser);
                });

        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));


        return new UsersPrincipal(
                user.getUserId(),
                user.getName(),
                authorities,
                attributes
        );
    }
}