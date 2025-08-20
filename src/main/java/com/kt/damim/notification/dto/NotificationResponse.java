package com.kt.damim.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String senderId;
    private String receiverId;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
