package com.ideaspark.api.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.ideaspark.api.exception.ExceptionUtils;
import com.ideaspark.api.service.interfaces.CloudinaryService;
import com.ideaspark.shared.exception.CloudinaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "folder", folder,
            "resource_type", "image",
            "transformation", new Transformation().quality("auto").fetchFormat("auto")
        );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            log.info("Image uploaded successfully to Cloudinary: {}", uploadResult.get("public_id"));
            
            return uploadResult;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            ExceptionUtils.logAndThrowCloudinaryError("image upload", e);
            return null; // Never reached
        }
    }

    @Override
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        try {
            if (publicId == null || publicId.trim().isEmpty()) {
                throw new IllegalArgumentException("Public ID cannot be null or empty");
            }

            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully from Cloudinary: {}", publicId);
            
            return deleteResult;
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
            ExceptionUtils.logAndThrowCloudinaryError("image deletion", e);
            return null; // Never reached
        }
    }

    @Override
    public Map<String, Object> updateImage(MultipartFile file, String oldPublicId, String folder) throws IOException {
        try {
            // Delete old image if it exists
            if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
                try {
                    deleteImage(oldPublicId);
                } catch (IOException e) {
                    log.warn("Failed to delete old image, proceeding with upload: {}", e.getMessage());
                }
            }

            // Upload new image
            return uploadImage(file, folder);
        } catch (IOException e) {
            log.error("Failed to update image in Cloudinary: {}", e.getMessage(), e);
            ExceptionUtils.logAndThrowCloudinaryError("image update", e);
            return null; // Never reached
        }
    }

    @Override
    public String getImageUrl(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }
        
        return cloudinary.url().generate(publicId);
    }

    @Override
    public String getOptimizedImageUrl(String publicId, int width, int height) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }
        
        return cloudinary.url()
                .transformation(new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto")
                        .fetchFormat("auto"))
                .generate(publicId);
    }

    @Override
    public String generateOptimizedUrl(String publicId, int width, int height, String format) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }
        
        return cloudinary.url()
                .transformation(new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto")
                        .fetchFormat(format != null ? format : "auto"))
                .generate(publicId);
    }

    @Override
    public boolean isValidImageFormat(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    @Override
    public long getMaxFileSize() {
        return 5 * 1024 * 1024; // 5MB
    }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Extract public ID from Cloudinary URL
            // URL format: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/filename.ext
            String[] parts = imageUrl.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                String secondLastPart = parts[parts.length - 2];
                
                // Remove file extension
                String filename = lastPart.contains(".") ? lastPart.substring(0, lastPart.lastIndexOf(".")) : lastPart;
                
                return secondLastPart + "/" + filename;
            }
        } catch (Exception e) {
            log.error("Error extracting public ID from URL: {}", e.getMessage());
        }
        
        return null;
    }
}