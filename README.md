# 알림 서비스 (Notification Service)

Spring Boot와 WebSocket(STOMP)을 사용하는 실시간 알림 서비스입니다. REST API로 알림을 저장하고, WebSocket 구독자에게 실시간으로 전송합니다.

## 주요 기능

- 실시간 WebSocket 알림 전송 (/topic/notifications/{socketUserId})
- 알림 저장/조회, 읽음 처리, 읽지 않은 개수 조회
- 테스트용 WebSocket 메시지 전송 API 제공

## 기술 스택

- Backend: Spring Boot 3.5.4, Java 17
- DB: H2(개발), JPA/Hibernate
- WebSocket: Spring WebSocket, STOMP

## 프로젝트 구조

```
notification-service/
├── src/main/java/com/kt/damim/notification/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   ├── config/
│   └── NotificationApplication.java
├── src/main/resources/
│   └── application.properties
└── build.gradle
```

## REST API

### 1) 알림 전송
POST /api/notifications/send

Request
```json
{
  "senderId": "userA",
  "receiverId": "userB",
  "message": "안녕하세요!",
  "type": "MESSAGE"
}
```

Response 200
```json
{
  "id": 1,
  "senderId": "userA",
  "receiverId": "userB",
  "message": "안녕하세요!",
  "type": "MESSAGE",
  "isRead": false,
  "createdAt": "2024-01-01T12:00:00"
}
```

### 2) 사용자의 모든 알림 조회
GET /api/notifications/user/{receiverId}

### 3) 사용자의 읽지 않은 알림 조회
GET /api/notifications/user/{receiverId}/unread

### 4) 사용자의 읽지 않은 알림 개수
GET /api/notifications/user/{receiverId}/unread-count

### 5) 특정 알림 읽음 처리
PUT /api/notifications/{notificationId}/read

### 6) 특정 사용자의 모든 알림 읽음 처리
PUT /api/notifications/user/{receiverId}/read-all

### 7) WebSocket 테스트 메시지 전송
POST /api/websocket-test/send-test-message/{socketUserId}

Request
```json
{ "message": "테스트 메시지입니다!" }
```

## WebSocket(STOMP)

- WS 엔드포인트: ws://<host>:8080/ws
- Application Prefix: /app
- Broker Prefix: /topic

### 구독
- 사용자별 알림 토픽: /topic/notifications/{socketUserId}

### 서버 제공 STOMP 엔드포인트
- 클라이언트 → 서버: /app/hello → 브로커: /topic/greetings
- 클라이언트 → 서버: /app/register → 브로커: /topic/registration
- 클라이언트 → 서버: /app/private-message (브로커로 송신하지 않고 서버 내 처리)

### STOMP 예제 (stompjs)
```javascript
import { Client } from '@stomp/stompjs'

const socketUserId = 'userB'
const client = new Client({ brokerURL: 'ws://localhost:8080/ws' })

client.onConnect = () => {
  client.subscribe(`/topic/notifications/${socketUserId}`, (msg) => {
    const payload = JSON.parse(msg.body)
    console.log('알림 수신:', payload)
  })

  // 예시 메시지 전송
  client.publish({ destination: '/app/hello', body: JSON.stringify({ name: 'tester' }) })
}

client.activate()
```

## 실행

```bash
gradlew.bat build
gradlew.bat bootRun
```

개발 기본값(H2, 포트 8080)으로 실행됩니다.

## 문제 해결

- WebSocket 연결 실패: 브라우저 콘솔/네트워크 탭 확인, CORS/프록시 설정 확인
- 알림 미수신: 클라이언트가 /topic/notifications/{socketUserId}를 구독했는지 확인
- DB 오류: 애플리케이션 프로퍼티의 데이터베이스 설정 및 드라이버 의존성 확인

