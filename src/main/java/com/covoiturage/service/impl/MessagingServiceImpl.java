package com.covoiturage.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.entity.Conversation;
import com.covoiturage.entity.Message;
import com.covoiturage.repository.ConversationRepository;
import com.covoiturage.repository.MessageRepository;
import com.covoiturage.service.MessagingService;
import com.covoiturage.service.UserService;

@Service
public class MessagingServiceImpl implements MessagingService {
    private final MessageRepository messages;
    private final ConversationRepository conversations;
    private final UserService users;

    public MessagingServiceImpl(MessageRepository messages, ConversationRepository conversations, UserService users) {
        this.messages = messages;
        this.conversations = conversations;
        this.users = users;
    }

    public List<Message> conversation(String userCin, String otherCin) {
        return messages.findBySenderCinAndRecipientCinOrRecipientCinAndSenderCinOrderByTimestampAsc(userCin, otherCin, userCin, otherCin)
                .stream()
                .peek(m -> {
                    if (Boolean.TRUE.equals(m.getIsDeleted())) {
                        m.setContent("Message supprimé");
                    }
                })
                .toList();
    }

    @Transactional
    public Message send(String senderCin, String senderName, String recipientCin, String recipientName, String content, String trajetId) {
        return messages.save(Message.builder()
                .messageId("MSG_" + UUID.randomUUID())
                .senderCin(senderCin)
                .senderName(senderName)
                .recipientCin(recipientCin)
                .recipientName(recipientName)
                .content(content)
                .timestamp(LocalDateTime.now())
                .isDeleted(false)
                .trajetId(trajetId)
                .build());
    }

    @Transactional
    public void deleteMessage(String messageId, String requesterCin, boolean requesterIsAdmin) {
        messages.findById(messageId).filter(m -> requesterIsAdmin || requesterCin.equals(m.getSenderCin())).ifPresent(m -> {
            m.setIsDeleted(true);
            m.setContent("Message supprimé");
            messages.save(m);
        });
    }

    public List<Conversation> adminConversations() {
        List<Conversation> convs = conversations.findAllByOrderByCreatedAtDesc();
        // Enrich conversations with user names
        convs.forEach(c -> {
            String displayName = users.passager(c.getUserId())
                    .map(p -> p.getPrenom() + " " + p.getNom())
                    .or(() -> users.conducteur(c.getUserId())
                            .map(cd -> cd.getPrenom() + " " + cd.getNom()))
                    .orElse(c.getUserId());
            c.setUserName(displayName);
        });
        return convs;
    }

    @Transactional
    public Conversation getOrCreateAdminConversation(String userCin, String adminCin, String triggeredBy) {
        return conversations.findByUserIdAndAdminId(userCin, adminCin).orElseGet(() -> conversations.save(Conversation.builder()
                .id("CONV_" + UUID.randomUUID())
                .userId(userCin)
                .adminId(adminCin)
                .triggeredBy(triggeredBy == null || triggeredBy.isBlank() ? "admin" : triggeredBy)
                .createdAt(LocalDateTime.now())
                .build()));
    }

    public List<Map<String, String>> userConversations(String userCin) {
        List<Message> userMessages = messages.findBySenderCinOrRecipientCinOrderByTimestampDesc(userCin, userCin);
        
        // Group by conversation partner
        Map<String, Map<String, String>> conversationMap = new LinkedHashMap<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (Message msg : userMessages) {
            String otherCin = userCin.equals(msg.getSenderCin()) ? msg.getRecipientCin() : msg.getSenderCin();
            String otherName = userCin.equals(msg.getSenderCin()) ? msg.getRecipientName() : msg.getSenderName();
            
            if (!conversationMap.containsKey(otherCin)) {
                Map<String, String> conv = new HashMap<>();
                conv.put("userId", otherCin);
                conv.put("userName", otherName);
                
                // Get user type
                String userType = "Utilisateur";
                if (users.admin(otherCin).isPresent()) {
                    userType = "Administrateur";
                } else if (users.conducteur(otherCin).isPresent()) {
                    userType = "Conducteur";
                } else if (users.passager(otherCin).isPresent()) {
                    userType = "Passager";
                }
                conv.put("userType", userType);
                
                // Format last message with preview (max 50 chars)
                String lastMessageContent = msg.getContent();
                if (Boolean.TRUE.equals(msg.getIsDeleted())) {
                    lastMessageContent = "Message supprimé";
                } else if (lastMessageContent.length() > 50) {
                    lastMessageContent = lastMessageContent.substring(0, 50) + "...";
                }
                conv.put("lastMessage", lastMessageContent);
                conv.put("lastMessageTime", msg.getTimestamp().format(timeFormatter));
                
                conversationMap.put(otherCin, conv);
            }
        }
        
        return new ArrayList<>(conversationMap.values());
    }
}
