# 🏋️ Synergym

> AI-Powered Fitness Platform for Personalized Workout Experiences

## 📋 개요

Synergym은 AI 기술을 활용한 개인화된 피트니스 플랫폼입니다. 사용자의 운동 자세를 실시간으로 분석하고, 개인 맞춤형 운동 프로그램을 제공하며, AI 코치가 운동 목표 달성을 지원합니다.

## 🏗️ 프로젝트 구조

```
Synergym/
├── backend-api/        # Spring Boot 백엔드 API 서버
├── frontend-web/       # React + TypeScript 웹 프론트엔드
├── fast-mcp/          # FastAPI AI 서비스 (모델 훈련 및 예측)
└── webview/           # Android 웹뷰 애플리케이션
```

## 🛠️ 기술 스택

### Backend API (Spring Boot)
- **Framework**: Spring Boot
- **Build Tool**: Gradle
- **Language**: Java

### Frontend Web (React + Vite)
- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **UI Library**: Radix UI + Tailwind CSS
- **State Management**: TanStack Query
- **HTTP Client**: Axios
- **Internationalization**: i18next

### AI Service (FastAPI)
- **Framework**: FastAPI
- **Language**: Python 3.13+
- **AI/ML**: 
  - LangChain & LangGraph
  - OpenAI API
  - Sentence Transformers
  - FAISS (벡터 검색)
  - OpenCV (컴퓨터 비전)
  - Scikit-learn
- **Database**: PostgreSQL, Redis
- **Model**: YOLO Pose Detection

### Mobile (Android WebView)
- **Framework**: Android (Kotlin/Java)
- **Build Tool**: Gradle

## 🚀 시작하기

### 1. 전체 프로젝트 클론

```bash
git clone <repository-url>
cd Synergym
```

### 2. Backend API 서버 실행

```bash
cd backend-api
./gradlew bootRun
```

### 3. AI 서비스 (FastAPI) 실행

```bash
cd fast-mcp
# Python 환경 설정
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install -e .

# 서버 실행
python app/main.py
```

### 4. Frontend 웹 애플리케이션 실행

```bash
cd frontend-web
pnpm install
pnpm dev
```

### 5. Android 애플리케이션 빌드

```bash
cd webview
./gradlew assembleDebug
```

## 🌟 주요 기능

### 🎯 AI 피트니스 코치
- 개인화된 운동 프로그램 생성
- 실시간 운동 자세 분석 및 피드백
- 운동 목표 설정 및 진행도 추적

### 📊 운동 분석
- YOLO 포즈 검출을 통한 실시간 자세 분석
- 운동 데이터 시각화 및 분석
- 개인 운동 기록 관리

### 🎥 유튜브 연동
- AI 기반 운동 영상 추천
- 운동 루틴별 영상 큐레이션

### 📱 멀티 플랫폼 지원
- 웹 브라우저 (React)
- 모바일 앱 (Android WebView)

## 📦 환경 설정

### 환경 변수 설정

각 서비스별로 필요한 환경 변수를 설정해주세요:

#### FastAPI (.env)
```bash
OPENAI_API_KEY=your_openai_api_key
DATABASE_URL=postgresql://user:password@localhost:5432/synergym
REDIS_URL=redis://localhost:6379
CLOUDINARY_URL=cloudinary://...
```

### 데이터베이스 설정

```bash
# PostgreSQL 설치 및 실행
# Redis 설치 및 실행
```

## 🧪 테스트

### Backend API 테스트
```bash
cd backend-api
./gradlew test
```

### AI 서비스 테스트
```bash
cd fast-mcp
python -m pytest tests/
```

### Frontend 테스트
```bash
cd frontend-web
pnpm test
```

## 📱 API 문서

- **Backend API**: `http://localhost:8080/swagger-ui.html`
- **AI Service API**: `http://localhost:8000/docs`

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 📞 연락처

- 프로젝트 링크: [https://github.com/TeamSynergym/Synergym](https://github.com/TeamSynergym/Synergym)

---

⭐ 이 프로젝트가 도움이 되었다면 Star를 눌러주세요!
