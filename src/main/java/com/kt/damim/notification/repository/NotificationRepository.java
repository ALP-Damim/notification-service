package com.kt.damim.notification.repository;

import com.kt.damim.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(String receiverId);
    
    List<Notification> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(String receiverId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiverId = :receiverId AND n.isRead = false")
    long countUnreadNotifications(@Param("receiverId") String receiverId);
}
