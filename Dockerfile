# Node.js用ビルドステージ（CSS前処理）
FROM node:20-alpine as node-stage

WORKDIR /app
COPY package*.json /app/
RUN npm install
COPY src/ /app/src/
RUN npm run build

# Javaアプリケーション用ビルドステージ
FROM eclipse-temurin:21-jdk as build-stage

WORKDIR /app
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle /app/
RUN chmod +x gradlew

# プロジェクトファイルをコピー
COPY . .

# Node.jsステージからビルド済みCSSをコピー
COPY --from=node-stage /app/src/main/resources/static/css/ /app/src/main/resources/static/css/

# Javaアプリケーションをビルド
RUN ./gradlew build -x test

# 実行ステージ（Alpine系で軽量化）
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# ビルドステージから実行可能JARをコピー
COPY --from=build-stage /app/build/libs/*.jar app.jar
# アプリケーション実行
CMD ["java", "-jar", "app.jar"]
