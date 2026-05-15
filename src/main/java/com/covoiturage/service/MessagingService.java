package com.covoiturage.service;

import java.util.List;
import java.util.Map;

import com.covoiturage.entity.Conversation;
import com.covoiturage.entity.Message;

public interface MessagingService {
    List<Message> conversation(String userCin, String otherCin);
    Message send(String senderCin, String senderName, String recipientCin, String recipientName, String content, String trajetId);
    void deleteMessage(String messageId, String requesterCin, boolean requesterIsAdmin);
    List<Conversation> adminConversations();
    Conversation getOrCreateAdminConversation(String userCin, String adminCin, String triggeredBy);
    List<Map<String, String>> userConversations(String userCin);
}
