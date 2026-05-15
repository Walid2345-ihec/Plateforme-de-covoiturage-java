package com.covoiturage.repository;
import com.covoiturage.entity.GroupMessage;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface GroupMessageRepository extends JpaRepository<GroupMessage,String>{java.util.List<GroupMessage> findByGroupIdOrderByTimestampAsc(String groupId);}