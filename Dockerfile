# ビルドステージ（Debian系でGradleを実行）
FROM eclipse-temurin:21-jdk as build-stage

# Node.jsとnpmをインストール
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

WORKDIR /app
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle /app/
RUN chmod +x gradlew

# package.jsonをコピーしてnpm installを実行
COPY package*.json /app/
RUN npm install

# プロジェクトファイルをコピー
COPY . .

# CSS前処理を実行
RUN npm run build

# Javaアプリケーションをビルド
RUN ./gradlew build -x test

# 実行ステージ（Alpine系で軽量化）
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# ビルドステージから実行可能JARをコピー
COPY --from=build-stage /app/build/libs/*.jar app.jar
# アプリケーション実行
CMD ["java", "-jar", "app.jar"]
