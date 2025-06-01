---
title: "プロジェクト共通ルール"
---

# コードスタイルと構造

- スタイルリングは全て DaisyUI で実装する。例外として DaisyUI での実装が非効率で、向いてない部分があれば無理に使わなくてよい。

# 使用技術とバージョン

## Tailwind CSS

- **バージョン**: v4.1.8 を使用
- Tailwind CSS v4 を使用（内蔵コンパイラ採用）
- 設定ファイル（`tailwind.config.js`）は不要
- PostCSS 設定も不要（v4 では内蔵されている）
- 設定は `src/input.css` で直接管理

## DaisyUI

- **バージョン**: v5.0.42 を使用
- テーマ設定は `src/input.css` の `@plugin "daisyui"` で管理

# 開発ガイドライン

## CSS ビルド

- 開発時: `npm run build-css` (watch モード)
- 本番用: `npm run build-css-prod` (minify)
- 構文チェック: `npm run build-css-check`

## ビルドコマンド詳細

```bash
# 開発時（watchモード）
npm run dev

# 本番ビルド
npm run build

# 構文チェック
npm run build-css-check
```

## スタイルファイル

- **入力ファイル**: `src/input.css` （編集対象）
- **出力ファイル**: `src/main/resources/static/css/style.css` （自動生成、編集禁止）

## フォント管理

- Inter（英語）、Noto Sans JP（日本語）は**CDN を使わず**、`/fonts/` ディレクトリに**ローカルファイルとしてセルフホスト**しています。
- `@font-face` により `src/main/resources/static/css/style.css` から利用。
