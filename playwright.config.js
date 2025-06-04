import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./tests/e2e",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: "html",

  use: {
    // Spring Boot アプリケーションのベースURL
    baseURL: "http://localhost:8080",
    trace: "on-first-retry",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },

  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] },
    },
    {
      name: "firefox",
      use: { ...devices["Desktop Firefox"] },
    },
    // WebKitはシステム依存のBus errorが発生するため一時的にスキップ
    // {
    //   name: 'webkit',
    //   use: { ...devices['Desktop Safari'] },
    // },
    {
      name: "Mobile Chrome",
      use: { ...devices["Pixel 5"] },
    },
    // {
    //   name: 'Mobile Safari',
    //   use: { ...devices['iPhone 12'] },
    // },
  ],

  // Spring Boot アプリケーションの起動とシャットダウン
  webServer: {
    command: "docker compose up --build -d",
    port: 8080,
    reuseExistingServer: !process.env.CI,
    timeout: 120 * 1000, // 2分でタイムアウト（Docker ビルド時間を考慮）
  },
});
