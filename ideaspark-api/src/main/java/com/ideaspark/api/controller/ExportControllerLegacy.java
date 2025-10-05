package com.ideaspark.api.controller;

import com.ideaspark.api.repository.AIGenerationRepository;
import com.ideaspark.api.repository.IdeaRepository;
import com.ideaspark.api.repository.UserActivityRepository;
import com.ideaspark.api.service.interfaces.AuthService;
import com.ideaspark.api.service.interfaces.ExportService;
import com.ideaspark.api.service.interfaces.PdfExportService;
import com.ideaspark.shared.dto.ExportRequest;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.enums.SubscriptionType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export-legacy")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Export (Legacy)", description = "Legacy export endpoints")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006", "exp://192.168.1.100:8081"})
public class ExportControllerLegacy {

    private final ExportService exportService;
    private final AuthService authService;
    private final PdfExportService pdfExportService;
    private final IdeaRepository ideaRepository;
    private final AIGenerationRepository aiGenerationRepository;
    private final UserActivityRepository userActivityRepository;

    /**
     * Export user's ideas as PDF
     * Frontend Integration: Profile Screen -> Export Ideas
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Export ideas as PDF (legacy)", description = "Export user's ideas as a PDF (legacy controller)")
    @PostMapping("/ideas/pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> exportIdeasPdf(@RequestBody ExportRequest exportRequest, Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            log.info("Exporting ideas PDF for user: {}", userId);
            
            ByteArrayOutputStream pdfStream = pdfExportService.generateIdeasPdf(userId, exportRequest);
            
            String filename = "IdeaSpark_Ideas_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfStream.size());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdfStream.toByteArray()));
                    
        } catch (Exception e) {
            log.error("Error exporting ideas PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<String>builder()
                            .success(false)
                            .message("Failed to export ideas PDF: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Export AI chat history as PDF
     * Frontend Integration: AI Chat Screen -> Export Chat History
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Export chat history as PDF (legacy)", description = "Export user's AI chat history as a PDF (legacy controller)")
    @PostMapping("/chat/pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> exportChatHistoryPdf(@RequestBody ExportRequest exportRequest, Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            log.info("Exporting chat history PDF for user: {}", userId);
            
            ByteArrayOutputStream pdfStream = pdfExportService.generateChatHistoryPdf(userId, exportRequest);
            
            String filename = "IdeaSpark_ChatHistory_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfStream.size());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdfStream.toByteArray()));
                    
        } catch (Exception e) {
            log.error("Error exporting chat history PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<String>builder()
                            .success(false)
                            .message("Failed to export chat history PDF: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Export AI generations (Writer, Code Assistant, Image Generator, etc.) as PDF
     * Frontend Integration: Multiple AI screens -> Export Generations
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Export AI generations as PDF (legacy)", description = "Export AI-generated content as PDF (legacy controller)")
    @PostMapping("/ai-generations/pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> exportAIGenerationsPdf(@RequestBody ExportRequest exportRequest, Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            log.info("Exporting AI generations PDF for user: {}", userId);
            
            ByteArrayOutputStream pdfStream = pdfExportService.generateAIGenerationsPdf(userId, exportRequest);
            
            String filename = "IdeaSpark_AIGenerations_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfStream.size());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdfStream.toByteArray()));
                    
        } catch (Exception e) {
            log.error("Error exporting AI generations PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<String>builder()
                            .success(false)
                            .message("Failed to export AI generations PDF: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Export complete user data as comprehensive PDF
     * Frontend Integration: Settings Screen -> Export All Data
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Export complete user data as PDF (legacy)", description = "Export full user data as a PDF (legacy controller)")
    @PostMapping("/complete/pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> exportCompleteUserDataPdf(@RequestBody ExportRequest exportRequest, Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            log.info("Exporting complete user data PDF for user: {}", userId);
            
            ByteArrayOutputStream pdfStream = pdfExportService.generateCompleteUserPdf(userId, exportRequest);
            
            String filename = "IdeaSpark_CompleteExport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfStream.size());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdfStream.toByteArray()));
                    
        } catch (Exception e) {
            log.error("Error exporting complete user data PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<String>builder()
                            .success(false)
                            .message("Failed to export complete user data PDF: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get export statistics and available data counts
     * Frontend Integration: Export screens -> Show available data counts
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Get export statistics (legacy)", description = "Returns counts and stats for exportable data (legacy controller)")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getExportStats(Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("ideasCount", ideaRepository.countByUserId(userId));
            stats.put("aiGenerationsCount", aiGenerationRepository.countByUserId(userId));
            stats.put("chatHistoryCount", aiGenerationRepository.countByUserIdAndType(userId, "CHAT"));
            stats.put("activitiesCount", userActivityRepository.countByUserId(userId));
            
            // AI generations by type
            Map<String, Long> aiGenerationsByType = new HashMap<>();
            aiGenerationsByType.put("CHAT", aiGenerationRepository.countByUserIdAndType(userId, "CHAT"));
            aiGenerationsByType.put("WRITER", aiGenerationRepository.countByUserIdAndType(userId, "WRITER"));
            aiGenerationsByType.put("CODE", aiGenerationRepository.countByUserIdAndType(userId, "CODE"));
            aiGenerationsByType.put("IMAGE", aiGenerationRepository.countByUserIdAndType(userId, "IMAGE"));
            aiGenerationsByType.put("IDEA", aiGenerationRepository.countByUserIdAndType(userId, "IDEA"));
            aiGenerationsByType.put("NEWS_DETECTION", aiGenerationRepository.countByUserIdAndType(userId, "NEWS_DETECTION"));
            aiGenerationsByType.put("TEXT_TOOLS", aiGenerationRepository.countByUserIdAndType(userId, "TEXT_TOOLS"));
            aiGenerationsByType.put("VOICE_TOOLS", aiGenerationRepository.countByUserIdAndType(userId, "VOICE_TOOLS"));
            stats.put("aiGenerationsByType", aiGenerationsByType);
            
            // Ideas by status
            Map<String, Long> ideasByStatus = new HashMap<>();
            ideasByStatus.put("DRAFT", ideaRepository.countByUserIdAndStatus(userId, "DRAFT"));
            ideasByStatus.put("ACTIVE", ideaRepository.countByUserIdAndStatus(userId, "ACTIVE"));
            ideasByStatus.put("COMPLETED", ideaRepository.countByUserIdAndStatus(userId, "COMPLETED"));
            ideasByStatus.put("ARCHIVED", ideaRepository.countByUserIdAndStatus(userId, "ARCHIVED"));
            stats.put("ideasByStatus", ideasByStatus);
            
            return ResponseEntity.ok(ResponseDTO.<Map<String, Object>>builder()
                    .success(true)
                    .message("Export statistics retrieved successfully")
                    .data(stats)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving export stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to retrieve export statistics: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get available export templates and formats
     * Frontend Integration: Export configuration screen
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Get export templates (legacy)", description = "Returns available templates and formats for exports (legacy controller)")
    @GetMapping("/templates")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getExportTemplates() {
        try {
            Map<String, Object> templates = new HashMap<>();
            
            // Available PDF templates
            Map<String, String> pdfTemplates = new HashMap<>();
            pdfTemplates.put("MODERN", "Modern design with gradients and clean layout");
            pdfTemplates.put("CLASSIC", "Traditional design with serif fonts and borders");
            pdfTemplates.put("MINIMAL", "Clean and simple design with minimal styling");
            templates.put("pdfTemplates", pdfTemplates);
            
            // Available export formats
            Map<String, String> formats = new HashMap<>();
            formats.put("DETAILED", "Complete information with full content");
            formats.put("SUMMARY", "Overview with key information only");
            formats.put("MINIMAL", "Basic information and titles only");
            templates.put("formats", formats);
            
            // Available data types
            Map<String, String> dataTypes = new HashMap<>();
            dataTypes.put("IDEAS", "Your creative ideas and concepts");
            dataTypes.put("CHAT_HISTORY", "AI chat conversations");
            dataTypes.put("AI_GENERATIONS", "All AI-generated content");
            dataTypes.put("USER_ACTIVITY", "Your activity and usage log");
            dataTypes.put("ALL", "Complete user data export");
            templates.put("dataTypes", dataTypes);
            
            return ResponseEntity.ok(ResponseDTO.<Map<String, Object>>builder()
                    .success(true)
                    .message("Export templates retrieved successfully")
                    .data(templates)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving export templates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to retrieve export templates: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Preview export data before generating PDF
     * Frontend Integration: Export preview screen
     */
    @io.swagger.v3.oas.annotations.Operation(summary = "Preview export data (legacy)", description = "Generate a preview for an export request (legacy controller)")
    @PostMapping("/preview")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> previewExportData(
            @RequestBody ExportRequest exportRequest, Authentication auth) {
        try {
            String userId = getUserIdFromAuth(auth);
            
            Map<String, Object> preview = new HashMap<>();
            
            switch (exportRequest.getDataType().toUpperCase()) {
                case "IDEAS":
                    long ideasCount = ideaRepository.countByUserId(userId);
                    preview.put("totalItems", ideasCount);
                    preview.put("type", "Ideas");
                    preview.put("description", "Your creative ideas and concepts");
                    break;
                    
                case "CHAT_HISTORY":
                    long chatCount = aiGenerationRepository.countByUserIdAndType(userId, "CHAT");
                    preview.put("totalItems", chatCount);
                    preview.put("type", "Chat History");
                    preview.put("description", "AI chat conversations and interactions");
                    break;
                    
                case "AI_GENERATIONS":
                    long aiCount = aiGenerationRepository.countByUserId(userId);
                    preview.put("totalItems", aiCount);
                    preview.put("type", "AI Generations");
                    preview.put("description", "All AI-generated content across different tools");
                    break;
                    
                case "USER_ACTIVITY":
                    long activityCount = userActivityRepository.countByUserId(userId);
                    preview.put("totalItems", activityCount);
                    preview.put("type", "User Activity");
                    preview.put("description", "Your usage patterns and activity log");
                    break;
                    
                case "ALL":
                    long totalIdeas = ideaRepository.countByUserId(userId);
                    long totalAI = aiGenerationRepository.countByUserId(userId);
                    long totalActivity = userActivityRepository.countByUserId(userId);
                    preview.put("totalItems", totalIdeas + totalAI + totalActivity);
                    preview.put("type", "Complete Export");
                    preview.put("description", "All your data including ideas, AI generations, and activity");
                    preview.put("breakdown", Map.of(
                        "ideas", totalIdeas,
                        "aiGenerations", totalAI,
                        "activities", totalActivity
                    ));
                    break;
                    
                default:
                    preview.put("totalItems", 0);
                    preview.put("type", "Unknown");
                    preview.put("description", "Unknown data type");
            }
            
            preview.put("exportFormat", exportRequest.getExportType());
            preview.put("templateStyle", exportRequest.getTemplateStyle());
            preview.put("includeImages", exportRequest.isIncludeImages());
            preview.put("estimatedSize", estimateFileSize(preview));
            
            return ResponseEntity.ok(ResponseDTO.<Map<String, Object>>builder()
                    .success(true)
                    .message("Export preview generated successfully")
                    .data(preview)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error generating export preview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to generate export preview: " + e.getMessage())
                            .build());
        }
    }

    // EXISTING EXPORT SERVICE ENDPOINTS

    @GetMapping("/user-data")
    @PreAuthorize("hasRole('PREMIUM') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Export user data (legacy)", description = "Export user data in requested format (legacy controller)")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> exportUserData(
            @RequestParam(defaultValue = "JSON") String format,
            HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<Map<String, Object>> response = exportService.exportUserData(email, format);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usage-stats")
    @PreAuthorize("hasRole('PREMIUM') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Export usage statistics (legacy)", description = "Export usage statistics in a given date range (legacy controller)")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> exportUsageStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "CSV") String format,
            HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            
            ResponseDTO<Map<String, Object>> response = exportService.exportUsageStatistics(email, start, end, format);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_DATE", "Invalid date format. Use yyyy-MM-dd"));
        }
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('PREMIUM') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Generate custom export (legacy)", description = "Generate a custom export for selected data types (legacy controller)")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> generateCustomExport(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        String token = extractTokenFromRequest(httpRequest);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        
        try {
            String exportType = (String) request.get("exportType");
            List<String> dataTypes = (List<String>) request.get("dataTypes");
            String format = (String) request.get("format");
            
            Map<String, String> dateRange = (Map<String, String>) request.get("dateRange");
            LocalDateTime startDate = LocalDateTime.parse(dateRange.get("startDate") + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(dateRange.get("endDate") + "T23:59:59");
            
            ResponseDTO<Map<String, Object>> response = exportService.generateCustomExport(
                email, exportType, dataTypes, format, startDate, endDate);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_REQUEST", "Invalid request format"));
        }
    }

    @GetMapping("/download/{exportId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Download export file (legacy)", description = "Download a previously generated export file (legacy controller)")
    public ResponseEntity<?> downloadExportFile(
            @PathVariable String exportId,
            HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<byte[]> response = exportService.downloadExportFile(exportId, email);
        
        if (response.isSuccess()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "user_data_export.json");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getData());
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status/{exportId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get export status (legacy)", description = "Get status of a generated export (legacy controller)")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getExportStatus(
            @PathVariable String exportId,
            HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<Map<String, Object>> response = exportService.getExportStatus(exportId, email);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String getUserIdFromAuth(Authentication auth) {
        // Extract user ID from authentication token
        // This depends on your JWT implementation
        return auth.getName(); // Adjust based on your auth implementation
    }
    
    private String estimateFileSize(Map<String, Object> preview) {
        long totalItems = (Long) preview.get("totalItems");
        boolean includeImages = (Boolean) preview.getOrDefault("includeImages", false);
        
        // Rough estimate: 1KB per item + 50KB for images if included
        long estimatedBytes = totalItems * 1024;
        if (includeImages) {
            estimatedBytes += totalItems * 51200; // ~50KB per image
        }
        
        if (estimatedBytes < 1024) {
            return estimatedBytes + " bytes";
        } else if (estimatedBytes < 1024 * 1024) {
            return String.format("%.1f KB", estimatedBytes / 1024.0);
        } else {
            return String.format("%.1f MB", estimatedBytes / (1024.0 * 1024.0));
        }
    }
}