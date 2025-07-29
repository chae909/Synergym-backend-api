# 🏃‍♂️ Synergym Backend API

> Enterprise-Grade Spring Boot REST API for AI-Powered Fitness Platform

Synergym의 핵심 백엔드 API 서버입니다. Spring Boot 3.5를 기반으로 구축된 RESTful API로, 사용자 관리, 운동 데이터, 커뮤니티, AI 서비스 연동 등 피트니스 플랫폼의 모든 비즈니스 로직을 담당합니다.

## 🚀 빠른 시작

### 필수 요구사항
- Java 17+
- PostgreSQL 13+
- Redis 6+
- Maven 또는 Gradle

### 설치 및 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd backend-api

# 의존성 설치 및 빌드
./gradlew build

# 개발 서버 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
java -jar build/libs/backend-api-0.0.1-SNAPSHOT.jar
```

## 🛠️ 기술 스택

### 핵심 프레임워크
- **Spring Boot 3.5** - 메인 애플리케이션 프레임워크
- **Spring Security** - 인증 및 보안
- **Spring Data JPA** - ORM 및 데이터 액세스
- **Spring Web** - RESTful 웹 서비스
- **Spring WebFlux** - 비동기 웹 클라이언트
- **Spring Mail** - 이메일 서비스

### 데이터베이스 & 캐싱
- **PostgreSQL** - 메인 관계형 데이터베이스
- **Redis** - 세션 관리 및 캐싱
- **Spring Data Redis** - Redis 연동

### 인증 & 보안
- **JWT (JSON Web Token)** - 토큰 기반 인증
- **OAuth2 Client** - 소셜 로그인 (구글, 카카오 등)
- **Spring Security OAuth2** - OAuth2 통합 보안

### 개발 도구
- **Lombok** - 보일러플레이트 코드 제거
- **Spring Boot DevTools** - 개발 시 자동 리로드
- **Gradle** - 빌드 도구

### 외부 서비스 연동
- **Cloudinary** - 이미지 업로드 및 관리
- **Thymeleaf** - 이메일 템플릿 엔진

### 테스트
- **JUnit 5** - 단위 테스트
- **Spring Boot Test** - 통합 테스트

## 📁 프로젝트 구조

```
src/main/java/org/synergym/backendapi/
├── BackendApiApplication.java    # 메인 애플리케이션 클래스
├── config/                       # 설정 클래스들
├── controller/                   # REST 컨트롤러
│   ├── AuthController.java      # 인증 및 회원가입
│   ├── UserController.java      # 사용자 관리
│   ├── ExerciseController.java  # 운동 관련 API
│   ├── RoutineController.java   # 운동 루틴 관리
│   ├── PostController.java      # 커뮤니티 게시글
│   ├── CommentController.java   # 댓글 관리
│   ├── AiController.java        # AI 서비스 연동
│   ├── ChatbotController.java   # 챗봇 API
│   ├── CloudinaryController.java # 이미지 업로드
│   ├── StatsController.java     # 통계 및 분석
│   ├── AdminController.java     # 관리자 기능
│   └── NotificationController.java # 알림 관리
├── dto/                         # 데이터 전송 객체
├── entity/                      # JPA 엔티티
│   ├── User.java               # 사용자 엔티티
│   ├── Exercise.java           # 운동 엔티티
│   ├── Routine.java            # 루틴 엔티티
│   ├── Post.java               # 게시글 엔티티
│   ├── Comment.java            # 댓글 엔티티
│   ├── AnalysisHistory.java    # 분석 이력
│   ├── ExerciseLog.java        # 운동 기록
│   ├── EmotionLog.java         # 감정 로그
│   └── Notification.java       # 알림 엔티티
├── repository/                  # 데이터 액세스 레이어
├── service/                     # 비즈니스 로직 서비스
├── exception/                   # 커스텀 예외 처리
├── filter/                      # 서블릿 필터
├── handler/                     # 예외 핸들러
├── provider/                    # JWT 프로바이더
└── util/                        # 유틸리티 클래스
```

## 🎯 주요 API 엔드포인트

### 🔐 인증 & 사용자 관리
```http
POST /api/auth/signup          # 회원가입
POST /api/auth/login           # 로그인
POST /api/auth/refresh         # 토큰 갱신
POST /api/auth/logout          # 로그아웃
GET  /api/auth/oauth2/{provider} # 소셜 로그인

GET    /api/users/profile      # 사용자 프로필 조회
PUT    /api/users/profile      # 프로필 수정
DELETE /api/users/account      # 계정 삭제
```

### 🏋️ 운동 관리
```http
GET    /api/exercises          # 운동 목록 조회
GET    /api/exercises/{id}     # 운동 상세 조회
POST   /api/exercises          # 새 운동 등록
PUT    /api/exercises/{id}     # 운동 정보 수정
DELETE /api/exercises/{id}     # 운동 삭제

POST   /api/exercises/{id}/like   # 운동 좋아요
DELETE /api/exercises/{id}/like   # 좋아요 취소
```

### 📅 운동 루틴
```http
GET    /api/routines           # 루틴 목록 조회
GET    /api/routines/{id}      # 루틴 상세 조회
POST   /api/routines           # 새 루틴 생성
PUT    /api/routines/{id}      # 루틴 수정
DELETE /api/routines/{id}      # 루틴 삭제
```

### 📊 운동 기록 & 분석
```http
GET    /api/exercise-logs      # 운동 기록 조회
POST   /api/exercise-logs      # 운동 기록 등록
GET    /api/analysis-history   # 분석 이력 조회
POST   /api/analysis-history   # 새 분석 결과 저장

GET    /api/stats/user         # 사용자 통계
GET    /api/stats/exercise     # 운동 통계
```

### 👥 커뮤니티
```http
GET    /api/posts              # 게시글 목록
GET    /api/posts/{id}         # 게시글 상세
POST   /api/posts              # 게시글 작성
PUT    /api/posts/{id}         # 게시글 수정
DELETE /api/posts/{id}         # 게시글 삭제

GET    /api/posts/{id}/comments # 댓글 목록
POST   /api/posts/{id}/comments # 댓글 작성
PUT    /api/comments/{id}      # 댓글 수정
DELETE /api/comments/{id}      # 댓글 삭제

POST   /api/posts/{id}/like    # 게시글 좋아요
```

### 🤖 AI 서비스 연동
```http
POST   /api/ai/analyze         # AI 운동 분석 요청
GET    /api/ai/recommendations # AI 추천 받기
POST   /api/chatbot/message    # 챗봇 대화
```

### 📷 이미지 관리
```http
POST   /api/cloudinary/upload  # 이미지 업로드
DELETE /api/cloudinary/{publicId} # 이미지 삭제
```

### 🔔 알림
```http
GET    /api/notifications      # 알림 목록
PUT    /api/notifications/{id}/read # 알림 읽음 처리
DELETE /api/notifications/{id} # 알림 삭제
```

### 🛠️ 관리자
```http
GET    /api/admin/users        # 사용자 관리
GET    /api/admin/posts        # 게시글 관리
GET    /api/admin/stats        # 관리자 통계
```

## 🗄️ 데이터베이스 설계

### 핵심 엔티티

#### User (사용자)
```java
@Entity
public class User extends BaseEntity {
    private String email;
    private String username;
    private String password;
    private Role role;
    private String profileImageUrl;
    // OAuth2 필드들
}
```

#### Exercise (운동)
```java
@Entity
public class Exercise extends BaseEntity {
    private String name;
    private String description;
    private Category category;
    private String imageUrl;
    private String videoUrl;
    private Integer caloryPerMinute;
}
```

#### Routine (루틴)
```java
@Entity
public class Routine extends BaseEntity {
    private String name;
    private String description;
    private User user;
    private List<RoutineExercise> exercises;
}
```

#### Post (게시글)
```java
@Entity
public class Post extends BaseEntity {
    private String title;
    private String content;
    private User author;
    private Category category;
    private PostCounter counter;
    private List<Comment> comments;
}
```

## ⚙️ 설정 및 환경변수

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/synergym
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  api-key: ${CLOUDINARY_API_KEY}
  api-secret: ${CLOUDINARY_API_SECRET}

oauth2:
  google:
    client-id: ${GOOGLE_CLIENT_ID}
    client-secret: ${GOOGLE_CLIENT_SECRET}
```

### 환경 변수
```bash
# 데이터베이스
DB_USERNAME=synergym_user
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_jwt_secret_key

# 이메일
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

## 🔐 보안 설정

### JWT 인증
- Bearer 토큰 기반 인증
- 액세스 토큰 + 리프레시 토큰
- 토큰 만료 시간: 24시간

### OAuth2 소셜 로그인
- Google, 카카오, 네이버 지원
- 자동 회원가입 연동

### CORS 설정
```java
@CrossOrigin(origins = {
    "http://localhost:3000",    // React 개발 서버
    "https://synergym.app"      // 프로덕션 도메인
})
```

## 🧪 테스트

### 단위 테스트 실행
```bash
./gradlew test
```

### 특정 테스트 클래스 실행
```bash
./gradlew test --tests UserServiceTest
```

### 테스트 커버리지
```bash
./gradlew jacocoTestReport
```

## 📖 API 문서

### Swagger/OpenAPI
개발 서버 실행 후 다음 URL에서 API 문서 확인:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

## 🚀 배포

### 프로덕션 빌드
```bash
./gradlew build -Pprofile=prod
```

### Docker 배포
```bash
# Dockerfile 생성 후
docker build -t synergym-backend .
docker run -p 8080:8080 synergym-backend
```

### 프로필별 설정
- `application-dev.yml` - 개발 환경
- `application-prod.yml` - 프로덕션 환경
- `application-test.yml` - 테스트 환경

## 📊 모니터링 & 로깅

### 애플리케이션 로그
```bash
# 로그 파일 위치
logs/application.log
```

### 액츄에이터 엔드포인트
```http
GET /actuator/health     # 헬스 체크
GET /actuator/metrics    # 메트릭 정보
GET /actuator/info       # 애플리케이션 정보
```

## 🤝 기여하기

1. 브랜치 생성: `git checkout -b feature/새기능`
2. 코드 작성 및 테스트
3. 코드 스타일 확인: `./gradlew checkstyleMain`
4. 테스트 실행: `./gradlew test`
5. 커밋: `git commit -m "feat: 새로운 API 추가"`
6. Pull Request 생성

## 📝 라이선스

MIT License

---

🏃‍♂️ **Powered by Spring Boot 3.5 + PostgreSQL + Redis**
