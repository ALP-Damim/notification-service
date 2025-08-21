package com.kt.damim.notification.repository;

import com.kt.damim.notification.entity.NotificationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSessionRepository extends JpaRepository<NotificationSession, Long> {
    
    // 특정 사용자의 활성 세션 조회
    List<NotificationSession> findBySocketUserIdAndIsActiveTrue(String socketUserId);
    
    // 특정 세션 ID로 활성 세션 조회
    Optional<NotificationSession> findBySocketSessionIdAndIsActiveTrue(String socketSessionId);
    

}
