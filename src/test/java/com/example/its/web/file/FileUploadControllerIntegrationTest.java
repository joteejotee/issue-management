package com.example.its.web.file;

import com.example.its.ItsApplication;
import com.example.its.config.TestSecurityConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = ItsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("ファイルアップロード API 統合テスト")
class FileUploadControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withCopyFileToContainer(
                org.testcontainers.utility.MountableFile.forClasspathResource("schema.sql"),
                "/docker-entrypoint-initdb.d/01-schema.sql"
            )
            .withCopyFileToContainer(
                org.testcontainers.utility.MountableFile.forClasspathResource("integration-test-data.sql"),
                "/docker-entrypoint-initdb.d/02-integration-test-data.sql"
            );

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("app.upload.dir", () -> System.getProperty("java.io.tmpdir") + "/test-uploads");
    }

    @LocalServerPort
    private int port;

    @TempDir
    Path tempDir;

    private Faker faker;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        faker = new Faker();
    }

    @Test
    @DisplayName("正常な画像ファイルがアップロードできる")
    void shouldUploadValidImageFile() throws IOException {
        // Given: テスト用の画像ファイルを作成
        Path testImagePath = createTestImageFile("test-image.jpg", "fake-jpeg-content");

        // When & Then: ファイルアップロード API を実行
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", testImagePath.toFile(), "image/jpeg")
            .header("X-CSRF-TOKEN", "test-token")
            .header("X-Requested-With", "XMLHttpRequest")
        .when()
            .post("/api/files/upload/avatar")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("success", equalTo(true))
            .body("filePath", matchesPattern("/uploads/avatars/avatar_\\d{8}_\\d{6}_[a-f0-9]{8}\\.jpg"))
            .body("message", containsString("アップロードが完了しました"));
    }

    @Test
    @DisplayName("ファイルサイズが大きすぎる場合はエラーになる")
    void shouldRejectOversizedFile() throws IOException {
        // Given: 6MBの大きなファイルを作成
        Path largeFilePath = createTestImageFile("large-image.jpg", 
            "x".repeat(6 * 1024 * 1024)); // 6MB

        // When & Then
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", largeFilePath.toFile(), "image/jpeg")
            .header("X-CSRF-TOKEN", "test-token")
            .header("X-Requested-With", "XMLHttpRequest")
        .when()
            .post("/api/files/upload/avatar")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("success", equalTo(false))
            .body("message", containsString("ファイルサイズが大きすぎます"));
    }

    @Test
    @DisplayName("許可されていないファイル形式はエラーになる")
    void shouldRejectInvalidFileType() throws IOException {
        // Given: テキストファイル
        Path textFilePath = createTestFile("test.txt", "This is a text file");

        // When & Then
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", textFilePath.toFile(), "text/plain")
            .header("X-CSRF-TOKEN", "test-token")
            .header("X-Requested-With", "XMLHttpRequest")
        .when()
            .post("/api/files/upload/avatar")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("success", equalTo(false))
            .body("message", containsString("許可されていないファイル形式です"));
    }

    @Test
    @DisplayName("空のファイルはエラーになる")
    void shouldRejectEmptyFile() throws IOException {
        // Given: 空のファイル
        Path emptyFilePath = createTestFile("empty.jpg", "");

        // When & Then
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", emptyFilePath.toFile(), "image/jpeg")
            .header("X-CSRF-TOKEN", "test-token")
            .header("X-Requested-With", "XMLHttpRequest")
        .when()
            .post("/api/files/upload/avatar")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("success", equalTo(false))
            .body("message", containsString("ファイルが選択されていません"));
    }

    @Test
    @DisplayName("CSRFトークンなしでもアップロードできる（テスト環境）")
    void shouldAllowUploadInTestEnvironment() throws IOException {
        // Given: テスト環境ではCSRFが無効化されているため正常にアップロードできる
        Path testImagePath = createTestImageFile("test-image.jpg", "fake-jpeg-content");

        // When & Then: CSRFトークンなしでもアップロード可能
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", testImagePath.toFile(), "image/jpeg")
            .header("X-Requested-With", "XMLHttpRequest")
        .when()
            .post("/api/files/upload/avatar")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("success", equalTo(true));
    }

    @Test
    @DisplayName("複数の画像形式がサポートされている")
    void shouldSupportMultipleImageFormats() throws IOException {
        String[] formats = {"jpg", "png", "gif", "webp"};
        String[] mimeTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

        for (int i = 0; i < formats.length; i++) {
            // Given
            Path testImagePath = createTestImageFile("test." + formats[i], "fake-content");

            // When & Then
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("file", testImagePath.toFile(), mimeTypes[i])
                .header("X-CSRF-TOKEN", "test-token")
                .header("X-Requested-With", "XMLHttpRequest")
            .when()
                .post("/api/files/upload/avatar")
            .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("filePath", endsWith("." + formats[i]));
        }
    }

    // テストヘルパーメソッド
    private Path createTestImageFile(String fileName, String content) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, content.getBytes());
        return filePath;
    }

    private Path createTestFile(String fileName, String content) throws IOException {
        return createTestImageFile(fileName, content);
    }
} 