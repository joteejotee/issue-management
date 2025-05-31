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

    // アップロードディレクトリの設定
    private final String uploadDir = "src/main/resources/static/images/avatars/";
    private final String publicPath = "/images/avatars/";

    /**
     * アバター画像をアップロードする
     * 
     * @param file アップロードするファイル
     * @return アップロードされたファイルのパブリックパス
     * @throws IllegalArgumentException バリデーションエラー
     * @throws RuntimeException ファイル操作エラー
     */
    public String uploadAvatarImage(MultipartFile file) {
        log.info("=== ファイルアップロードサービス開始 ===");
        
        // 基本バリデーション
        validateFile(file);
        log.info("基本バリデーション完了");

        // ファイル形式チェック
        validateImageType(file);
        log.info("ファイル形式バリデーション完了");

        // ファイルサイズチェック
        validateFileSize(file);
        log.info("ファイルサイズバリデーション完了");

        // 安全なファイル名を生成
        String fileName = generateSafeFileName(file.getOriginalFilename());
        log.info("生成されたファイル名: {}", fileName);

        try {
            // アップロードディレクトリを作成（存在しない場合）
            Path uploadPath = Paths.get(uploadDir);
            log.info("アップロードディレクトリパス: {}", uploadPath.toAbsolutePath());
            log.info("ディレクトリが存在するか: {}", Files.exists(uploadPath));
            
            if (!Files.exists(uploadPath)) {
                log.info("ディレクトリを作成中...");
                Files.createDirectories(uploadPath);
                log.info("アップロードディレクトリを作成しました: {}", uploadPath.toAbsolutePath());
            }
            
            // ディレクトリの権限確認
            log.info("ディレクトリの権限確認:");
            log.info("- 読み取り可能: {}", Files.isReadable(uploadPath));
            log.info("- 書き込み可能: {}", Files.isWritable(uploadPath));
            log.info("- 実行可能: {}", Files.isExecutable(uploadPath));

            // ファイルを保存
            Path filePath = uploadPath.resolve(fileName);
            log.info("保存先ファイルパス: {}", filePath.toAbsolutePath());
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("ファイルの保存が完了: {}", filePath.toAbsolutePath());
            log.info("保存されたファイルサイズ: {} bytes", Files.size(filePath));

            // パブリックパスを返す
            String publicPath = this.publicPath + fileName;
            log.info("パブリックパス: {}", publicPath);
            return publicPath;

        } catch (IOException e) {
            log.error("=== ファイル保存中にIOエラーが発生 ===");
            log.error("エラークラス: {}", e.getClass().getSimpleName());
            log.error("エラーメッセージ: {}", e.getMessage());
            log.error("詳細スタックトレース: ", e);
            throw new RuntimeException("ファイルの保存に失敗しました: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("=== ファイル保存中に予期しないエラーが発生 ===");
            log.error("エラークラス: {}", e.getClass().getSimpleName());
            log.error("エラーメッセージ: {}", e.getMessage());
            log.error("詳細スタックトレース: ", e);
            throw new RuntimeException("ファイルの保存中に予期しないエラーが発生しました: " + e.getMessage(), e);
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