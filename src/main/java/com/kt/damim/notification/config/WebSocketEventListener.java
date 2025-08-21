package com.kt.damim.notification.config;

import com.kt.damim.notification.service.NotificationSessionService;
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
    
    private final NotificationSessionService notificationSessionService;
    
    /**
     * WebSocket 연결 완료 시 임시 세션 등록
     */
    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String socketSessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket 연결 완료: socketSessionId={}", socketSessionId);
        notificationSessionService.registerTemporarySession(socketSessionId);
    }
    
    /**
     * 토픽 구독 시 사용자 세션 등록
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String socketSessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        // 알림 토픽 구독 시 사용자 ID 추출
        if (destination != null && destination.startsWith("/topic/notifications/")) {
            String socketUserId = destination.substring("/topic/notifications/".length());
            log.info("사용자 구독: socketUserId={}, socketSessionId={}", socketUserId, socketSessionId);
            notificationSessionService.updateSessionWithUserId(socketSessionId, socketUserId);
        }
    }
    
    /**
     * WebSocket 연결 해제 시 세션 정리
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String socketSessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket 연결 해제: socketSessionId={}", socketSessionId);
        notificationSessionService.unregisterUserSession(socketSessionId);
    }
}
