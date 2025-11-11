package org.example.cloudpos.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {


    @Value("${jwt.secret}") // 예시용
    private String secretKeyBase64;

    @Value("${jwt.expiration-ms}")
    private long validityInMs;

    private SecretKey key() {
        // Base64 디코드해서 SecretKey 만들기
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long memberId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key(), Jwts.SIG.HS256)   // ← 최신식
                .compact();
    }
}
