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

// フォームバリデーション
function validateForm() {
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value;
  const errorDiv = document.getElementById("client-error");

  // エラーメッセージをクリア
  errorDiv.classList.add("hidden");
  errorDiv.textContent = "";

  // バリデーション
  if (!username) {
    showError("ユーザー名を入力してください。");
    return false;
  }

  if (username.length < 3) {
    showError("ユーザー名は3文字以上で入力してください。");
    return false;
  }

  if (!password) {
    showError("パスワードを入力してください。");
    return false;
  }

  if (password.length < 6) {
    showError("パスワードは6文字以上で入力してください。");
    return false;
  }

  return true;
}

// エラーメッセージを表示
function showError(message) {
  const errorDiv = document.getElementById("client-error");
  errorDiv.textContent = message;
  errorDiv.classList.remove("hidden");
  errorDiv.scrollIntoView({ behavior: "smooth" });
}

// フォーム送信時の処理
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("login-form");
  if (form) {
    form.addEventListener("submit", function (e) {
      if (!validateForm()) {
        e.preventDefault();
      }
    });
  }

  // 入力フィールドのフォーカス時にエラーをクリア
  const inputs = document.querySelectorAll("#username, #password");
  inputs.forEach((input) => {
    input.addEventListener("focus", function () {
      const errorDiv = document.getElementById("client-error");
      errorDiv.classList.add("hidden");
    });
  });
});
