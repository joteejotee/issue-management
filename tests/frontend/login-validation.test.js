describe("ログインバリデーション", () => {
  let loginFormInstance;

  beforeEach(() => {
    // DOM初期化
    document.body.innerHTML = `
      <form id="login-form" x-data="loginForm()" x-init="init()">
        <div class="form-control">
          <input type="email" id="email" name="email" 
                 x-model="email" 
                 @input="validate" 
                 @focus="clearFieldError('email')" 
                 placeholder="メールアドレスを入力" />
          <div id="email-error" class="text-error text-sm mt-1 hidden"></div>
        </div>
        
        <div class="form-control">
          <input type="password" id="password" name="password" 
                 x-model="password" 
                 @input="validate" 
                 @focus="clearFieldError('password')" 
                 placeholder="パスワードを入力" />
          <div id="password-error" class="text-error text-sm mt-1 hidden"></div>
        </div>
        
        <button type="submit">ログイン</button>
      </form>
    `;

    // Zodスキーマのモック設定
    globalThis.Zod = {
      z: {
        object: schema => ({
          safeParse: data => {
            const errors = {};

            // メールバリデーション
            if (!data.email) {
              errors.email = ["メールアドレスは必須です"];
            } else if (!data.email.includes("@")) {
              errors.email = ["正しいメールアドレス形式で入力してください"];
            }

            // パスワードバリデーション
            if (!data.password) {
              errors.password = ["パスワードは必須です"];
            } else if (data.password.length < 8) {
              errors.password = ["パスワードは8文字以上である必要があります"];
            } else if (!/^[A-Za-z0-9]+$/.test(data.password)) {
              errors.password = ["パスワードは半角英数字のみ使用できます"];
            } else if (!/(?=.*[a-z])/.test(data.password)) {
              errors.password = ["パスワードには小文字を1文字以上含めてください"];
            } else if (!/(?=.*[A-Z])/.test(data.password)) {
              errors.password = ["パスワードには大文字を1文字以上含めてください"];
            } else if (!/(?=.*\d)/.test(data.password)) {
              errors.password = ["パスワードには数字を1文字以上含めてください"];
            }

            const hasErrors = Object.keys(errors).length > 0;
            return {
              success: !hasErrors,
              error: hasErrors ? { flatten: () => ({ fieldErrors: errors }) } : null,
            };
          },
        }),
        string: () => ({
          min: () => ({ email: () => ({ regex: () => ({}) }) }),
          email: () => ({ regex: () => ({}) }),
          regex: () => ({}),
        }),
      },
    };

    // Alpine.js loginForm関数のモック（実際のコードから抜粋）
    globalThis.loginForm = function () {
      return {
        email: "",
        password: "",
        errors: {},

        init() {
          console.log("Login form initialized");
        },

        validate() {
          if (!globalThis.Zod) return;

          const result = globalThis.Zod.z.object({}).safeParse({
            email: this.email,
            password: this.password,
          });

          if (result.success) {
            this.errors = {};
            this.clearErrorMessages();
          } else {
            this.errors = result.error.flatten().fieldErrors;
            this.showErrorMessages();
          }
        },

        showErrorMessages() {
          this.clearErrorMessages();

          if (this.errors.email) {
            this.showFieldError("email", this.errors.email[0]);
          }

          if (this.errors.password) {
            this.showFieldError("password", this.errors.password[0]);
          }
        },

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
        },

        clearFieldError(fieldName) {
          const field = document.getElementById(fieldName);
          const errorDiv = document.getElementById(`${fieldName}-error`);

          if (field) {
            field.classList.remove("input-error", "border-error");
          }

          if (errorDiv) {
            errorDiv.classList.add("hidden");
            errorDiv.textContent = "";
          }
        },
      };
    };

    // ログインフォームインスタンスを作成
    loginFormInstance = globalThis.loginForm();
    loginFormInstance.init();
  });

  describe("基本的なDOM要素", () => {
    it("メールアドレス入力フィールドが存在する", () => {
      const emailInput = document.getElementById("email");
      expect(emailInput).not.toBeNull();
      expect(emailInput.type).toBe("email");
    });

    it("パスワード入力フィールドが存在する", () => {
      const passwordInput = document.getElementById("password");
      expect(passwordInput).not.toBeNull();
      expect(passwordInput.type).toBe("password");
    });

    it("エラーメッセージ要素が存在し、初期状態では非表示", () => {
      const emailError = document.getElementById("email-error");
      const passwordError = document.getElementById("password-error");

      expect(emailError).not.toBeNull();
      expect(passwordError).not.toBeNull();
      expect(emailError.classList.contains("hidden")).toBe(true);
      expect(passwordError.classList.contains("hidden")).toBe(true);
    });
  });

  describe("Alpine.js + Zodバリデーション", () => {
    it("空のメールアドレスでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "";
      loginFormInstance.password = "Test1234";
      loginFormInstance.validate();

      const emailError = document.getElementById("email-error");
      expect(emailError.textContent).toBe("メールアドレスは必須です");
      expect(emailError.classList.contains("hidden")).toBe(false);
    });

    it("無効なメールアドレス形式でバリデーションエラーが表示される", () => {
      loginFormInstance.email = "invalid-email";
      loginFormInstance.password = "Test1234";
      loginFormInstance.validate();

      const emailError = document.getElementById("email-error");
      expect(emailError.textContent).toBe("正しいメールアドレス形式で入力してください");
      expect(emailError.classList.contains("hidden")).toBe(false);
    });

    it("短いパスワードでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "123";
      loginFormInstance.validate();

      const passwordError = document.getElementById("password-error");
      expect(passwordError.textContent).toBe("パスワードは8文字以上である必要があります");
      expect(passwordError.classList.contains("hidden")).toBe(false);
    });

    it("英数字以外のパスワードでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "Test123!";
      loginFormInstance.validate();

      const passwordError = document.getElementById("password-error");
      expect(passwordError.textContent).toBe("パスワードは半角英数字のみ使用できます");
      expect(passwordError.classList.contains("hidden")).toBe(false);
    });

    it("小文字を含まないパスワードでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "TEST1234";
      loginFormInstance.validate();

      const passwordError = document.getElementById("password-error");
      expect(passwordError.textContent).toBe("パスワードには小文字を1文字以上含めてください");
      expect(passwordError.classList.contains("hidden")).toBe(false);
    });

    it("大文字を含まないパスワードでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "test1234";
      loginFormInstance.validate();

      const passwordError = document.getElementById("password-error");
      expect(passwordError.textContent).toBe("パスワードには大文字を1文字以上含めてください");
      expect(passwordError.classList.contains("hidden")).toBe(false);
    });

    it("数字を含まないパスワードでバリデーションエラーが表示される", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "TestPassword";
      loginFormInstance.validate();

      const passwordError = document.getElementById("password-error");
      expect(passwordError.textContent).toBe("パスワードには数字を1文字以上含めてください");
      expect(passwordError.classList.contains("hidden")).toBe(false);
    });

    it("有効な入力でバリデーションエラーが表示されない", () => {
      loginFormInstance.email = "test@example.com";
      loginFormInstance.password = "Test1234";
      loginFormInstance.validate();

      const emailError = document.getElementById("email-error");
      const passwordError = document.getElementById("password-error");

      expect(emailError.textContent).toBe("");
      expect(passwordError.textContent).toBe("");
      expect(emailError.classList.contains("hidden")).toBe(true);
      expect(passwordError.classList.contains("hidden")).toBe(true);
    });
  });

  describe("エラークリア機能", () => {
    it("フィールドフォーカス時にエラーがクリアされる", () => {
      // まずエラーを発生させる
      loginFormInstance.email = "";
      loginFormInstance.validate();

      const emailError = document.getElementById("email-error");
      expect(emailError.classList.contains("hidden")).toBe(false);

      // エラーをクリア
      loginFormInstance.clearFieldError("email");

      expect(emailError.classList.contains("hidden")).toBe(true);
      expect(emailError.textContent).toBe("");
    });

    it("全エラーメッセージがクリアされる", () => {
      // エラーを発生させる
      loginFormInstance.email = "";
      loginFormInstance.password = "";
      loginFormInstance.validate();

      // エラーが表示されていることを確認
      const emailError = document.getElementById("email-error");
      const passwordError = document.getElementById("password-error");
      expect(emailError.classList.contains("hidden")).toBe(false);
      expect(passwordError.classList.contains("hidden")).toBe(false);

      // すべてクリア
      loginFormInstance.clearErrorMessages();

      expect(emailError.classList.contains("hidden")).toBe(true);
      expect(passwordError.classList.contains("hidden")).toBe(true);
      expect(emailError.textContent).toBe("");
      expect(passwordError.textContent).toBe("");
    });
  });

  describe("CSS クラス管理", () => {
    it("エラー時に適切なCSSクラスが適用される", () => {
      loginFormInstance.email = "";
      loginFormInstance.validate();

      const emailInput = document.getElementById("email");
      expect(emailInput.classList.contains("input-error")).toBe(true);
      expect(emailInput.classList.contains("border-error")).toBe(true);
    });

    it("エラークリア時にCSSクラスが削除される", () => {
      // エラーを発生させる
      loginFormInstance.email = "";
      loginFormInstance.validate();

      const emailInput = document.getElementById("email");
      expect(emailInput.classList.contains("input-error")).toBe(true);

      // エラーをクリア
      loginFormInstance.clearFieldError("email");
      expect(emailInput.classList.contains("input-error")).toBe(false);
      expect(emailInput.classList.contains("border-error")).toBe(false);
    });
  });
});

// 簡単なexpect関数の実装
globalThis.expect = actual => ({
  toBe(expected) {
    if (actual !== expected) {
      throw new Error(`Expected ${actual} to be ${expected}`);
    }
  },
  toBeNull() {
    if (actual !== null) {
      throw new Error(`Expected ${actual} to be null`);
    }
  },
  not: {
    toBeNull() {
      if (actual === null) {
        throw new Error(`Expected ${actual} not to be null`);
      }
    },
  },
  toContain(expected) {
    if (!actual.includes(expected)) {
      throw new Error(`Expected ${actual} to contain ${expected}`);
    }
  },
});
