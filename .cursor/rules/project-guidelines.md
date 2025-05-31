---
title: "プロジェクト共通ルール"
---

# コードスタイルと構造

- スタイルリングは全てDaisyUIで実装する。例外としてDaisyUIでの実装が非効率で、向いてない部分があれば無理に使わなくてよい。

# 使用技術とバージョン

## Tailwind CSS

- **バージョン**: v4.1.8 を使用
- Tailwind CSS v4 を使用
- 設定ファイル（`tailwind.config.js`）は不要
- 設定は `src/input.css` で直接管理

## DaisyUI

- **バージョン**: v5.0.42 を使用
- テーマ設定は `src/input.css` の `@plugin "daisyui"` で管理

# 開発ガイドライン

## CSS ビルド

- 開発時: `npm run build-css` (watch モード)
- 本番用: `npm run build-css-prod` (minify)

## スタイルファイル

- 入力ファイル: `src/input.css`
- 出力ファイル: `src/main/resources/static/css/style.css`

## フォント

- Inter フォント（英語）
- Noto Sans JP フォント（日本語）
- フォントファイルは `/fonts/` ディレクトリに配置
