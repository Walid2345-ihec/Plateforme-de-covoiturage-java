package com.covoiturage.repository;

import com.covoiturage.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    List<Conversation> findAllByOrderByCreatedAtDesc();
    Optional<Conversation> findByUserIdAndAdminId(String userId, String adminId);
}
