// パスワード表示/非表示の切り替え
function togglePassword() {
  const passwordInput = document.getElementById("password");
  const eyeIcon = document.getElementById("eye-icon");
  const eyeOffIcon = document.getElementById("eye-off-icon");

  if (passwordInput.type === "password") {
    passwordInput.type = "text";
    eyeIcon.classList.add("hidden");
    eyeOffIcon.classList.remove("hidden");
  } else {
    passwordInput.type = "password";
    eyeIcon.classList.remove("hidden");
    eyeOffIcon.classList.add("hidden");
  }
}

// フォームバリデーション（Alpine.js + Zodに統合されたため簡素化）
function validateForm() {
  const email = document.getElementById("email");
  const password = document.getElementById("password");

  // 要素が存在しない場合は無視
  if (!email || !password) {
    return true;
  }

  // 基本的なチェックのみ（詳細はAlpine.js + Zodが担当）
  return email.value.trim() !== "" && password.value !== "";
}

// エラーメッセージを表示（Alpine.js + Zodがメインのエラー表示を担当）
function showError(message) {
  console.log("Error:", message);
  // Alpine.js + Zodのエラー表示に任せる
}

// フォーム送信時の処理
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("login-form");
  if (form) {
    form.addEventListener("submit", function (e) {
      // Alpine.js + Zodバリデーションが主体なので、基本チェックのみ
      if (!validateForm()) {
        e.preventDefault();
      }
    });
  }

  // 入力フィールドのフォーカス時の処理（存在する要素のみ）
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");

  if (emailInput) {
    emailInput.addEventListener("focus", function () {
      // Alpine.jsのエラークリア機能に任せる
      console.log("Email field focused");
    });
  }

  if (passwordInput) {
    passwordInput.addEventListener("focus", function () {
      // Alpine.jsのエラークリア機能に任せる
      console.log("Password field focused");
    });
  }
});
