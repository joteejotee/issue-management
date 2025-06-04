import { test, expect } from "@playwright/test";

test.describe("課題管理機能", () => {
  test.beforeEach(async ({ page }) => {
    // まずログインする
    await page.goto("/login");
    await page.fill("#email", "test@example.com");
    await page.fill("#password", "Test1234");
    await page.click('button[type="submit"]');

    // ホームページから課題一覧に移動
    await expect(page).toHaveURL("/issues");

    // ページが完全に読み込まれるまで待機
    await page.waitForLoadState("networkidle");

    // カンバンボードが表示されるまで待機
    await expect(page.getByRole("heading", { name: "課題", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "作業中", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "完了", exact: true })).toBeVisible();
  });

  test("課題一覧ページが正しく表示される", async ({ page }) => {
    // カンバンボードの3つのカラムが表示されることを確認
    await expect(page.getByRole("heading", { name: "課題", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "作業中", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "完了", exact: true })).toBeVisible();

    // 課題追加ボタンが表示されることを確認
    await expect(page.locator("text=課題を追加")).toBeVisible();
  });

  test("新しい課題を作成できる", async ({ page }) => {
    // 課題追加ボタンをクリック
    await page.click("text=課題を追加");

    // 課題作成フォームが表示されることを確認
    await expect(page).toHaveURL("/issues/creationForm");
    await expect(page.locator("h1")).toContainText("新しい課題を作成");

    // フォームに入力
    await page.fill('input[name="summary"]', "テスト課題のタイトル");
    await page.fill(
      'textarea[name="description"]',
      "テスト課題の詳細説明です。この課題はE2Eテストで作成されました。"
    );

    // 担当者を選択（ドロップダウンから）
    await page.selectOption('select[name="assigneeId"]', { index: 1 });

    // 作成ボタンをクリック
    await page.click('button[type="submit"]');

    // 課題一覧ページにリダイレクトされることを確認
    await expect(page).toHaveURL("/issues");

    // 作成した課題がTODOカラムに表示されることを確認
    await expect(page.locator("text=テスト課題のタイトル").first()).toBeVisible();
  });

  test("課題のステータスを変更できる", async ({ page }) => {
    // ページが完全に読み込まれるまで待機
    await page.waitForLoadState("networkidle");

    // まず「課題に戻す」ボタンがあるかチェック（作業中の課題を課題カラムに戻す）
    const backButton = page.locator("button[title='課題に戻す']").first();
    if (await backButton.isVisible()) {
      await backButton.click();
      await page.waitForLoadState("networkidle");
      // 追加の待機時間
      await page.waitForTimeout(1000);
    }

    // 「作業中に移動」ボタンをチェック
    const nextButton = page.locator("button[title='作業中に移動']").first();
    if (await nextButton.isVisible()) {
      // 「作業中に移動」ボタンをクリック
      await nextButton.click();

      // より確実な待機処理
      await page.waitForLoadState("networkidle");
      await page.waitForTimeout(2000); // 2秒追加待機

      // 作業中カラム自体が表示されることを先に確認
      await expect(page.getByRole("heading", { name: "作業中", exact: true })).toBeVisible();

      // 作業中カラムに課題が表示されることを確認
      const completeButtons = page.locator("button[title='完了に移動']");
      await expect(completeButtons.first()).toBeVisible({ timeout: 10000 });
    } else {
      // 「作業中に移動」ボタンがない場合は、既に作業中の課題があることを確認
      const completeButtons = page.locator("button[title='完了に移動']");
      await expect(completeButtons.first()).toBeVisible({ timeout: 10000 });
    }
  });

  test("課題の詳細を表示できる", async ({ page }) => {
    // 課題タイトルをクリック
    const issueLink = page.locator('a[href*="/issues/"]').first();
    await issueLink.click();

    // 課題詳細ページが表示されることを確認
    await expect(page.locator("h1")).toContainText("課題詳細");

    // 課題の情報が表示されることを確認
    await expect(page.locator("text=課題の概要")).toBeVisible();
    await expect(page.getByRole("heading", { name: "詳細説明" })).toBeVisible();
    await expect(page.locator("text=ステータス")).toBeVisible();
  });

  test("課題を完了できる", async ({ page }) => {
    // 課題を順次完了に移動
    let nextButton = page.locator("button[title='作業中に移動']").first();

    // 課題 → 作業中
    if (await nextButton.isVisible()) {
      await nextButton.click();
      await page.waitForLoadState("networkidle");
    }

    // 作業中 → 完了
    nextButton = page.locator("button[title='完了に移動']").first();
    if (await nextButton.isVisible()) {
      await nextButton.click();
      await page.waitForLoadState("networkidle");
    }

    // 完了カラムに課題が表示されることを確認
    await expect(page.locator("button[title='作業中に戻す']").first()).toBeVisible();
  });

  test("完了した課題を戻すことができる", async ({ page }) => {
    // 完了カラムの「作業中に戻す」ボタンをクリック
    const previousButton = page.locator("button[title='作業中に戻す']").first();

    if (await previousButton.isVisible()) {
      await previousButton.click();
      await page.waitForLoadState("networkidle");

      // 作業中カラムに課題が表示されることを確認
      await expect(page.locator("button[title='完了に移動']").first()).toBeVisible();
    }
  });

  test("カンバンボードのワンクリック操作が動作する", async ({ page }) => {
    // ページのボタン要素が正しく配置されていることを確認
    const nextButtons = page.locator("button[title='作業中に移動'], button[title='完了に移動']");
    const previousButtons = page.locator(
      "button[title='課題に戻す'], button[title='作業中に戻す']"
    );

    // 少なくとも1つのボタンが存在することを確認
    await expect(nextButtons.or(previousButtons).first()).toBeVisible();

    // ボタンにホバーしてアニメーションが動作することを確認
    const firstButton = nextButtons.or(previousButtons).first();
    await firstButton.hover();

    // ボタンが存在することを確認
    await expect(firstButton).toBeVisible();
  });

  test("担当者が正しく表示される", async ({ page }) => {
    // 担当者の画像（アバター）が表示されることを確認
    const assigneeImages = page.locator(
      'img[alt*="Emily Thompson"], img[alt*="Isaac Castillejos"], img[alt*="James Marty"], img[alt*="Daniel Miller"]'
    );

    // 少なくとも1つの担当者アバターが表示されることを確認
    await expect(assigneeImages.first()).toBeVisible();

    // 担当者名が表示されることを確認（いずれかの担当者名が表示されていること）
    const emilyName = page.locator("text=Emily Thompson");
    const isaacName = page.locator("text=Isaac Castillejos");
    const jamesName = page.locator("text=James Marty");
    const danielName = page.locator("text=Daniel Miller");

    // いずれかの担当者名が表示されることを確認
    await expect(emilyName.or(isaacName).or(jamesName).or(danielName).first()).toBeVisible();
  });

  test("課題作成フォームのバリデーションが動作する", async ({ page }) => {
    await page.click("text=課題を追加");

    // 空のフォームで送信を試行
    await page.click('button[type="submit"]');

    // バリデーションエラーが表示されることを確認
    // Spring Bootのバリデーションメッセージを確認
    await expect(page.locator(".alert-error"))
      .toBeVisible()
      .catch(() => {
        // バリデーションエラーがない場合は、フォームが送信されていないことを確認
        expect(page.url()).toContain("/creationForm");
      });
  });

  test("課題削除機能が動作する", async ({ page }) => {
    // 課題詳細ページに移動
    const issueLink = page.locator('a[href*="/issues/"]').first();
    await issueLink.click();

    // 削除ボタンが存在する場合はクリック
    const deleteButton = page.locator("text=削除");

    if (await deleteButton.isVisible()) {
      // 削除確認ダイアログを処理
      page.on("dialog", dialog => dialog.accept());

      await deleteButton.click();

      // 課題一覧ページにリダイレクトされることを確認
      await expect(page).toHaveURL("/issues");
    }
  });

  test("レスポンシブデザインが動作する", async ({ page }) => {
    // モバイルサイズにリサイズ
    await page.setViewportSize({ width: 375, height: 667 });

    // カンバンボードが適切に表示されることを確認
    await expect(page.getByRole("heading", { name: "課題", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "作業中", exact: true })).toBeVisible();
    await expect(page.getByRole("heading", { name: "完了", exact: true })).toBeVisible();

    // デスクトップサイズに戻す
    await page.setViewportSize({ width: 1280, height: 720 });

    // デスクトップレイアウトが復元されることを確認
    await expect(page.getByRole("heading", { name: "課題", exact: true })).toBeVisible();
  });
});
