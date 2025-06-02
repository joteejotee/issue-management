package com.example.its.web.file;

import com.example.its.domain.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 担当者プロフィール画像のアップロード
     * 
     * @param file アップロードするファイル
     * @return アップロード結果（ファイルパスとメタデータ）
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-CSRF-TOKEN", required = false) String csrfToken) {
        try {
            log.info("=== アバター画像アップロード開始 ===");
            log.info("ファイル名: {}", file.getOriginalFilename());
            log.info("コンテンツタイプ: {}", file.getContentType());
            log.info("ファイルサイズ: {} bytes", file.getSize());
            log.info("ファイルが空か: {}", file.isEmpty());
            log.info("CSRFトークン受信: {}", csrfToken != null ? "あり" : "なし");

            // ファイルアップロード処理
            String filePath = fileUploadService.uploadAvatarImage(file);

            // レスポンス作成
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("message", "ファイルのアップロードが完了しました");

            log.info("=== アバター画像アップロード完了 ===");
            log.info("保存パス: {}", filePath);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("=== アバター画像アップロード: バリデーションエラー ===");
            log.error("エラー詳細: {}", e.getMessage(), e);
            return createErrorResponse(HttpStatus.BAD_REQUEST, "ファイル形式エラー", e.getMessage());
        } catch (Exception e) {
            log.error("=== アバター画像アップロード: 予期しないエラー ===");
            log.error("エラークラス: {}", e.getClass().getSimpleName());
            log.error("エラーメッセージ: {}", e.getMessage());
            log.error("スタックトレース: ", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "アップロードエラー", "ファイルのアップロード中にエラーが発生しました: " + e.getMessage());
        }
    }

    /**
     * エラーレスポンスを作成
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
} 