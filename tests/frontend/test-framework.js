// テスト用のヘルパー関数
const testHelpers = {
  // DOM要素をクリーンアップ
  cleanupDOM() {
    document.body.innerHTML = "";
  },

  // Alpine.jsコンポーネントを初期化
  async initializeAlpine() {
    if (!globalThis.Alpine) {
      // Alpine.jsをロード（実際のテストではモックまたは実際のライブラリを使用）
      globalThis.Alpine = {
        data: () => {},
        start: () => {},
      };
    }
  },

  // Zodスキーマテストヘルパー
  createMockZod() {
    globalThis.Zod = {
      z: {
        object: schema => ({
          safeParse: data => {
            // 簡単なバリデーションモック
            const isValid = Object.keys(schema).every(key => data[key] !== undefined);
            return {
              success: isValid,
              error: isValid ? null : { flatten: () => ({ fieldErrors: {} }) },
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
  },

  // CSRFトークンを模擬
  setupCSRF() {
    const csrfMeta = document.createElement("meta");
    csrfMeta.name = "_csrf";
    csrfMeta.content = "test-csrf-token";
    document.head.appendChild(csrfMeta);
  },
};

// グローバルに設定
globalThis.testHelpers = testHelpers;

// 各テスト前にクリーンアップ
globalThis.beforeEach = fn => {
  if (!globalThis._beforeEachCallbacks) {
    globalThis._beforeEachCallbacks = [];
  }
  globalThis._beforeEachCallbacks.push(fn);
};

// テスト関数を実行する前に beforeEach を実行
globalThis.runBeforeEach = () => {
  if (globalThis._beforeEachCallbacks) {
    globalThis._beforeEachCallbacks.forEach(callback => callback());
  }
};

// テストセットアップ
beforeEach(() => {
  testHelpers.cleanupDOM();
  testHelpers.setupCSRF();
});

// テスト用のアサーション関数
globalThis.expect = actual => ({
  toBe: expected => {
    if (actual !== expected) {
      throw new Error(`Expected ${actual} to be ${expected}`);
    }
  },
  toContain: expected => {
    if (!actual.includes(expected)) {
      throw new Error(`Expected ${actual} to contain ${expected}`);
    }
  },
  toBeNull: () => {
    if (actual !== null) {
      throw new Error(`Expected ${actual} to be null`);
    }
  },
  toBeInstanceOf: expectedClass => {
    if (!(actual instanceof expectedClass)) {
      throw new Error(`Expected ${actual} to be instance of ${expectedClass.name}`);
    }
  },
});
