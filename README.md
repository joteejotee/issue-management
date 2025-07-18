[![Java](https://img.shields.io/badge/Java-v21-007396?logo=java&logoColor=white)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-v3.3.0-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-v6.3.0-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-v3.1.2-005F0F?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-v4.1.8-38B2AC?logo=tailwind-css&logoColor=white)](https://tailwindcss.com)
[![DaisyUI](https://img.shields.io/badge/DaisyUI-v5.0.42-5A0EF8?logo=daisyui&logoColor=white)](https://daisyui.com)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-v15-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![MyBatis](https://img.shields.io/badge/MyBatis-v3.0.3-DC382D?logo=mybatis&logoColor=white)](https://mybatis.org)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white)](https://www.docker.com)
[![Gradle](https://img.shields.io/badge/Gradle-Build_Tool-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![JUnit 5](https://img.shields.io/badge/JUnit_5-Testing-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Playwright](https://img.shields.io/badge/Playwright-E2E_Testing-45ba4b?logo=playwright&logoColor=white)](https://playwright.dev)
[![Prettier](https://img.shields.io/badge/Prettier-v3.5.3-F7B93E?logo=prettier&logoColor=white)](https://prettier.io)

# issue-management アプリケーション

## 概要

このアプリケーションは、開発現場でのバグや機能要望を効率的に管理するための課題管理システムです。バックエンドには Spring Boot 3.3.0、データベースには PostgreSQL、フロントエンドには Thymeleaf + TailwindCSS を使用し、Spring Security による認証機能を実装。ユーザーはカンバン形式（課題/作業中/完了）で課題のステータス管理、担当者のアサイン、プロフィール画像のアップロードが可能です。また、直感的な操作のため、ワンクリックでのステータス変更機能を搭載しています。

Docker のみで、面倒なセットアップ不要・数分で動作確認できます。

## 主な機能

### 機能一覧

- **ユーザー認証機能(Spring Security)**
  - ログイン・ログアウト機能
  - CSRF保護
  
- **課題管理機能**
  - 課題登録・編集・削除(CRUD)
  - 課題詳細表示
  - ステータス管理(課題/作業中/完了)
  - ワンクリック操作(Ajax)
    - ステータス変更機能
    - 完了・戻す機能
  
- **担当者管理機能**
  - 担当者登録・編集・削除(CRUD)
  - プロフィール画像表示
  - 課題への担当者アサイン
  
- **ファイルアップロード機能(Uppy.js)**
  - 画像アップロード
  - プレビュー表示
  - ファイルサイズや形式のバリデーション機能
  
- **フロントエンドバリデーション**
  - リアルタイムバリデーション(Alpine.js + Zod)
  - パスワード表示切替
  - エラーメッセージ表示
  
- **データベース機能(PostgreSQL + MyBatis)**
  - トランザクション管理
  - 外部キー制約
  
- **環境・デプロイ機能**
  - Docker対応(Docker Compose)
  - 環境変数管理

## ローカルでの動かし方

### 前提条件

- Docker がインストールされていること
- ポート 8080 が使用可能であること

※ ダークモードは現在未実装のため、ライトモードでのご利用を推奨します。

### 実行手順

1. **リポジトリをクローンします。**

   ```bash
   git clone https://github.com/joteejotee/issue-management.git
   ```

2. **ルートディレクトリで Docker コンテナを起動します。**

   ```bash
   docker compose up -d
   ```

   このコマンドで以下の処理が自動的に行われます：

   - PostgreSQL データベースの準備
   - アプリケーションのビルドと実行
   - 必要な依存関係のインストール

3. **ブラウザでアプリケーションにアクセスします。**

   - `http://localhost:8080` にアクセス
   - ログイン情報:
     - メールアドレス: `test@example.com`
     - パスワード: `Test1234`

4. **アプリケーションの停止**
   ```bash
   docker compose down
   ```

### トラブルシューティング

- コンテナの状態確認

  ```bash
  docker compose ps
  ```

- ログの確認

  ```bash
  docker compose logs
  ```

- コンテナの再起動
  ```bash
  docker compose restart
  ```

## 技術スタック

- Java 21
- Spring Boot 3.3.0
- Spring Security 6.3.0
- Thymeleaf 3.1.2
- TailwindCSS 4.1.8
- DaisyUI 5.0.42
- PostgreSQL 15
- MyBatis 3.0.3
- Docker/Docker Compose
- Gradle
- Prettier 3.5.3

## テスト環境

- **バックエンド API**: JUnit 5 + REST Assured + Testcontainers（Spring Boot API テスト・ユニットテスト）
- **フロントエンド**: Web Test Runner + @testing-library/dom（Alpine.js + Zod バリデーションテスト）
- **フロントエンド E2E**: Playwright（ブラウザ自動化・ユーザーシナリオテスト）
- **テストカバレッジ**: JaCoCo + Web Test Runner Coverage 対応

## 画面一覧

| ログイン画面 | ホーム画面 |
| ---- | ---- |
| ![ログイン画面](./docs/screenshots/login.png) | ![ホーム画面](./docs/screenshots/top.png) |
| Spring Security による認証機能を実装しました。 | カンバン形式（TODO/DOING/DONE）での課題ステータス管理とワンクリックでのステータス変更機能を提供します。 |

| 課題一覧画面 | 課題詳細画面 |
| ---- | ---- |
| ![課題一覧画面](./docs/screenshots/issue-list.png) | ![課題詳細画面](./docs/screenshots/issue-detail.png) |
| 課題の一覧表示機能を提供します。 | 課題の詳細情報表示機能を実装しました。 |

| 担当者一覧画面 | 担当者新規登録画面 |
| ---- | ---- |
| ![担当者一覧画面](./docs/screenshots/assignee-list.png) | ![担当者新規登録画面](./docs/screenshots/assignee-create.png) |
| 登録済みの担当者の一覧表示と管理機能を実装しました。 | 担当者名を入力し、プロフィール画像をアップロードして、登録ボタンをクリックしてください。 |

## ディレクトリ構造

- `src/main/java/com/example/its`: Java ソースコード
- `src/main/resources`: リソースファイル（設定ファイル、テンプレートなど）
- `src/test/java/com/example/its`: テストコード

## ER 図

- このアプリケーションでは、以下のようなエンティティ関係図に基づいてデータベースを設計しています。
  - assignees・issues は、課題管理の中核を担う 2 テーブルで構成
  - users テーブルはログイン認証用で、課題管理とは独立した設計
  - 外部キー制約により、データ整合性を保っています

![architecture](./docs/database-schema.png)

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.
