package com.kt.damim.notification.repository;

import com.kt.damim.notification.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    // 특정 사용자의 활성 세션 조회
    List<UserSession> findByUserIdAndIsActiveTrue(String userId);
    
    // 특정 세션 ID로 활성 세션 조회
    Optional<UserSession> findBySessionIdAndIsActiveTrue(String sessionId);
    

}
