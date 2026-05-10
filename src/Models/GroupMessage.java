package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GroupMessage - Représente un message envoyé dans la discussion d'un groupe.
 * Contrairement à Message (privé entre deux utilisateurs), ce message est lié à un groupId
 * et peut être lu par tous les membres du groupe.
 */
public class GroupMessage {

    private String messageId;
    private String groupId;
    private String senderCin;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private boolean isDeleted;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GroupMessage(String messageId, String groupId, String senderCin, String senderName,
                        String content, LocalDateTime timestamp, boolean isDeleted) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.senderCin = senderCin;
        this.senderName = senderName;
        this.content = isDeleted ? "message supprimée" : content;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
    }

    public GroupMessage(String messageId, String groupId, String senderCin, String senderName,
                        String content) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.senderCin = senderCin;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
    }

    public String getMessageId() { return messageId; }
    public String getGroupId() { return groupId; }
    public String getSenderCin() { return senderCin; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isDeleted() { return isDeleted; }

    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setSenderCin(String senderCin) { this.senderCin = senderCin; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setContent(String content) { this.content = content; this.isDeleted = false; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public void delete() {
        this.isDeleted = true;
        this.content = "message supprimée";
    }

    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.format(FORMATTER) : "";
    }

    public String getTimeOnly() {
        return timestamp != null ? timestamp.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }

    public static LocalDateTime parseTimestamp(String dateString) {
        try {
            return LocalDateTime.parse(dateString, FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
