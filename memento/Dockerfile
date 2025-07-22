# Step 1: Build Stage
FROM gradle:8.5.0-jdk21 AS builder
WORKDIR /app

# gradlew와 wrapper 먼저 복사
COPY gradlew .
COPY gradle ./gradle

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 캐시 최적화
COPY build.gradle* settings.gradle* ./
RUN ./gradlew --no-daemon build -x test || true

# 전체 복사
COPY . .

# test와 asciidoctor 빌드 제외
RUN ./gradlew --no-daemon clean bootJar -x test -x asciidoctor

# Step 2: Runtime Stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# 시간대 설정 및 curl 설치
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# RestDocs 디렉토리가 존재할 때만 복사되도록 하고 싶다면 try-catch 방식이 필요하지만,
# Dockerfile 자체는 조건문 지원이 안되므로, 존재하지 않을 경우를 대비해 아래 라인은 제거하거나,
# 아니면 build.gradle에서 asciidoctor 출력을 다른 위치로 바꾸는 것도 방법
# 지금은 제거 처리 추천:
# COPY --from=builder /app/build/docs/asciidoc /app/static/docs

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]