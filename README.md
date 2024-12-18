# issue-management　アプリケーション

## 概要

issue-managementアプリケーションは、開発現場でのバグや機能要望を管理するための課題管理システムです。Java Spring Bootをバックエンドに使用し、PostgreSQLをデータベースとして利用しています。ユーザーは課題の作成、完了状態の管理、削除などの操作が可能です。

## 機能
- ユーザー認証（カスタムログイン機能）
- 課題一覧表示
  - 未完了課題と完了課題の分類表示
- 課題の詳細表示
- 課題の作成（入力値バリデーション機能付き）
- 課題の状態管理
  - 完了機能（未完了→完了）
  - 戻す機能（完了→未完了）
- 課題の削除
- トランザクション管理

## ローカルでの動かし方

### 前提条件

- Dockerがインストールされていること
- ポート8080が使用可能であること

### 実行手順

1. **リポジトリをクローンします。**
   ```bash
   git clone https://github.com/joteejotee/issue-management.git
   ```

2. **ルートディレクトリでDockerコンテナを起動します。**
   ```bash
   docker-compose up -d
   ```
   このコマンドで以下の処理が自動的に行われます：
   - PostgreSQLデータベースの準備
   - アプリケーションのビルドと実行
   - 必要な依存関係のインストール

3. **ブラウザでアプリケーションにアクセスします。**
   - `http://localhost:8080` にアクセス
   - ログイン情報:
     - ユーザー名: `user`
     - パスワード: `password`

4. **アプリケーションの停止**
   ```bash
   docker-compose down
   ```

### トラブルシューティング

- コンテナの状態確認
  ```bash
  docker-compose ps
  ```

- ログの確認
  ```bash
  docker-compose logs
  ```

- コンテナの再起動
  ```bash
  docker-compose restart
  ```

## 技術スタック
- Java 21
- Spring Boot 3.3.0
- PostgreSQL
- Thymeleaf
- Spring Security
- MyBatis

## ディレクトリ構造
- `src/main/java/com/example/its`: Javaソースコード
- `src/main/resources`: リソースファイル（設定ファイル、テンプレートなど）
- `src/test/java/com/example/its`: テストコード

## 注意事項
- データベースの設定やアプリケーションの設定は、環境に応じて適宜変更してください。
- セキュリティ設定は`SecurityConfig.java`で管理されています。
