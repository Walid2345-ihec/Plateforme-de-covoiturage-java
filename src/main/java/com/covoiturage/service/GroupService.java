package com.covoiturage.service;

import com.covoiturage.entity.Group;
import com.covoiturage.entity.GroupMessage;
import java.util.List;

public interface GroupService {
    List<Group> groupsFor(String role, String cin);
    Group create(String conducteurCin, String groupName, List<String> memberCins);
    Group group(String groupId);
    List<GroupMessage> messages(String groupId);
    GroupMessage sendMessage(String groupId, String senderCin, String senderName, String content);
    void deleteMessage(String messageId, String requesterCin);
}
