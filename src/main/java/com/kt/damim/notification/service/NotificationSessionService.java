package com.kt.damim.notification.service;

import com.kt.damim.notification.entity.NotificationSession;
import com.kt.damim.notification.repository.NotificationSessionRepository;
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
public class NotificationSessionService {
    
    private final NotificationSessionRepository notificationSessionRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    
    /**
     * 임시 세션 등록
     */
    @Transactional
    public void registerTemporarySession(String socketSessionId) {
        NotificationSession tempSession = NotificationSession.builder()
                .socketUserId("TEMP_" + socketSessionId)
                .socketSessionId(socketSessionId)
                .connectedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        notificationSessionRepository.save(tempSession);
        log.info("임시 세션 등록: socketSessionId={}", socketSessionId);
    }
    
    /**
     * 임시 세션을 실제 사용자 세션으로 업데이트
     */
    @Transactional
    public void updateSessionWithUserId(String socketSessionId, String socketUserId) {
        // 기존 활성 세션 비활성화
        List<NotificationSession> existingSessions = notificationSessionRepository.findBySocketUserIdAndIsActiveTrue(socketUserId);
        existingSessions.forEach(session -> {
            session.setActive(false);
            session.setDisconnectedAt(LocalDateTime.now());
        });
        notificationSessionRepository.saveAll(existingSessions);
        
        // 임시 세션을 실제 사용자 ID로 업데이트
        notificationSessionRepository.findBySocketSessionIdAndIsActiveTrue(socketSessionId)
                .ifPresent(session -> {
                    session.setSocketUserId(socketUserId);
                    notificationSessionRepository.save(session);
                    log.info("사용자 세션 등록: socketUserId={}, socketSessionId={}", socketUserId, socketSessionId);
                });
    }
    

    
    /**
     * 사용자 세션 해제
     */
    @Transactional
    public void unregisterUserSession(String socketSessionId) {
        notificationSessionRepository.findBySocketSessionIdAndIsActiveTrue(socketSessionId)
                .ifPresent(session -> {
                    session.setActive(false);
                    session.setDisconnectedAt(LocalDateTime.now());
                    notificationSessionRepository.save(session);
                    log.info("사용자 세션 해제: socketUserId={}, socketSessionId={}", session.getSocketUserId(), socketSessionId);
                });
    }
    
    /**
     * 특정 사용자에게 메시지 전송
     */
    @Transactional(readOnly = true)
    public boolean sendMessageToUser(String socketUserId, String destination, Object message) {
        List<NotificationSession> activeSessions = notificationSessionRepository.findBySocketUserIdAndIsActiveTrue(socketUserId);
        
        if (activeSessions.isEmpty()) {
            log.warn("사용자가 연결되어 있지 않음: socketUserId={}", socketUserId);
            return false;
        }
        
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.info("메시지 전송 성공: socketUserId={}, destination={}", socketUserId, destination);
            return true;
        } catch (Exception e) {
            log.error("메시지 전송 실패: socketUserId={}, error={}", socketUserId, e.getMessage());
            return false;
        }
    }
    

    

}
