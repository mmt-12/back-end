# Step 1: Build Stage
FROM gradle:8.5.0-jdk21 AS builder
WORKDIR /app

# 종속성 캐시 최적화
COPY build.gradle* settings.gradle* ./
COPY gradle ./gradle
RUN gradle --no-daemon build -x test || return 0

COPY . .
RUN gradle --no-daemon clean bootJar -x test

# Step 2: Runtime Stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# 시간대 설정 및 curl 설치
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# jar 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# RestDocs HTML 문서가 있다면 함께 복사
COPY --from=builder /app/build/docs/asciidoc /app/static/docs

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]