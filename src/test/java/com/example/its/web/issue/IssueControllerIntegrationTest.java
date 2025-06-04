package com.example.its.web.issue;

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
@DisplayName("課題管理 API 統合テスト")
class IssueControllerIntegrationTest {

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
    @DisplayName("課題一覧ページが正常に表示される")
    void shouldDisplayIssueListPage() {
        given()
            .when()
                .get("/issues")
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("課題"));
    }

    @Test
    @DisplayName("課題作成フォームが正常に表示される")
    void shouldDisplayIssueCreationForm() {
        given()
            .when()
                .get("/issues/creationForm")
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("name=\"summary\""))
                .body(containsString("name=\"description\""));
    }

    @Test
    @DisplayName("有効な課題データで課題が正常に作成される")
    void shouldCreateIssueWithValidData() {
        // Given: テストデータ
        String summary = faker.lorem().sentence(5);
        String description = faker.lorem().paragraph(3);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", summary)
            .formParam("description", description)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("課題サマリーが空の場合はバリデーションエラーが発生する")
    void shouldRejectEmptySummary() {
        // Given: 空のサマリー
        String description = faker.lorem().paragraph(3);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", "")
            .formParam("description", description)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML);
    }

    @Test
    @DisplayName("課題説明が空の場合はバリデーションエラーが発生する")
    void shouldRejectEmptyDescription() {
        // Given: 空の説明
        String summary = faker.lorem().sentence(5);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", summary)
            .formParam("description", "")
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML);
    }

    @Test
    @DisplayName("課題サマリーが256文字を超える場合はバリデーションエラーが発生する")
    void shouldRejectOversizedSummary() {
        // Given: 256文字を超えるサマリー
        String oversizedSummary = "a".repeat(257);
        String description = faker.lorem().paragraph(3);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", oversizedSummary)
            .formParam("description", description)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("新しい課題を作成"));
    }

    @Test
    @DisplayName("課題説明が256文字を超える場合はバリデーションエラーが発生する")
    void shouldRejectOversizedDescription() {
        // Given: 256文字を超える説明
        String summary = faker.lorem().sentence(5);
        String oversizedDescription = "a".repeat(257);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", summary)
            .formParam("description", oversizedDescription)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(200) // バリデーションエラーでフォーム再表示
            .contentType(ContentType.HTML)
            .body(containsString("新しい課題を作成"));
    }

    @Test
    @DisplayName("担当者なしで課題が作成される")
    void shouldCreateIssueWithoutAssignee() {
        // Given: 担当者IDなし
        String summary = faker.lorem().sentence(5);
        String description = faker.lorem().paragraph(3);
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("summary", summary)
            .formParam("description", description)
        .when()
            .post("/issues")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("課題ステータスを次の段階に進めることができる")
    void shouldMoveIssueToNextStatus() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("id", 1L)
        .when()
            .post("/issues/next")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("課題ステータスを前の段階に戻すことができる")
    void shouldMoveIssueToPreviousStatus() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("id", 1L)
        .when()
            .post("/issues/previous")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("課題を完了状態にできる")
    void shouldCompleteIssue() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("id", 1L)
        .when()
            .post("/issues/complete")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("課題詳細ページが正常に表示される")
    void shouldDisplayIssueDetailPage() {
        // Given: 存在する課題ID
        long issueId = 1L;
        
        given()
            .when()
                .get("/issues/{issueId}", issueId)
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML);
    }

    @Test
    @DisplayName("課題を削除できる")
    void shouldDeleteIssue() {
        // Given: 存在する課題ID
        long issueId = 1L;
        
        given()
        .when()
            .post("/issues/{issueId}/delete", issueId)
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("存在しない課題IDでの詳細表示は404エラーが発生する")
    void shouldReturn404ForNonExistentIssue() {
        // Given: 存在しない課題ID
        long nonExistentId = 999999L;
        
        given()
            .when()
                .get("/issues/{issueId}", nonExistentId)
            .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("日本語文字を含む課題が正常に作成される")
    void shouldCreateIssueWithJapaneseText() {
        // Given: 日本語テキスト
        String summary = "課題の概要：重要なバグの修正";
        String description = "このバグは、ユーザーがログインできない問題を引き起こしています。優先度高で対応が必要です。";
        
        given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("summary", summary)
            .formParam("description", description)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }

    @Test
    @DisplayName("特殊文字を含む課題が正常に作成される")
    void shouldCreateIssueWithSpecialCharacters() {
        // Given: 特殊文字を含むテキスト
        String summary = "API エラー: HTTP 500 & データベース接続タイムアウト";
        String description = "エラーメッセージ: \"Connection timeout\" が発生。原因: DB負荷増大 (CPU: 90%+)";
        
        given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("summary", summary)
            .formParam("description", description)
            .formParam("assigneeId", 1L)
        .when()
            .post("/issues")
        .then()
            .statusCode(302) // リダイレクト
            .header("Location", endsWith("/issues"));
    }
} 