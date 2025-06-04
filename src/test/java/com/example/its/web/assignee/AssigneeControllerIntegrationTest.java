package com.example.its.web.assignee;

import com.example.its.ItsApplication;
import com.example.its.config.TestSecurityConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = ItsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("担当者管理 API 統合テスト")
class AssigneeControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @LocalServerPort
    private int port;

    private Faker faker;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        faker = new Faker();
    }

    @Test
    @DisplayName("担当者一覧ページが正常に表示される")
    void shouldDisplayAssigneeListPage() {
        given()
            .when()
                .get("/assignees")
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("担当者"));
    }

    @Test
    @DisplayName("担当者作成フォームが正常に表示される")
    void shouldDisplayAssigneeCreationForm() {
        given()
            .when()
                .get("/assignees/creationForm")
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("name=\"name\""))
                .body(containsString("name=\"photoUrl\""));
    }

    @Test
    @DisplayName("有効な担当者データで担当者が正常に作成される")
    void shouldCreateAssigneeWithValidData() {
        // Given: テストデータ
        String name = faker.name().fullName();
        String photoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", name)
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("担当者名が空の場合はバリデーションエラーが発生する")
    void shouldRejectEmptyName() {
        // Given: 空の名前
        String photoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", "")
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML);
    }

    @Test
    @DisplayName("写真URLが空の場合はバリデーションエラーが発生する")
    void shouldRejectEmptyPhotoUrl() {
        // Given: 空の写真URL
        String name = faker.name().fullName();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", name)
            .formParam("photoUrl", "")
        .when()
            .post("/assignees")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("新しい担当者を追加"));
    }

    @Test
    @DisplayName("担当者名が100文字を超える場合はバリデーションエラーが発生する")
    void shouldRejectOversizedName() {
        // Given: 100文字を超える名前
        String oversizedName = "a".repeat(101);
        String photoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", oversizedName)
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("新しい担当者を追加"));
    }

    @Test
    @DisplayName("写真URLが256文字を超える場合はバリデーションエラーが発生する")
    void shouldRejectOversizedPhotoUrl() {
        // Given: 256文字を超える写真URL
        String name = faker.name().fullName();
        String oversizedPhotoUrl = "https://example.com/" + "a".repeat(240);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", name)
            .formParam("photoUrl", oversizedPhotoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("新しい担当者を追加"));
    }

    @Test
    @DisplayName("担当者編集フォームが正常に表示される")
    void shouldDisplayAssigneeEditForm() {
        // Given: 存在する担当者ID
        long assigneeId = 1L;
        
        given()
            .when()
                .get("/assignees/{id}/editForm", assigneeId)
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("担当者編集"))
                .body(containsString("name=\"name\""))
                .body(containsString("name=\"photoUrl\""));
    }

    @Test
    @DisplayName("存在しない担当者IDでの編集フォーム表示は一覧ページにリダイレクトされる")
    void shouldRedirectToListForNonExistentAssigneeEditForm() {
        // Given: 存在しない担当者ID
        long nonExistentId = 999999L;
        
        given()
            .when()
                .get("/assignees/{id}/editForm", nonExistentId)
            .then()
                .statusCode(302) // リダイレクト
                .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("有効な担当者データで担当者が正常に更新される")
    void shouldUpdateAssigneeWithValidData() {
        // Given: 存在する担当者IDと更新データ
        long assigneeId = 1L;
        String updatedName = faker.name().fullName();
        String updatedPhotoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", updatedName)
            .formParam("photoUrl", updatedPhotoUrl)
        .when()
            .post("/assignees/{id}", assigneeId)
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("無効な担当者データで更新は失敗する")
    void shouldRejectUpdateWithInvalidData() {
        // Given: 存在する担当者IDと無効なデータ
        long assigneeId = 1L;
        String emptyName = "";
        String photoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", emptyName)
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees/{id}", assigneeId)
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("担当者編集"));
    }

    @Test
    @DisplayName("担当者を削除できる")
    void shouldDeleteAssignee() {
        // Given: 存在する担当者ID
        long assigneeId = 1L;
        
        given()
        .when()
            .post("/assignees/{id}/delete", assigneeId)
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("日本語文字を含む担当者が正常に作成される")
    void shouldCreateAssigneeWithJapaneseText() {
        // Given: 日本語テキスト
        String japaneseName = "山田太郎";
        String photoUrl = "/uploads/avatars/yamada_taro.jpg";
        
        given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("name", japaneseName)
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("複雑なURLパスを含む写真URLが正常に作成される")
    void shouldCreateAssigneeWithComplexPhotoUrl() {
        // Given: 複雑なURLパス
        String name = faker.name().fullName();
        String complexPhotoUrl = "/uploads/avatars/avatar_20241201_123456_a1b2c3d4.jpg";
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", name)
            .formParam("photoUrl", complexPhotoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("外部URLを写真URLとして使用できる")
    void shouldCreateAssigneeWithExternalPhotoUrl() {
        // Given: 外部URL
        String name = faker.name().fullName();
        String externalPhotoUrl = "https://cdn.example.com/avatars/user123.png";
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("name", name)
            .formParam("photoUrl", externalPhotoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }

    @Test
    @DisplayName("特殊文字を含む担当者名が正常に作成される")
    void shouldCreateAssigneeWithSpecialCharacters() {
        // Given: 特殊文字を含む名前
        String specialName = "John O'Connor-Smith (Jr.)";
        String photoUrl = faker.internet().url();
        
        given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("name", specialName)
            .formParam("photoUrl", photoUrl)
        .when()
            .post("/assignees")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/assignees"));
    }
} 