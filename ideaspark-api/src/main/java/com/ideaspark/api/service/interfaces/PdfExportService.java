package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.ExportDataDTO;
import com.ideaspark.shared.dto.ExportRequest;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface PdfExportService {
    
    /**
     * Generate PDF report for user's ideas
     * @param userId User ID
     * @param exportRequest Export configuration
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateIdeasPdf(String userId, ExportRequest exportRequest);
    
    /**
     * Generate PDF report for AI generations
     * @param userId User ID
     * @param exportRequest Export configuration
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateAIGenerationsPdf(String userId, ExportRequest exportRequest);
    
    /**
     * Generate PDF report for chat history
     * @param userId User ID
     * @param exportRequest Export configuration
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateChatHistoryPdf(String userId, ExportRequest exportRequest);
    
    /**
     * Generate comprehensive user activity PDF
     * @param userId User ID
     * @param exportRequest Export configuration
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateUserActivityPdf(String userId, ExportRequest exportRequest);
    
    /**
     * Generate complete user data export PDF
     * @param userId User ID
     * @param exportRequest Export configuration
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateCompleteUserPdf(String userId, ExportRequest exportRequest);
    
    /**
     * Generate custom PDF from provided data
     * @param data List of export data
     * @param exportRequest Export configuration
     * @param title PDF title
     * @return PDF as byte array
     */
    ByteArrayOutputStream generateCustomPdf(List<ExportDataDTO> data, ExportRequest exportRequest, String title);
}