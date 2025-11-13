package org.example.cloudpos.cart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**

 * Redis 연결 및 직렬화 설정을 담당하는 구성 클래스입니다.
 *
 * <p><b>기능</b></p>
 * <ul>
 * <li>{@link LettuceConnectionFactory}를 사용해 Redis 서버와의 연결을 생성</li>
 * <li>{@link RedisTemplate}을 빈으로 등록하여 문자열 기반 Key-Value 연산을 지원</li>
 * <li>Key와 Value 모두 {@link StringRedisSerializer}로 직렬화 설정</li>
 * </ul>
 *
 * <p><b>비고</b><br>
 * 로컬 환경에서는 기본적으로 {@code localhost:6379}로 연결하며,
 * 운영 환경에서는 호스트와 포트를 환경 설정 파일에서 주입받도록 수정할 수 있습니다.
 * </p>

 */

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
