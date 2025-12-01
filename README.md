# 🍀 Lucky Log 🍀

> AI 기반 운세 예측을 기록하고 실제 결과와 비교할 수 있는 웹 플랫폼

## 📖 프로젝트 소개

Lucky Log는 AI를 활용하여 운세를 예측하고 저장하는 서비스입니다.  
실제 일어난 일과 비교하며 운세의 정확도를 확인할 수 있습니다.

**🔗 서비스 주소 [🔮LUCKY LOG](https://lucky-log.duckdns.org/)**

## 🛠️ 기술 스택

### Backend

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.3
- **ORM**: Spring Data JPA
- **Security**: Spring Security
- **Database**: H2 (개발) / MySQL (운영)

### Frontend

- **Template Engine**: Thymeleaf
- **JavaScript**: Vanilla JS (ES6+)
- **Styling**: CSS3

### Infra

- **Cloud**: AWS EC2, AWS RDS
- **DevOps**: GitHub Actions
- **Monitoring**: Spring Boot Actuator, Grafana Loki

### External Services

- **AI API**: Google Gemini API 1.12.0

### DevOps

- **Build Tool**: Gradle
- **Testing**: JUnit 5
- **Version Control**: Git

## 🏗️배포 아키텍쳐

```txt
GitHub Repository
       ↓ (Push)
GitHub Actions (CI/CD)
       ↓ (Build & Test)
AWS EC2 (Application Server)
       ↓ (Data)
AWS RDS (MySQL Database)
```

## 📂 디렉터리 구조

```txt
src
├─java
│  └─com
│      └─fortunehub
│          └─luckylog
│              ├─client # 외부 API
│              │  └─gemini
│              ├─common
│              ├─config # 설정 파일
│              ├─controller # 웹 컨트롤러
│              │  ├─api
│              │  │   └─fortune
│              │  └─web
│              │      ├─auth
│              │      │  └─form
│              │      └─fortune
│              │          └─form
│              ├─domain # 도메인 엔티티
│              │  ├─common
│              │  ├─fortune
│              │  └─member
│              ├─dto # 데이터 전송 객체
│              │  ├─request
│              │  │  ├─auth
│              │  │  └─fortune
│              │  └─response
│              │      ├─common
│              │      └─fortune
│              ├─exception # 예외 처리
│              ├─init # local용 초기 데이터
│              ├─repository # 데이터 처리
│              │  ├─fortune
│              │  └─member
│              ├─security # 인증, 인가
│              └─service # 비즈니스 로직 처리
│                  ├─auth
│                  └─fortune
└─resources
    ├─static
    │  ├─css
    │  │  ├─auth
    │  │  ├─common
    │  │  └─fortune
    │  ├─images
    │  └─js
    │      ├─auth
    │      ├─common
    │      └─fortune
    └─templates # thymeleaf 템플릿
        ├─auth
        ├─error
        ├─fortune
        └─fragments
```

## 🗄️데이터베이스 설계

![ERD_v1](./uploads/erd_v1_3.png)

## 🎫 Local 환경에서의 실행

1. 프로젝트 클론

```bash
https://github.com/HJ0216/lucky-log.git
```

2. 환경변수 설정

- `SPRING_PROFILES_ACTIVE=local` profile 설정
- `application.yaml` 관련 환경 변수 설정
- `application-local.yaml` 관련 환경 변수 설정
- `application-prompt.yaml` 파일 추가

3. 프로젝트 실행

## 📜 Git 전략

- `main`: 프로덕션 배포
- `develop`: 개발 통합
- `feature`: 신규 기능 개발
  - `feature/signup`, `feature/login`
- `refactor`: 코드 리팩토링
  - `refactor/ui`
- `fix`: 버그 수정
  - `fix/signup-error`

## 🗒️ 커밋 컨벤션

| Emoji | Type     | Description      |
| ----- | -------- | ---------------- |
| 🎉    | Init     | 프로젝트 시작    |
| ✨    | Feat     | 새로운 기능 추가 |
| 🐛    | Fix      | 버그 수정        |
| 📝    | Docs     | 문서 수정        |
| 🎨    | Style    | 코드 포매팅      |
| 💄    | Design   | UI 디자인 변경   |
| ✅    | Test     | 테스트 코드      |
| ♻️    | Refactor | 코드 리팩토링    |
| 🧹    | Chore    | 기타 수정        |
| 🚚    | Rename   | 파일/폴더명 수정 |
| 🔥    | Remove   | 파일 삭제        |

**커밋 메시지 형식**

- `<emoji><Type>: #<이슈번호> <설명>`
- ✨Feat: #12 회원가입 기능 추가
