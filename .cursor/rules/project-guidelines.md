---
title: "プロジェクト共通ルール"
---

# コードスタイルと構造

- シンプル重視（最小限のコードで目的を達成し、肥大化・複雑化・冗長化を避ける）
- 関数型と宣言型プログラミングパターンを使用し、クラスは避ける
- 説明的な変数名を使用する（例: isLoading、hasError）

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
