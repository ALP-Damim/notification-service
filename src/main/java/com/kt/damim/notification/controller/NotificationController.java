package com.kt.damim.notification.controller;

import com.kt.damim.notification.dto.NotificationRequest;
import com.kt.damim.notification.dto.NotificationResponse;
import com.kt.damim.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{receiverId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByReceiverId(@PathVariable String receiverId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByReceiverId(receiverId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{receiverId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable String receiverId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(receiverId);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/user/{receiverId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String receiverId) {
        notificationService.markAllAsRead(receiverId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/{receiverId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String receiverId) {
        long count = notificationService.getUnreadCount(receiverId);
        return ResponseEntity.ok(count);
    }
}
