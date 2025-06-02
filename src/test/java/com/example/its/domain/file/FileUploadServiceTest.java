package com.example.its.domain.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {
    
    @Mock
    private MultipartFile mockFile;
    
    @InjectMocks
    private FileUploadService fileUploadService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // アップロードディレクトリをテンポラリディレクトリに設定
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
    }
    
    @Nested
    @DisplayName("ファイルバリデーションテスト")
    class FileValidationTest {
        
        @Test
        @DisplayName("nullファイルはエラーになる")
        void uploadAvatarImage_WithNullFile_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ファイルが選択されていません");
        }
        
        @Test
        @DisplayName("空ファイルはエラーになる")
        void uploadAvatarImage_WithEmptyFile_ShouldThrowException() {
            // Given
            when(mockFile.isEmpty()).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ファイルが選択されていません");
        }
        
        @Test
        @DisplayName("ファイル名がnullの場合はエラーになる")
        void uploadAvatarImage_WithNullFilename_ShouldThrowException() {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn(null);
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ファイル名が不正です");
        }
        
        @Test
        @DisplayName("空のファイル名はエラーになる")
        void uploadAvatarImage_WithEmptyFilename_ShouldThrowException() {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("");
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ファイル名が不正です");
        }
    }
    
    @Nested
    @DisplayName("ファイル形式バリデーションテスト")
    class FileTypeValidationTest {
        
        @ParameterizedTest
        @ValueSource(strings = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"})
        @DisplayName("許可された画像形式は正常に処理される")
        void uploadAvatarImage_WithAllowedImageTypes_ShouldSucceed(String contentType) throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn(contentType);
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
            
            // When & Then
            assertThatCode(() -> fileUploadService.uploadAvatarImage(mockFile))
                .doesNotThrowAnyException();
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"text/plain", "application/pdf", "image/bmp", "video/mp4"})
        @DisplayName("許可されていないファイル形式はエラーになる")
        void uploadAvatarImage_WithDisallowedTypes_ShouldThrowException(String contentType) {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn(contentType);
            when(mockFile.getOriginalFilename()).thenReturn("test.txt");
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("サポートされていないファイル形式です");
        }
        
        @Test
        @DisplayName("ContentTypeがnullの場合はエラーになる")
        void uploadAvatarImage_WithNullContentType_ShouldThrowException() {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn(null);
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("サポートされていないファイル形式です");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"test.txt", "test.doc", "test.exe", "test.bmp"})
        @DisplayName("許可されていない拡張子はエラーになる")
        void uploadAvatarImage_WithDisallowedExtensions_ShouldThrowException(String filename) {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn(filename);
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ファイル拡張子が不正です");
        }
    }
    
    @Nested
    @DisplayName("ファイルサイズバリデーションテスト")
    class FileSizeValidationTest {
        
        @Test
        @DisplayName("サイズ制限を超えるファイルはエラーになる")
        void uploadAvatarImage_WithOversizedFile_ShouldThrowException() {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ファイルサイズが大きすぎます");
        }
        
        @Test
        @DisplayName("サイズ制限内のファイルは正常に処理される")
        void uploadAvatarImage_WithValidSize_ShouldSucceed() throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(1024L); // 1KB
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
            
            // When & Then
            assertThatCode(() -> fileUploadService.uploadAvatarImage(mockFile))
                .doesNotThrowAnyException();
        }
        
        @Test
        @DisplayName("0バイトファイルは正常に処理される")
        void uploadAvatarImage_WithZeroSizeFile_ShouldSucceed() throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(0L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
            
            // When & Then
            assertThatCode(() -> fileUploadService.uploadAvatarImage(mockFile))
                .doesNotThrowAnyException();
        }
    }
    
    @Nested
    @DisplayName("ファイル保存テスト")
    class FileSaveTest {
        
        @Test
        @DisplayName("正常なファイルが正しく保存される")
        void uploadAvatarImage_WithValidFile_ShouldSaveFile() throws IOException {
            // Given
            String testContent = "test image content";
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(testContent.getBytes()));
            
            // When
            String result = fileUploadService.uploadAvatarImage(mockFile);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).startsWith("/uploads/avatars/");
            assertThat(result).contains("avatar_");
            assertThat(result).endsWith(".jpg");
        }
        
        @Test
        @DisplayName("ファイル名に安全な文字列が生成される")
        void uploadAvatarImage_ShouldGenerateSafeFilename() throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/png");
            when(mockFile.getOriginalFilename()).thenReturn("test file with spaces.png");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
            
            // When
            String result = fileUploadService.uploadAvatarImage(mockFile);
            
            // Then
            assertThat(result).startsWith("/uploads/avatars/avatar_");
            assertThat(result).endsWith(".png");
            assertThat(result).matches("/uploads/avatars/avatar_\\d{8}_\\d{6}_[a-f0-9]{8}\\.png");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"test.JPG", "test.PNG", "test.GIF", "test.WEBP"})
        @DisplayName("大文字拡張子も正しく処理される")
        void uploadAvatarImage_WithUppercaseExtensions_ShouldWork(String filename) throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn(filename);
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
            
            // When & Then
            assertThatCode(() -> fileUploadService.uploadAvatarImage(mockFile))
                .doesNotThrowAnyException();
        }
    }
    
    @Nested
    @DisplayName("エラーハンドリングテスト")
    class ErrorHandlingTest {
        
        @Test
        @DisplayName("IOException発生時にRuntimeExceptionがスローされる")
        void uploadAvatarImage_WithIOException_ShouldThrowRuntimeException() throws IOException {
            // Given
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenThrow(new IOException("Test IO Exception"));
            
            // When & Then
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ファイルの保存に失敗しました")
                .hasCauseInstanceOf(IOException.class);
        }
        
        @Test
        @DisplayName("複数のバリデーションエラーが適切に処理される")
        void uploadAvatarImage_WithMultipleValidationErrors_ShouldThrowFirstError() {
            // Given - 空ファイルで不正なContentType
            when(mockFile.isEmpty()).thenReturn(true);
            
            // When & Then - 最初のバリデーション（空ファイル）でエラーになる
            assertThatThrownBy(() -> fileUploadService.uploadAvatarImage(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ファイルが選択されていません");
        }
    }
} 