package com.example.its.domain.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    // 許可される画像ファイル形式
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // 許可されるファイル拡張子
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    // 最大ファイルサイズ（5MB）
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // アップロードディレクトリの設定（外部ディレクトリを使用）
    @Value("${app.upload.dir:/app/uploads/avatars}")
    private String uploadDir;
    
    private final String publicPath = "/uploads/avatars/";

    /**
     * アバター画像をアップロードする
     * 
     * @param file アップロードするファイル
     * @return アップロードされたファイルのパブリックパス
     * @throws IllegalArgumentException バリデーションエラー
     * @throws RuntimeException ファイル操作エラー
     */
    public String uploadAvatarImage(MultipartFile file) {
        log.info("=== ファイルアップロード開始 ===");
        
        // バリデーション
        validateFile(file);
        validateImageType(file);
        validateFileSize(file);

        // 安全なファイル名を生成
        String fileName = generateSafeFileName(file.getOriginalFilename());
        log.info("生成されたファイル名: {}", fileName);

        try {
            // アップロードディレクトリを作成
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            
            // ファイルを保存
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("ファイル保存完了: {}", filePath.toAbsolutePath());
            
            // パブリックパスを返す
            String publicUrl = this.publicPath + fileName;
            log.info("パブリックURL: {}", publicUrl);
            return publicUrl;

        } catch (IOException e) {
            log.error("ファイル保存中にエラーが発生: {}", e.getMessage(), e);
            throw new RuntimeException("ファイルの保存に失敗しました", e);
        }
    }

    /**
     * ファイルの基本バリデーション
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("ファイルが選択されていません");
        }

        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new IllegalArgumentException("ファイル名が不正です");
        }
    }

    /**
     * 画像ファイル形式のバリデーション
     */
    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "サポートされていないファイル形式です。JPG、PNG、GIF、WebP形式のファイルを選択してください。");
        }

        // ファイル拡張子もチェック
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "ファイル拡張子が不正です。.jpg、.png、.gif、.webp のファイルを選択してください。");
        }
    }

    /**
     * ファイルサイズのバリデーション
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("ファイルサイズが大きすぎます。%dMB以下のファイルを選択してください。", 
                            MAX_FILE_SIZE / (1024 * 1024)));
        }
    }

    /**
     * 安全なファイル名を生成する
     * タイムスタンプとUUIDを組み合わせて一意性を保証
     */
    private String generateSafeFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("avatar_%s_%s.%s", timestamp, uuid, extension);
    }

    /**
     * ファイル拡張子を取得する
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
} 