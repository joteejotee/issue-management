// 課題ボードのJavaScript
document.addEventListener("DOMContentLoaded", function () {
  console.log("Issue Board JavaScript loaded");

  // CSRFトークンを取得
  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    .getAttribute("content");

  // 次のステータスに進めるボタン
  const nextButtons = document.querySelectorAll(".next-button");
  nextButtons.forEach(function (button) {
    button.addEventListener("click", function (event) {
      event.preventDefault();

      const issueId = this.getAttribute("data-issue-id");

      const form = document.createElement("form");
      form.method = "post";
      form.action = "/issues/next";

      form.setAttribute("style", "display:none;");
      form.innerHTML = `
                <input type="hidden" name="id" value="${issueId}">
                <input type="hidden" name="_csrf" value="${csrfToken}">
            `;

      document.body.appendChild(form);
      form.submit();
    });
  });

  // 前のステータスに戻すボタン
  const previousButtons = document.querySelectorAll(".previous-button");
  previousButtons.forEach(function (button) {
    button.addEventListener("click", function (event) {
      event.preventDefault();

      const issueId = this.getAttribute("data-issue-id");

      const form = document.createElement("form");
      form.method = "post";
      form.action = "/issues/previous";

      form.setAttribute("style", "display:none;");
      form.innerHTML = `
                <input type="hidden" name="id" value="${issueId}">
                <input type="hidden" name="_csrf" value="${csrfToken}">
            `;

      document.body.appendChild(form);
      form.submit();
    });
  });
});
