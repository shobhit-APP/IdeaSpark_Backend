package com.ideaspark.api.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    
    /**
     * Upload image to Cloudinary
     * @param file The image file to upload
     * @param folder The folder in Cloudinary to store the image
     * @return Map containing image details (url, public_id, etc.)
     */
    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;
    
    /**
     * Delete image from Cloudinary
     * @param publicId The public ID of the image to delete
     * @return Map containing deletion result
     */
    Map<String, Object> deleteImage(String publicId) throws IOException;
    
    /**
     * Update/Replace image in Cloudinary
     * @param file New image file
     * @param oldPublicId Public ID of old image to replace
     * @param folder Folder to store the new image
     * @return Map containing new image details
     */
    Map<String, Object> updateImage(MultipartFile file, String oldPublicId, String folder) throws IOException;
    
    /**
     * Get image URL
     * @param publicId Public ID of the image
     * @return Image URL
     */
    String getImageUrl(String publicId);
    
    /**
     * Get optimized image URL
     * @param publicId Public ID of the image
     * @param width Desired width
     * @param height Desired height
     * @return Optimized image URL
     */
    String getOptimizedImageUrl(String publicId, int width, int height);
    
    /**
     * Generate optimized image URL with format
     * @param publicId Public ID of the image
     * @param width Desired width
     * @param height Desired height
     * @param format Image format (auto, webp, jpg, etc.)
     * @return Optimized image URL
     */
    String generateOptimizedUrl(String publicId, int width, int height, String format);
    
    /**
     * Check if file is a valid image format
     * @param file File to check
     * @return true if valid image format
     */
    boolean isValidImageFormat(MultipartFile file);
    
    /**
     * Get maximum allowed file size
     * @return Maximum file size in bytes
     */
    long getMaxFileSize();
    
    /**
     * Extract public ID from Cloudinary URL
     * @param imageUrl Cloudinary image URL
     * @return Public ID
     */
    String extractPublicIdFromUrl(String imageUrl);
}