package com.covoiturage.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.covoiturage.entity.Message;
@Repository public interface MessageRepository extends JpaRepository<Message,String>{java.util.List<Message> findBySenderCinAndRecipientCinOrRecipientCinAndSenderCinOrderByTimestampAsc(String a,String b,String c,String d);java.util.List<Message> findBySenderCinOrRecipientCinOrderByTimestampDesc(String senderCin,String recipientCin);}