package org.example.cloudpos.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.auth.AuthProvider;
import org.example.cloudpos.auth.JwtTokenProvider;
import org.example.cloudpos.auth.domain.Member;
import org.example.cloudpos.auth.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();


        Long kakaoId = ((Number) attributes.get("id")).longValue();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : Map.of();

        String nickname = profile != null ? (String) profile.get("nickname") : null;
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        // 3. 우리 멤버 찾기 (userId=카카오 id 로 쓰는 버전)
        Member member = memberRepository
                .findByUserIdAndProvider(kakaoId, AuthProvider.KAKAO)
                .orElseGet(() -> {
                    // 여기서 inventoryId는 네 도메인 로직에 맞게 기본값 넣어
                    Member created = Member.builder()
                            .userId(kakaoId)
                            .inventoryId(0L)
                            .provider(AuthProvider.KAKAO)
                            .build();
                    return memberRepository.save(created);
                });

        // 4. 우리 JWT 발급 (멤버 PK 기준이 제일 안전)
        String jwt = jwtTokenProvider.createToken(member.getId(), "ROLE_USER");

        // 5. OAuth2User로 다시 구성 (프론트에서 토큰 꺼낼 수 있게)
        Map<String, Object> mapped = new HashMap<>();
        mapped.put("id", member.getId());          // 우리 멤버 id
        mapped.put("kakaoId", kakaoId);
        mapped.put("nickname", nickname);
        mapped.put("email", email);
        mapped.put("token", jwt);                  // 여기서 JWT 줌

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                mapped,
                "id"
        );
    }
}