package com.kt.damim.notification.service;

import com.kt.damim.notification.dto.NotificationRequest;
import com.kt.damim.notification.dto.NotificationResponse;
import com.kt.damim.notification.entity.Notification;
import com.kt.damim.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationSessionService notificationSessionService;
    
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .message(request.getMessage())
                .type(request.getType())
                .isRead(false)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // WebSocket을 통해 실시간 알림 전송
        NotificationResponse response = convertToResponse(savedNotification);
        String destination = "/topic/notifications/" + request.getReceiverId();
        
        boolean messageSent = notificationSessionService.sendMessageToUser(request.getReceiverId(), destination, response);
        
        if (!messageSent) {
            log.warn("사용자가 연결되어 있지 않음: receiverId={}", request.getReceiverId());
        }
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByReceiverId(String receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(String receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(receiverId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }
    
    @Transactional
    public void markAllAsRead(String receiverId) {
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(receiverId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Transactional(readOnly = true)
    public long getUnreadCount(String receiverId) {
        return notificationRepository.countUnreadNotifications(receiverId);
    }
    
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId())
                .receiverId(notification.getReceiverId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
