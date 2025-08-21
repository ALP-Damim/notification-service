package com.kt.damim.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "socket_user_id", nullable = false)
    private String socketUserId;
    
    @Column(name = "socket_session_id", nullable = false, unique = true)
    private String socketSessionId;
    
    @Column(name = "connected_at", nullable = false)
    private LocalDateTime connectedAt;
    
    @Column(name = "disconnected_at")
    private LocalDateTime disconnectedAt;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
