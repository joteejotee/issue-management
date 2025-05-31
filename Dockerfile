# ビルドステージ（Debian系でGradleを実行）
FROM eclipse-temurin:21-jdk as build-stage
WORKDIR /app
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle /app/
RUN chmod +x gradlew
COPY . .
RUN ./gradlew build -x test

# 実行ステージ（Alpine系で軽量化）
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# ビルドステージから実行可能JARをコピー
COPY --from=build-stage /app/build/libs/*.jar app.jar
# アプリケーション実行
CMD ["java", "-jar", "app.jar"]
