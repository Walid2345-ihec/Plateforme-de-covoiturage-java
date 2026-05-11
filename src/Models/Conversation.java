package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Conversation between a platform user and an administrator.
 */
public class Conversation {
    private String id;
    private String userId;
    private String adminId;
    private String triggeredBy;
    private LocalDateTime createdAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Conversation(String id, String userId, String adminId, String triggeredBy, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.adminId = adminId;
        this.triggeredBy = triggeredBy;
        this.createdAt = createdAt;
    }

    public Conversation(String id, String userId, String adminId, String triggeredBy) {
        this(id, userId, adminId, triggeredBy, LocalDateTime.now());
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getAdminId() { return adminId; }
    public String getTriggeredBy() { return triggeredBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getCreatedAtAsString() {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }

    public static LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
