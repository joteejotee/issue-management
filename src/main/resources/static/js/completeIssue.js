// JavaScriptを読み込む
document.addEventListener("DOMContentLoaded", function() {
    console.log("JavaScript loaded");

    // CSRFトークンを取得取得する
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    // ボタンを取得する
    const completeButtons = document.querySelectorAll(".complete-button");

    completeButtons.forEach(function(button) {
        // ボタンのクリックを検知
        button.addEventListener("click", function(event) {
            event.preventDefault();

            // 課題IDを取得する
            const issueId = this.getAttribute("data-issue-id");

            const form = document.createElement("form");
            form.method = "post";
            form.action = "/issues/complete";

            form.setAttribute("style", "display:none;");
            form.innerHTML = `
                <input type="hidden" name="id" value="${issueId}">
                <input type="hidden" name="_csrf" value="${csrfToken}">
            `;

            document.body.appendChild(form);
            form.submit();
        });
    });

    // 削除ボタンを取得する
    const deleteButtons = document.querySelectorAll(".delete-button");

    deleteButtons.forEach(function(button) {
        // 削除ボタンのクリックを検知
        button.addEventListener("click", function(event) {
            event.preventDefault();

            // 課題IDを取得する
            const issueId = this.getAttribute("data-issue-id");

            const form = document.createElement("form");
            form.method = "post";
            form.action = "/issues/delete";

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
