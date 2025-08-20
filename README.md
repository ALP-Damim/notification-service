# 알림 서비스 (Notification Service)

Spring Boot와 WebSocket을 사용한 실시간 알림 서비스입니다. 사용자가 버튼을 누르면 다른 사용자에게 실시간으로 알림이 전송됩니다.

## 주요 기능

- 실시간 WebSocket 알림 전송
- 알림 저장 및 조회
- 읽음/읽지 않음 상태 관리
- 읽지 않은 알림 개수 조회
- REST API 제공

## 기술 스택

- **Backend**: Spring Boot 3.5.4, Java 17
- **Database**: H2 (개발용), JPA/Hibernate
- **WebSocket**: Spring WebSocket, STOMP
- **Frontend**: React (예제 제공)

## 프로젝트 구조

```
notification-service/
├── src/main/java/com/kt/damim/notification/
│   ├── entity/              # 엔티티 클래스
│   ├── dto/                 # 데이터 전송 객체
│   ├── repository/          # 데이터 접근 계층
│   ├── service/             # 비즈니스 로직
│   ├── controller/          # REST API 컨트롤러
│   ├── config/              # 설정 클래스
│   └── NotificationApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── static/
│       └── index.html       # 테스트용 HTML 페이지
├── react-example/           # React 예제 코드
└── build.gradle
```

## API 엔드포인트

### 알림 관련 API

### 1. 알림 전송
**POST** `/api/notifications/send`

사용자 간 알림을 전송합니다.

**Request Body:**
```json
{
    "senderId": "userA",
    "receiverId": "userB",
    "message": "안녕하세요!",
    "type": "MESSAGE"
}
```

**Response (200 OK):**
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

**cURL 예제:**
```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "senderId": "userA",
    "receiverId": "userB",
    "message": "안녕하세요!",
    "type": "MESSAGE"
  }'
```

### 2. 사용자의 모든 알림 조회
**GET** `/api/notifications/user/{userId}`

특정 사용자가 받은 모든 알림을 조회합니다.

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "senderId": "userA",
        "receiverId": "userB",
        "message": "안녕하세요!",
        "type": "MESSAGE",
        "isRead": false,
        "createdAt": "2024-01-01T12:00:00"
    },
    {
        "id": 2,
        "senderId": "userC",
        "receiverId": "userB",
        "message": "회의 시간 변경되었습니다.",
        "type": "MEETING",
        "isRead": true,
        "createdAt": "2024-01-01T11:30:00"
    }
]
```

**cURL 예제:**
```bash
curl -X GET http://localhost:8080/api/notifications/user/userB
```

### 3. 읽지 않은 알림 조회
**GET** `/api/notifications/user/{userId}/unread`

특정 사용자의 읽지 않은 알림만 조회합니다.

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "senderId": "userA",
        "receiverId": "userB",
        "message": "안녕하세요!",
        "type": "MESSAGE",
        "isRead": false,
        "createdAt": "2024-01-01T12:00:00"
    }
]
```

**cURL 예제:**
```bash
curl -X GET http://localhost:8080/api/notifications/user/userB/unread
```

### 4. 읽지 않은 알림 개수 조회
**GET** `/api/notifications/user/{userId}/unread-count`

특정 사용자의 읽지 않은 알림 개수를 조회합니다.

**Response (200 OK):**
```json
3
```

**cURL 예제:**
```bash
curl -X GET http://localhost:8080/api/notifications/user/userB/unread-count
```

### 5. 개별 알림 읽음 처리
**PUT** `/api/notifications/{notificationId}/read`

특정 알림을 읽음 처리합니다.

**Response (200 OK):**
```
(빈 응답)
```

**cURL 예제:**
```bash
curl -X PUT http://localhost:8080/api/notifications/1/read
```

### 6. 모든 알림 읽음 처리
**PUT** `/api/notifications/user/{userId}/read-all`

특정 사용자의 모든 알림을 읽음 처리합니다.

**Response (200 OK):**
```
(빈 응답)
```

**cURL 예제:**
```bash
curl -X PUT http://localhost:8080/api/notifications/user/userB/read-all
```

### WebSocket 테스트 API

### 7. 테스트 메시지 전송
**POST** `/api/websocket-test/send-test-message/{userId}`

특정 사용자에게 테스트 WebSocket 메시지를 전송합니다.

**Request Body:**
```json
{
    "message": "테스트 메시지입니다!"
}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "message": "테스트 메시지가 전송되었습니다."
}
```

## JavaScript/React API 사용 예제

### fetch를 사용한 API 호출
```javascript
// 알림 전송
const sendNotification = async (senderId, receiverId, message) => {
    try {
        const response = await fetch('/api/notifications/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                senderId,
                receiverId,
                message,
                type: 'MESSAGE'
            })
        });
        
        if (!response.ok) {
            throw new Error('알림 전송 실패');
        }
        
        const notification = await response.json();
        console.log('전송된 알림:', notification);
        return notification;
    } catch (error) {
        console.error('오류:', error);
        throw error;
    }
};

// 알림 목록 조회
const getNotifications = async (userId) => {
    try {
        const response = await fetch(`/api/notifications/user/${userId}`);
        
        if (!response.ok) {
            throw new Error('알림 조회 실패');
        }
        
        const notifications = await response.json();
        console.log('알림 목록:', notifications);
        return notifications;
    } catch (error) {
        console.error('오류:', error);
        throw error;
    }
};

// 읽음 처리
const markAsRead = async (notificationId) => {
    try {
        const response = await fetch(`/api/notifications/${notificationId}/read`, {
            method: 'PUT'
        });
        
        if (!response.ok) {
            throw new Error('읽음 처리 실패');
        }
        
        console.log('읽음 처리 완료');
    } catch (error) {
        console.error('오류:', error);
        throw error;
    }
};
```

### axios를 사용한 API 호출
```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/notifications';

// 알림 전송
const sendNotification = async (senderId, receiverId, message) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/send`, {
            senderId,
            receiverId,
            message,
            type: 'MESSAGE'
        });
        
        console.log('전송된 알림:', response.data);
        return response.data;
    } catch (error) {
        console.error('오류:', error.response?.data || error.message);
        throw error;
    }
};

// 알림 목록 조회
const getNotifications = async (userId) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/user/${userId}`);
        
        console.log('알림 목록:', response.data);
        return response.data;
    } catch (error) {
        console.error('오류:', error.response?.data || error.message);
        throw error;
    }
};

// 읽지 않은 알림 개수 조회
const getUnreadCount = async (userId) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/user/${userId}/unread-count`);
        
        console.log('읽지 않은 알림 개수:', response.data);
        return response.data;
    } catch (error) {
        console.error('오류:', error.response?.data || error.message);
        throw error;
    }
};
```

## WebSocket

### 연결
```
WebSocket URL: ws://localhost:8080/ws
```

### 구독
```
/topic/notifications/{userId}
```

### 메시지 형식
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

### 사용자 세션 관리
- 사용자가 WebSocket에 연결하고 특정 토픽에 구독하면 자동으로 세션이 등록됩니다
- 연결이 해제되면 세션이 자동으로 비활성화됩니다
- 알림은 연결된 사용자에게만 전송됩니다
- 연결되지 않은 사용자에게는 알림이 전송되지 않고 로그에 경고가 기록됩니다

## 실행 방법

### 1. 프로젝트 빌드 및 실행
```bash
# Gradle을 사용하여 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 2. 테스트 페이지 접속
브라우저에서 `http://localhost:8080` 접속

### 3. H2 데이터베이스 콘솔
`http://localhost:8080/h2-console` 접속
- JDBC URL: `jdbc:h2:mem:notificationdb`
- Username: `sa`
- Password: (비어있음)

## React 연동 가이드

### 1. 네이티브 WebSocket 사용 (권장)
```javascript
// WebSocket 연결
const ws = new WebSocket('ws://localhost:8080/ws');

ws.onopen = () => {
    console.log('WebSocket 연결 성공');
    
    // STOMP CONNECT 프레임 전송
    ws.send('CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\0');
    
    // 구독 설정
    ws.send('SUBSCRIBE\nid:sub-1\ndestination:/topic/notifications/userId\n\n\0');
};

ws.onmessage = (event) => {
    // STOMP 메시지 파싱 및 처리
    const data = event.data;
    // 메시지 처리 로직
};
```

### 2. API 호출 예제
```javascript
// 알림 전송
const sendNotification = async (senderId, receiverId, message) => {
    const response = await fetch('/api/notifications/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ senderId, receiverId, message, type: 'MESSAGE' })
    });
    return response.json();
};

// 알림 조회
const getNotifications = async (userId) => {
    const response = await fetch(`/api/notifications/user/${userId}`);
    return response.json();
};

// 읽음 처리
const markAsRead = async (notificationId) => {
    await fetch(`/api/notifications/${notificationId}/read`, { method: 'PUT' });
};
```

### 3. 컴포넌트 구조 예시
```javascript
const NotificationComponent = ({ userId }) => {
    const [notifications, setNotifications] = useState([]);
    const [isConnected, setIsConnected] = useState(false);
    
    useEffect(() => {
        // WebSocket 연결 및 구독
        // API 호출로 초기 데이터 로드
    }, [userId]);
    
    const handleNewNotification = (notification) => {
        setNotifications(prev => [notification, ...prev]);
    };
    
    return (
        <div>
            {/* 알림 UI */}
        </div>
    );
};
```

## 테스트 시나리오

1. **기본 알림 전송**
   - 사용자 A가 사용자 B에게 알림 전송
   - 사용자 B가 실시간으로 알림 수신

2. **읽음 처리**
   - 사용자가 알림을 클릭하여 읽음 처리
   - 읽지 않은 알림 개수 업데이트

3. **모든 알림 읽음 처리**
   - 한 번에 모든 알림을 읽음 처리

4. **브라우저 알림**
   - 브라우저 알림 권한 요청
   - 새 알림 수신 시 브라우저 알림 표시

## 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Gradle 7.x 이상

### IDE 설정
- IntelliJ IDEA 또는 Eclipse 사용 권장
- Lombok 플러그인 설치 필요

## 배포

### 프로덕션 환경
1. `application.properties`에서 데이터베이스 설정 변경
2. CORS 설정 조정
3. 로깅 레벨 조정

### Docker 배포
```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/notification-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 문제 해결

### WebSocket 연결 실패
- 브라우저 콘솔에서 연결 상태 확인
- 방화벽 설정 확인
- CORS 설정 확인

### 알림이 전송되지 않음
- 데이터베이스 연결 상태 확인
- 로그에서 오류 메시지 확인
- API 엔드포인트 응답 확인

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
