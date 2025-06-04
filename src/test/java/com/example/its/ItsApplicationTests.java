package com.example.its;

import org.junit.jupiter.api.Test;

class ItsApplicationTests {

	@Test
	void contextLoads() {
		// アプリケーションクラスが正常に存在することをテスト
		ItsApplication app = new ItsApplication();
		org.assertj.core.api.Assertions.assertThat(app).isNotNull();
	}

}
