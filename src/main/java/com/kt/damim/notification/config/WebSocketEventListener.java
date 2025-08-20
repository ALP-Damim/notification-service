package com.kt.damim.notification.config;

import com.kt.damim.notification.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    
    private final UserSessionService userSessionService;
    
    /**
     * WebSocket 연결 완료 시 임시 세션 등록
     */
    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket 연결 완료: sessionId={}", sessionId);
        userSessionService.registerTemporarySession(sessionId);
    }
    
    /**
     * 토픽 구독 시 사용자 세션 등록
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        // 알림 토픽 구독 시 사용자 ID 추출
        if (destination != null && destination.startsWith("/topic/notifications/")) {
            String userId = destination.substring("/topic/notifications/".length());
            log.info("사용자 구독: userId={}, sessionId={}", userId, sessionId);
            userSessionService.updateSessionWithUserId(sessionId, userId);
        }
    }
    
    /**
     * WebSocket 연결 해제 시 세션 정리
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket 연결 해제: sessionId={}", sessionId);
        userSessionService.unregisterUserSession(sessionId);
    }
}
