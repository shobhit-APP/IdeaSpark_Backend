package com.ideaspark.api.service.impl;

import com.ideaspark.api.exception.ExceptionUtils;
import com.ideaspark.api.repository.AIGenerationRepository;
import com.ideaspark.api.repository.IdeaRepository;
import com.ideaspark.api.repository.UserActivityRepository;
import com.ideaspark.api.repository.UserRepository;
import com.ideaspark.api.service.interfaces.PdfExportService;
import com.ideaspark.shared.dto.ExportDataDTO;
import com.ideaspark.shared.dto.ExportRequest;
import com.ideaspark.shared.entity.AIGeneration;
import com.ideaspark.shared.entity.Idea;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.entity.UserActivity;
import com.ideaspark.shared.exception.PdfGenerationException;
import com.ideaspark.shared.exception.UserNotFoundException;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportServiceImpl implements PdfExportService {
    
    private final IdeaRepository ideaRepository;
    private final AIGenerationRepository aiGenerationRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;
    
    @Override
    public ByteArrayOutputStream generateIdeasPdf(String userId, ExportRequest exportRequest) {
        try {
            List<Idea> ideas = getIdeasByDateRange(userId, exportRequest);
            String html = generateIdeasHtml(ideas, exportRequest);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating ideas PDF for user {}: {}", userId, e.getMessage());
            ExceptionUtils.throwPdfGenerationError("ideas PDF generation", e);
            return null; // Never reached
        }
    }
    
    @Override
    public ByteArrayOutputStream generateAIGenerationsPdf(String userId, ExportRequest exportRequest) {
        try {
            List<AIGeneration> generations = getAIGenerationsByDateRange(userId, exportRequest);
            String html = generateAIGenerationsHtml(generations, exportRequest);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating AI generations PDF for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate AI generations PDF", e);
        }
    }
    
    @Override
    public ByteArrayOutputStream generateChatHistoryPdf(String userId, ExportRequest exportRequest) {
        try {
            List<AIGeneration> chatHistory = aiGenerationRepository.findByUserIdAndType(userId, "CHAT");
            String html = generateChatHistoryHtml(chatHistory, exportRequest);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating chat history PDF for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate chat history PDF", e);
        }
    }
    
    @Override
    public ByteArrayOutputStream generateUserActivityPdf(String userId, ExportRequest exportRequest) {
        try {
            List<UserActivity> activities = getUserActivitiesByDateRange(userId, exportRequest);
            String html = generateUserActivityHtml(activities, exportRequest);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating user activity PDF for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate user activity PDF", e);
        }
    }
    
    @Override
    public ByteArrayOutputStream generateCompleteUserPdf(String userId, ExportRequest exportRequest) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            List<Idea> ideas = getIdeasByDateRange(userId, exportRequest);
            List<AIGeneration> generations = getAIGenerationsByDateRange(userId, exportRequest);
            List<UserActivity> activities = getUserActivitiesByDateRange(userId, exportRequest);
            
            String html = generateCompleteUserHtml(user, ideas, generations, activities, exportRequest);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating complete user PDF for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate complete user PDF", e);
        }
    }
    
    @Override
    public ByteArrayOutputStream generateCustomPdf(List<ExportDataDTO> data, ExportRequest exportRequest, String title) {
        try {
            String html = generateCustomDataHtml(data, exportRequest, title);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating custom PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate custom PDF", e);
        }
    }
    
    private List<Idea> getIdeasByDateRange(String userId, ExportRequest exportRequest) {
        if (exportRequest.getDateFrom() != null && exportRequest.getDateTo() != null) {
            LocalDateTime startDate = LocalDateTime.parse(exportRequest.getDateFrom());
            LocalDateTime endDate = LocalDateTime.parse(exportRequest.getDateTo());
            return ideaRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        }
        return ideaRepository.findByUserId(userId);
    }
    
    private List<AIGeneration> getAIGenerationsByDateRange(String userId, ExportRequest exportRequest) {
        if (exportRequest.getDateFrom() != null && exportRequest.getDateTo() != null) {
            LocalDateTime startDate = LocalDateTime.parse(exportRequest.getDateFrom());
            LocalDateTime endDate = LocalDateTime.parse(exportRequest.getDateTo());
            return aiGenerationRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        }
        return aiGenerationRepository.findByUserId(userId);
    }
    
    private List<UserActivity> getUserActivitiesByDateRange(String userId, ExportRequest exportRequest) {
        if (exportRequest.getDateFrom() != null && exportRequest.getDateTo() != null) {
            LocalDateTime startDate = LocalDateTime.parse(exportRequest.getDateFrom());
            LocalDateTime endDate = LocalDateTime.parse(exportRequest.getDateTo());
            return userActivityRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate);
        }
        return userActivityRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    private String generateIdeasHtml(List<Idea> ideas, ExportRequest exportRequest) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("My Ideas Export", exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>My Ideas Collection</h1>");
        html.append("<p class='summary'>Total Ideas: ").append(ideas.size()).append("</p>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        for (Idea idea : ideas) {
            html.append("<div class='item'>");
            html.append("<h3>").append(escapeHtml(idea.getTitle())).append("</h3>");
            html.append("<p class='meta'>Category: ").append(idea.getCategory()).append(" | Status: ").append(idea.getStatus());
            html.append(" | Created: ").append(idea.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("</p>");
            html.append("<p class='description'>").append(escapeHtml(idea.getDescription())).append("</p>");
            
            if (idea.getTags() != null && !idea.getTags().isEmpty()) {
                html.append("<p class='tags'>Tags: ").append(String.join(", ", idea.getTags())).append("</p>");
            }
            
            if (idea.getImageUrl() != null && exportRequest.isIncludeImages()) {
                html.append("<img src='").append(idea.getImageUrl()).append("' class='idea-image' />");
            }
            
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String generateAIGenerationsHtml(List<AIGeneration> generations, ExportRequest exportRequest) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("AI Generations Export", exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>AI Generations History</h1>");
        html.append("<p class='summary'>Total Generations: ").append(generations.size()).append("</p>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        for (AIGeneration generation : generations) {
            html.append("<div class='item'>");
            html.append("<h3>").append(generation.getType()).append(" Generation</h3>");
            html.append("<p class='meta'>Model: ").append(generation.getModel()).append(" | Created: ");
            html.append(generation.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
            
            if (generation.getPrompt() != null) {
                html.append("<div class='prompt'><strong>Prompt:</strong><br/>").append(escapeHtml(generation.getPrompt())).append("</div>");
            }
            
            if (generation.getResponse() != null) {
                html.append("<div class='response'><strong>Response:</strong><br/>").append(escapeHtml(generation.getResponse())).append("</div>");
            }
            
            if (generation.getImageUrl() != null && exportRequest.isIncludeImages()) {
                html.append("<img src='").append(generation.getImageUrl()).append("' class='ai-image' />");
            }
            
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String generateChatHistoryHtml(List<AIGeneration> chatHistory, ExportRequest exportRequest) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Chat History Export", exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>AI Chat History</h1>");
        html.append("<p class='summary'>Total Conversations: ").append(chatHistory.size()).append("</p>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        // Group by session ID
        chatHistory.stream()
                .collect(Collectors.groupingBy(chat -> chat.getSessionId() != null ? chat.getSessionId() : "single"))
                .forEach((sessionId, chats) -> {
                    html.append("<div class='chat-session'>");
                    html.append("<h3>Chat Session: ").append(sessionId).append("</h3>");
                    
                    for (AIGeneration chat : chats) {
                        html.append("<div class='chat-item'>");
                        html.append("<div class='user-message'><strong>You:</strong> ").append(escapeHtml(chat.getPrompt())).append("</div>");
                        html.append("<div class='ai-response'><strong>AI:</strong> ").append(escapeHtml(chat.getResponse())).append("</div>");
                        html.append("<p class='chat-time'>").append(chat.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
                        html.append("</div>");
                    }
                    
                    html.append("</div>");
                });
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String generateUserActivityHtml(List<UserActivity> activities, ExportRequest exportRequest) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("User Activity Export", exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>User Activity Log</h1>");
        html.append("<p class='summary'>Total Activities: ").append(activities.size()).append("</p>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        for (UserActivity activity : activities) {
            html.append("<div class='activity-item'>");
            html.append("<h4>").append(activity.getActionType()).append("</h4>");
            html.append("<p class='meta'>").append(activity.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"))).append("</p>");
            
            if (activity.getDescription() != null) {
                html.append("<p>").append(escapeHtml(activity.getDescription())).append("</p>");
            }
            
            if (activity.getMetadata() != null) {
                html.append("<p class='metadata'>").append(escapeHtml(activity.getMetadata())).append("</p>");
            }
            
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String generateCompleteUserHtml(User user, List<Idea> ideas, List<AIGeneration> generations, 
                                          List<UserActivity> activities, ExportRequest exportRequest) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Complete User Export - " + user.getFullName(), exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>Complete User Export</h1>");
        html.append("<h2>User: ").append(escapeHtml(user.getFullName())).append("</h2>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        // User Summary
        html.append("<div class='summary-section'>");
        html.append("<h3>Summary</h3>");
        html.append("<ul>");
        html.append("<li>Total Ideas: ").append(ideas.size()).append("</li>");
        html.append("<li>AI Generations: ").append(generations.size()).append("</li>");
        html.append("<li>Activities: ").append(activities.size()).append("</li>");
        html.append("<li>Member Since: ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("</li>");
        html.append("</ul>");
        html.append("</div>");
        
        // Ideas Section
        if (!ideas.isEmpty()) {
            html.append("<div class='section'>");
            html.append("<h3>Ideas (").append(ideas.size()).append(")</h3>");
            for (Idea idea : ideas.stream().limit(10).collect(Collectors.toList())) {
                html.append("<div class='mini-item'>");
                html.append("<h4>").append(escapeHtml(idea.getTitle())).append("</h4>");
                html.append("<p>").append(escapeHtml(idea.getDescription().length() > 200 ? 
                    idea.getDescription().substring(0, 200) + "..." : idea.getDescription())).append("</p>");
                html.append("</div>");
            }
            html.append("</div>");
        }
        
        // AI Generations Section
        if (!generations.isEmpty()) {
            html.append("<div class='section'>");
            html.append("<h3>Recent AI Generations (").append(generations.size()).append(")</h3>");
            for (AIGeneration generation : generations.stream().limit(10).collect(Collectors.toList())) {
                html.append("<div class='mini-item'>");
                html.append("<h4>").append(generation.getType()).append(" - ").append(generation.getModel()).append("</h4>");
                html.append("<p><strong>Prompt:</strong> ").append(escapeHtml(generation.getPrompt())).append("</p>");
                html.append("</div>");
            }
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String generateCustomDataHtml(List<ExportDataDTO> data, ExportRequest exportRequest, String title) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader(title, exportRequest.getTemplateStyle()));
        
        html.append("<div class='content'>");
        html.append("<h1>").append(escapeHtml(title)).append("</h1>");
        html.append("<p class='summary'>Total Items: ").append(data.size()).append("</p>");
        html.append("<p class='export-date'>Exported on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("</p>");
        
        for (ExportDataDTO item : data) {
            html.append("<div class='item'>");
            html.append("<h3>").append(escapeHtml(item.getTitle())).append("</h3>");
            html.append("<p class='meta'>Type: ").append(item.getType()).append(" | Category: ").append(item.getCategory());
            html.append(" | Created: ").append(item.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("</p>");
            html.append("<p>").append(escapeHtml(item.getContent())).append("</p>");
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append(getHtmlFooter());
        return html.toString();
    }
    
    private String getHtmlHeader(String title, String templateStyle) {
        String cssClass = templateStyle != null ? templateStyle.toLowerCase() : "modern";
        
        return "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta charset='UTF-8'>" +
                "<title>" + escapeHtml(title) + "</title>" +
                "<style>" + getCssStyles(cssClass) + "</style>" +
                "</head><body class='" + cssClass + "'>";
    }
    
    private String getHtmlFooter() {
        return "<div class='footer'>" +
                "<p>Generated by IdeaSpark - Your AI-Powered Creativity Platform</p>" +
                "<p>© 2025 IdeaSpark. All rights reserved.</p>" +
                "</div></body></html>";
    }
    
    private String getCssStyles(String templateStyle) {
        switch (templateStyle) {
            case "classic":
                return getClassicCss();
            case "minimal":
                return getMinimalCss();
            default:
                return getModernCss();
        }
    }
    
    private String getModernCss() {
        return "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }" +
                ".content { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }" +
                "h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }" +
                "h2, h3 { color: #34495e; }" +
                ".item { margin: 20px 0; padding: 20px; border-left: 4px solid #3498db; background: #f8f9fa; border-radius: 5px; }" +
                ".meta { color: #7f8c8d; font-size: 0.9em; margin: 5px 0; }" +
                ".summary { background: #e8f5e8; padding: 15px; border-radius: 5px; font-weight: bold; color: #27ae60; }" +
                ".export-date { text-align: right; color: #95a5a6; font-style: italic; }" +
                ".footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #bdc3c7; color: #7f8c8d; }" +
                "img { max-width: 100%; height: auto; margin: 10px 0; border-radius: 5px; }";
    }
    
    private String getClassicCss() {
        return "body { font-family: 'Times New Roman', serif; line-height: 1.8; color: #2c2c2c; margin: 0; padding: 30px; background: #f5f5f5; }" +
                ".content { background: white; padding: 40px; border: 2px solid #8b4513; }" +
                "h1 { color: #8b4513; text-align: center; border-bottom: 2px solid #8b4513; padding-bottom: 15px; }" +
                "h2, h3 { color: #654321; }" +
                ".item { margin: 25px 0; padding: 20px; border: 1px solid #ddd; background: #fafafa; }" +
                ".meta { color: #666; font-style: italic; }" +
                ".summary { background: #f0f8ff; padding: 20px; border: 1px solid #b0c4de; text-align: center; }" +
                ".footer { text-align: center; margin-top: 40px; font-style: italic; color: #666; }";
    }
    
    private String getMinimalCss() {
        return "body { font-family: 'Helvetica Neue', Arial, sans-serif; line-height: 1.5; color: #444; margin: 0; padding: 20px; background: white; }" +
                ".content { max-width: 800px; margin: 0 auto; }" +
                "h1 { color: #222; font-weight: 300; border-bottom: 1px solid #eee; padding-bottom: 10px; }" +
                "h2, h3 { color: #333; font-weight: 400; }" +
                ".item { margin: 15px 0; padding: 15px 0; border-bottom: 1px solid #f0f0f0; }" +
                ".meta { color: #888; font-size: 0.85em; }" +
                ".summary { padding: 10px 0; color: #666; }" +
                ".footer { text-align: center; margin-top: 30px; color: #999; font-size: 0.9em; }";
    }
    
    private ByteArrayOutputStream convertHtmlToPdf(String html) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            HtmlConverter.convertToPdf(html, baos);
        } catch (Exception e) {
            log.error("Error converting HTML to PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to convert HTML to PDF", e);
        }
        return baos;
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
}