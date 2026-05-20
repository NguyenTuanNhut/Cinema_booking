package com.cinema.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload.dir:uploads/movies/}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif")
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Upload file ảnh phim
     * @param file MultipartFile từ form
     * @return URL relative để lưu vào database
     * @throws IOException nếu có lỗi
     */
    public String uploadMoviePoster(MultipartFile file) throws IOException {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Không thể đọc tên file");
        }

        // Validate extension
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file: JPG, JPEG, PNG, GIF");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }

        // Tạo thư mục nếu chưa tồn tại
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Tạo tên file unique
        String uniqueFileName = UUID.randomUUID() + "." + extension;
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        // Lưu file
        Files.write(filePath, file.getBytes());

        // Return relative path để dùng trong HTML
        return "/uploads/movies/" + uniqueFileName;
    }

    /**
     * Lấy extension của file
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Xóa file
     */
    public void deleteFile(String relativePath) {
        if (relativePath != null && !relativePath.isEmpty()) {
            try {
                // relativePath format: /uploads/movies/uuid.jpg
                String filePath = relativePath.startsWith("/") ?
                        relativePath.substring(1) : relativePath;
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                // Log hoặc xử lý lỗi (nếu cần)
                System.err.println("Không thể xóa file: " + relativePath);
            }
        }
    }
}

