/**
 * ログインフォーム バリデーション
 * Alpine.js + Zod でリアルタイム検証を実装
 * バックエンド（Spring Validation）と同じルールを適用
 */

// ZodスキーマをCDNで読み込み後に定義
let loginSchema = null;

// Zodライブラリの読み込み待機とスキーマ初期化
let retryCount = 0;
function initializeZodSchema() {
  // 最大リトライ回数
  if (retryCount++ > 30) {
    console.error("Zodライブラリの読み込みに失敗しました。スキーマ初期化を中断します。");
    return;
  }

  // グローバルオブジェクトを取得
  const zGlobal = window.Zod && window.Zod.z ? window.Zod.z : window.z;

  if (zGlobal && typeof zGlobal.object === "function") {
    // バックエンドと同じバリデーションルール
    loginSchema = zGlobal.object({
      email: zGlobal
        .string()
        .min(1, "メールアドレスは必須です")
        .email("正しいメールアドレス形式で入力してください"),
      password: zGlobal
        .string()
        .min(1, "パスワードは必須です")
        .min(8, "パスワードは8文字以上である必要があります")
        .regex(/^[A-Za-z0-9]+$/, "パスワードは半角英数字のみ使用できます")
        .regex(/(?=.*[a-z])/, "パスワードには小文字を1文字以上含めてください")
        .regex(/(?=.*[A-Z])/, "パスワードには大文字を1文字以上含めてください")
        .regex(/(?=.*\d)/, "パスワードには数字を1文字以上含めてください"),
    });
    console.log("Zod schema initialized successfully (retryCount=" + retryCount + ")");
    return;
  }

  console.warn("Zod library not yet available, retrying... (" + retryCount + ")");
  setTimeout(initializeZodSchema, 200);
}

// 即座にZod初期化を開始
initializeZodSchema();

// Alpine.jsのデータ関数として定義（グローバルに即座に配置）
window.loginForm = function () {
  return {
    // フォームデータ（初期値を空文字で明示的に設定）
    email: "",
    password: "",

    // バリデーション結果
    errors: {},

    // 初期化
    init() {
      console.log("Login form initialized with Alpine.js + Zod validation");
      console.log("Initial values - email:", this.email, "password:", this.password);
      // Zodスキーマの初期化をチェック
      if (!loginSchema) {
        console.log("Zod schema not ready, waiting...");
        // スキーマが準備できるまで待機
        const checkSchema = () => {
          if (loginSchema) {
            console.log("Zod schema is now ready");
          } else {
            setTimeout(checkSchema, 100);
          }
        };
        checkSchema();
      }
    },

    // リアルタイムバリデーション（onChange）
    validate() {
      console.log("Validate called with email:", this.email, "password:", this.password);

      if (!loginSchema) {
        console.warn("Zod schema not initialized yet, skipping validation");
        return;
      }

      try {
        // Zodでバリデーション実行
        const result = loginSchema.safeParse({
          email: this.email,
          password: this.password,
        });

        if (result.success) {
          // バリデーション成功
          this.errors = {};
          this.clearErrorMessages();
          console.log("Validation passed");
        } else {
          // バリデーション失敗
          this.errors = result.error.flatten().fieldErrors;
          this.showErrorMessages();
          console.log("Validation failed:", this.errors);
        }
      } catch (error) {
        console.error("Validation error:", error);
      }
    },

    // エラーメッセージの表示
    showErrorMessages() {
      this.clearErrorMessages();

      // メールエラー表示
      if (this.errors.email) {
        this.showFieldError("email", this.errors.email[0]);
      }

      // パスワードエラー表示
      if (this.errors.password) {
        this.showFieldError("password", this.errors.password[0]);
      }
    },

    // フィールド別エラー表示
    showFieldError(fieldName, message) {
      const field = document.getElementById(fieldName);
      const errorDiv = document.getElementById(`${fieldName}-error`);

      if (field) {
        field.classList.add("input-error", "border-error");
      }

      if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.classList.remove("hidden");
      }
    },

    // エラーメッセージのクリア
    clearErrorMessages() {
      ["email", "password"].forEach(fieldName => {
        const field = document.getElementById(fieldName);
        const errorDiv = document.getElementById(`${fieldName}-error`);

        if (field) {
          field.classList.remove("input-error", "border-error");
        }

        if (errorDiv) {
          errorDiv.classList.add("hidden");
          errorDiv.textContent = "";
        }
      });

      // サーバーサイドエラーもクリア
      const clientError = document.getElementById("client-error");
      if (clientError) {
        clientError.classList.add("hidden");
      }
    },

    // フィールドフォーカス時のエラークリア
    clearFieldError(fieldName) {
      const field = document.getElementById(fieldName);
      const errorDiv = document.getElementById(`${fieldName}-error`);

      if (field) {
        field.classList.remove("input-error", "border-error");
      }

      if (errorDiv) {
        errorDiv.classList.add("hidden");
      }
    },
  };
};
