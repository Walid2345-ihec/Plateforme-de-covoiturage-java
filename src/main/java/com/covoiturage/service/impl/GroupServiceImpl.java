package com.covoiturage.service.impl;

import com.covoiturage.entity.Group;
import com.covoiturage.entity.GroupMessage;
import com.covoiturage.repository.GroupMessageRepository;
import com.covoiturage.repository.GroupRepository;
import com.covoiturage.service.GroupService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groups;
    private final GroupMessageRepository messages;
    private final NotificationService notifications;
    private final UserService users;

    public GroupServiceImpl(GroupRepository groups, GroupMessageRepository messages, NotificationService notifications, UserService users) {
        this.groups = groups;
        this.messages = messages;
        this.notifications = notifications;
        this.users = users;
    }

    public List<Group> groupsFor(String role, String cin) {
        if ("conducteur".equals(role)) {
            return groups.findByConducteurCin(cin);
        }
        return groups.findByMemberCinsContaining(cin);
    }

    @Transactional
    public Group create(String conducteurCin, String groupName, List<String> memberCins) {
        String members = memberCins.stream().map(String::trim).filter(s -> !s.isBlank()).distinct().reduce((a, b) -> a + "," + b).orElse("");
        Group group = groups.save(Group.builder()
                .groupId("GRP_" + System.currentTimeMillis() + "_" + conducteurCin)
                .groupName(groupName)
                .conducteurCin(conducteurCin)
                .memberCins(members)
                .dateCreation(LocalDateTime.now())
                .build());
        String conducteurNom = users.displayName("conducteur", conducteurCin);
        for (String memberCin : members.split(",")) {
            if (!memberCin.isBlank()) {
                notifications.notifyPassager(memberCin, conducteurCin, null, "GROUPE",
                        "Vous avez ete ajoute au groupe \"" + groupName + "\" par " + conducteurNom);
            }
        }
        return group;
    }

    public Group group(String groupId) {
        return groups.findById(groupId).orElseThrow();
    }

    public List<GroupMessage> messages(String groupId) {
        return messages.findByGroupIdOrderByTimestampAsc(groupId).stream()
                .peek(m -> {
                    if (Boolean.TRUE.equals(m.getIsDeleted())) {
                        m.setContent("Message supprimé");
                    }
                })
                .toList();
    }

    @Transactional
    public GroupMessage sendMessage(String groupId, String senderCin, String senderName, String content) {
        Group group = group(groupId);
        boolean isMember = senderCin.equals(group.getConducteurCin()) || Arrays.asList((group.getMemberCins() == null ? "" : group.getMemberCins()).split(",")).contains(senderCin);
        if (!isMember) {
            throw new IllegalArgumentException("Utilisateur hors du groupe");
        }
        GroupMessage message = messages.save(GroupMessage.builder()
                .messageId("GMSG_" + System.currentTimeMillis() + "_" + senderCin)
                .groupId(groupId)
                .senderCin(senderCin)
                .senderName(senderName)
                .content(content)
                .timestamp(LocalDateTime.now())
                .isDeleted(false)
                .build());
        String preview = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        for (String memberCin : (group.getMemberCins() == null ? "" : group.getMemberCins()).split(",")) {
            if (!memberCin.isBlank() && !memberCin.equals(senderCin)) {
                notifications.notifyPassager(memberCin, senderCin, null, "MESSAGE_GROUPE",
                        group.getGroupName() + " " + senderName + ": " + preview);
            }
        }
        if (!group.getConducteurCin().equals(senderCin)) {
            notifications.notifyConducteur(group.getConducteurCin(), senderCin, null, "MESSAGE_GROUPE",
                    group.getGroupName() + " " + senderName + ": " + preview);
        }
        return message;
    }

    @Transactional
    public void deleteMessage(String messageId, String requesterCin) {
        messages.findById(messageId).filter(m -> requesterCin.equals(m.getSenderCin())).ifPresent(m -> {
            m.setIsDeleted(true);
            m.setContent("Message supprimé");
            messages.save(m);
        });
    }
}
