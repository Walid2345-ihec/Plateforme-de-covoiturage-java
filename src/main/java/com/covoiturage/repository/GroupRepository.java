package com.covoiturage.repository;
import com.covoiturage.entity.Group;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface GroupRepository extends JpaRepository<Group,String>{java.util.List<Group> findByConducteurCin(String conducteurCin);
 java.util.List<Group> findByMemberCinsContaining(String cin);}