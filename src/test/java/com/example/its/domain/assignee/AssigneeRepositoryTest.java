package com.example.its.domain.assignee;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=",
    "spring.datasource.username=",
    "spring.datasource.password="
})
@DisplayName("AssigneeRepository マッパーテスト")
class AssigneeRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql");

    @Autowired
    private AssigneeRepository assigneeRepository;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        
        // Testcontainerの動的プロパティを設定
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Test
    @DisplayName("担当者を挿入し、IDが自動生成されることを確認")
    void shouldInsertAssigneeAndGenerateId() {
        // Given
        String name = faker.name().fullName();
        String photoUrl = faker.internet().url();
        AssigneeEntity assignee = new AssigneeEntity(0L, name, photoUrl);

        // When
        assigneeRepository.insert(assignee);

        // Then
        assertThat(assignee.getId()).isPositive();
        assertThat(assignee.getName()).isEqualTo(name);
        assertThat(assignee.getPhotoUrl()).isEqualTo(photoUrl);
    }

    @Test
    @DisplayName("すべての担当者を取得できる")
    void shouldFindAllAssignees() {
        // Given: 複数の担当者を作成
        AssigneeEntity assignee1 = createTestAssignee("田中太郎", "/images/tanaka.jpg");
        AssigneeEntity assignee2 = createTestAssignee("佐藤花子", "/images/sato.jpg");
        AssigneeEntity assignee3 = createTestAssignee("鈴木一郎", "/images/suzuki.jpg");

        assigneeRepository.insert(assignee1);
        assigneeRepository.insert(assignee2);
        assigneeRepository.insert(assignee3);

        // When
        List<AssigneeEntity> result = assigneeRepository.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(AssigneeEntity::getName)
            .containsExactlyInAnyOrder("田中太郎", "佐藤花子", "鈴木一郎");
    }

    @Test
    @DisplayName("IDで担当者を取得できる")
    void shouldFindAssigneeById() {
        // Given
        String expectedName = faker.name().fullName();
        String expectedPhotoUrl = faker.internet().url();
        AssigneeEntity assignee = createTestAssignee(expectedName, expectedPhotoUrl);
        assigneeRepository.insert(assignee);
        long assigneeId = assignee.getId();

        // When
        Optional<AssigneeEntity> result = assigneeRepository.findById(assigneeId);

        // Then
        assertThat(result).isPresent();
        AssigneeEntity foundAssignee = result.get();
        assertThat(foundAssignee.getId()).isEqualTo(assigneeId);
        assertThat(foundAssignee.getName()).isEqualTo(expectedName);
        assertThat(foundAssignee.getPhotoUrl()).isEqualTo(expectedPhotoUrl);
    }

    @Test
    @DisplayName("存在しないIDで検索した場合、空のOptionalが返される")
    void shouldReturnEmptyOptionalForNonExistentId() {
        // Given
        long nonExistentId = 999999L;

        // When
        Optional<AssigneeEntity> result = assigneeRepository.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("担当者情報を更新できる")
    void shouldUpdateAssignee() {
        // Given: 担当者を作成
        AssigneeEntity originalAssignee = createTestAssignee("山田太郎", "/images/yamada.jpg");
        assigneeRepository.insert(originalAssignee);
        long assigneeId = originalAssignee.getId();

        // 更新用データ
        String updatedName = "山田花子";
        String updatedPhotoUrl = "/images/yamada_updated.jpg";
        AssigneeEntity updatedAssignee = new AssigneeEntity(assigneeId, updatedName, updatedPhotoUrl);

        // When
        assigneeRepository.update(updatedAssignee);

        // Then
        Optional<AssigneeEntity> result = assigneeRepository.findById(assigneeId);
        assertThat(result).isPresent();
        AssigneeEntity foundAssignee = result.get();
        assertThat(foundAssignee.getName()).isEqualTo(updatedName);
        assertThat(foundAssignee.getPhotoUrl()).isEqualTo(updatedPhotoUrl);
    }

    @Test
    @DisplayName("担当者を削除できる")
    void shouldDeleteAssignee() {
        // Given
        AssigneeEntity assignee = createTestAssignee(faker.name().fullName(), faker.internet().url());
        assigneeRepository.insert(assignee);
        long assigneeId = assignee.getId();

        // 削除前に存在することを確認
        assertThat(assigneeRepository.findById(assigneeId)).isPresent();

        // When
        assigneeRepository.delete(assigneeId);

        // Then
        assertThat(assigneeRepository.findById(assigneeId)).isEmpty();
    }

    @Test
    @DisplayName("日本語名とURLパスが正しく保存・取得される")
    void shouldHandleJapaneseNamesAndUrlPaths() {
        // Given: 日本語の名前と複雑なURLパス
        String japaneseName = "山田太郎";
        String complexPhotoUrl = "/uploads/avatars/avatar_20241201_123456_a1b2c3d4.jpg";
        AssigneeEntity assignee = createTestAssignee(japaneseName, complexPhotoUrl);

        // When
        assigneeRepository.insert(assignee);

        // Then
        Optional<AssigneeEntity> result = assigneeRepository.findById(assignee.getId());
        assertThat(result).isPresent();
        AssigneeEntity foundAssignee = result.get();
        assertThat(foundAssignee.getName()).isEqualTo(japaneseName);
        assertThat(foundAssignee.getPhotoUrl()).isEqualTo(complexPhotoUrl);
    }

    @Test
    @DisplayName("複数の担当者がIDの昇順で取得される")
    void shouldReturnAssigneesOrderedById() {
        // Given: 複数の担当者を時系列で作成
        AssigneeEntity first = createTestAssignee("First", "/first.jpg");
        AssigneeEntity second = createTestAssignee("Second", "/second.jpg");
        AssigneeEntity third = createTestAssignee("Third", "/third.jpg");

        assigneeRepository.insert(first);
        assigneeRepository.insert(second);
        assigneeRepository.insert(third);

        // When
        List<AssigneeEntity> result = assigneeRepository.findAll();

        // Then: IDの昇順になっていることを確認
        assertThat(result).hasSize(3);
        for (int i = 0; i < result.size() - 1; i++) {
            AssigneeEntity current = result.get(i);
            AssigneeEntity next = result.get(i + 1);
            assertThat(current.getId()).isLessThan(next.getId());
        }
    }

    // テストヘルパーメソッド
    private AssigneeEntity createTestAssignee(String name, String photoUrl) {
        return new AssigneeEntity(0L, name, photoUrl);
    }
} 