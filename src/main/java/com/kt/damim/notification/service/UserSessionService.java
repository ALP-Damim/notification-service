package com.kt.damim.notification.service;

import com.kt.damim.notification.entity.UserSession;
import com.kt.damim.notification.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {
    
    private final UserSessionRepository userSessionRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    
    /**
     * 임시 세션 등록
     */
    @Transactional
    public void registerTemporarySession(String sessionId) {
        UserSession tempSession = UserSession.builder()
                .userId("TEMP_" + sessionId)
                .sessionId(sessionId)
                .connectedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        userSessionRepository.save(tempSession);
        log.info("임시 세션 등록: sessionId={}", sessionId);
    }
    
    /**
     * 임시 세션을 실제 사용자 세션으로 업데이트
     */
    @Transactional
    public void updateSessionWithUserId(String sessionId, String userId) {
        // 기존 활성 세션 비활성화
        List<UserSession> existingSessions = userSessionRepository.findByUserIdAndIsActiveTrue(userId);
        existingSessions.forEach(session -> {
            session.setActive(false);
            session.setDisconnectedAt(LocalDateTime.now());
        });
        userSessionRepository.saveAll(existingSessions);
        
        // 임시 세션을 실제 사용자 ID로 업데이트
        userSessionRepository.findBySessionIdAndIsActiveTrue(sessionId)
                .ifPresent(session -> {
                    session.setUserId(userId);
                    userSessionRepository.save(session);
                    log.info("사용자 세션 등록: userId={}, sessionId={}", userId, sessionId);
                });
    }
    

    
    /**
     * 사용자 세션 해제
     */
    @Transactional
    public void unregisterUserSession(String sessionId) {
        userSessionRepository.findBySessionIdAndIsActiveTrue(sessionId)
                .ifPresent(session -> {
                    session.setActive(false);
                    session.setDisconnectedAt(LocalDateTime.now());
                    userSessionRepository.save(session);
                    log.info("사용자 세션 해제: userId={}, sessionId={}", session.getUserId(), sessionId);
                });
    }
    
    /**
     * 특정 사용자에게 메시지 전송
     */
    @Transactional(readOnly = true)
    public boolean sendMessageToUser(String userId, String destination, Object message) {
        List<UserSession> activeSessions = userSessionRepository.findByUserIdAndIsActiveTrue(userId);
        
        if (activeSessions.isEmpty()) {
            log.warn("사용자가 연결되어 있지 않음: userId={}", userId);
            return false;
        }
        
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.info("메시지 전송 성공: userId={}, destination={}", userId, destination);
            return true;
        } catch (Exception e) {
            log.error("메시지 전송 실패: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }
    

    

}
