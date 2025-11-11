# ===== 1단계: 빌드 =====
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# 먼저 빌드 스크립트만 복사해서 의존성 캐시
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 윈도우 실행권한
RUN chmod +x gradlew

# 의존성만 미리 내려받기
RUN ./gradlew dependencies --no-daemon || true

# 실제 소스 복사
COPY src ./src

# 스프링 부트 실행 jar 빌드
RUN ./gradlew bootJar --no-daemon

# ===== 2단계: 실행 =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# 위에서 빌드된 jar
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
