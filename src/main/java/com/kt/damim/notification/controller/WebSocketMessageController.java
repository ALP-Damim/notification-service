package com.kt.damim.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {
    
    /**
     * 클라이언트로부터 받은 메시지를 처리하고 응답
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, Object> handleHello(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("=== STOMP 메시지 수신 ===");
        log.info("sessionId: {}", sessionId);
        log.info("message: {}", message);
        log.info("========================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "GREETING");
        response.put("message", "안녕하세요! " + message.get("name") + "님");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 사용자 등록 메시지 처리
     */
    @MessageMapping("/register")
    @SendTo("/topic/registration")
    public Map<String, Object> handleRegistration(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userId = (String) message.get("userId");
        
        log.info("=== 사용자 등록 메시지 ===");
        log.info("sessionId: {}", sessionId);
        log.info("userId: {}", userId);
        log.info("message: {}", message);
        log.info("=========================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "REGISTRATION");
        response.put("userId", userId);
        response.put("status", "SUCCESS");
        response.put("message", "사용자 등록이 완료되었습니다.");
        
        return response;
    }
    
    /**
     * 개인 메시지 처리 (특정 사용자에게만)
     */
    @MessageMapping("/private-message")
    public void handlePrivateMessage(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String targetUserId = (String) message.get("targetUserId");
        String content = (String) message.get("content");
        
        log.info("=== 개인 메시지 ===");
        log.info("sessionId: {}", sessionId);
        log.info("targetUserId: {}", targetUserId);
        log.info("content: {}", content);
        log.info("===================");
        
        // 여기서 개인 메시지 처리 로직 구현
        // 예: NotificationService를 통해 알림 전송
    }
}
