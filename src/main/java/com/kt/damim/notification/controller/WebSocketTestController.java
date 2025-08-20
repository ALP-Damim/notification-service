package com.kt.damim.notification.controller;

import com.kt.damim.notification.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket-test")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WebSocketTestController {
    
    private final UserSessionService userSessionService;
    
    @PostMapping("/send-test-message/{userId}")
    public Map<String, String> sendTestMessage(@PathVariable String userId, @RequestBody Map<String, String> request) {
        Map<String, Object> testMessage = new HashMap<>();
        testMessage.put("id", 999L);
        testMessage.put("senderId", "SYSTEM");
        testMessage.put("receiverId", userId);
        testMessage.put("message", request.get("message"));
        testMessage.put("type", "TEST");
        testMessage.put("isRead", false);
        testMessage.put("createdAt", java.time.LocalDateTime.now().toString());
        
        String destination = "/topic/notifications/" + userId;
        boolean messageSent = userSessionService.sendMessageToUser(userId, destination, testMessage);
        
        Map<String, String> response = new HashMap<>();
        if (messageSent) {
            response.put("status", "success");
            response.put("message", "테스트 메시지가 전송되었습니다.");
        } else {
            response.put("status", "error");
            response.put("message", "사용자가 연결되어 있지 않습니다: " + userId);
        }
        
        return response;
    }
    

    

}
