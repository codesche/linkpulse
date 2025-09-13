
# Day3 — Auth 흐름 (회원가입/로그인/리프레시/로그아웃)

## 🎯 목표
- 회원가입/로그인/로그아웃/리프레시 토큰 발급 구현
- JWT AccessToken 발급 및 검증
- RefreshToken은 Redis에 저장
- Spring Security로 기본 보안 뼈대 구성
- 공통 응답(`ApiResponse`), 전역 예외 처리, traceId 필터 이미 적용됨

<br/>

## 📦 모듈/패키지 구조

```
auth-service
└─ src/main/java/com/linkpulse/auth
├─ AuthServiceApplication.java
├─ config/
│ ├─ SecurityConfig.java
│ └─ AuthBeansConfig.java
├─ controller/
│ ├─ AuthController.java
│ ├─ ProtectedController.java (선택: 테스트용)
│ └─ HealthController.java
├─ domain/
│ └─ Member.java
├─ repository/
│ └─ MemberRepository.java
├─ service/
│ └─ AuthService.java
├─ token/
│ ├─ JwtProvider.java
│ └─ RedisRefreshToken.java
└─ dto/
├─ request/
│ ├─ SignUpRequest.java
│ └─ LoginRequest.java
└─ response/
├─ SignUpResponse.java
└─ TokenResponse.java
```

<br/>

## 🧪 Postman 테스트 시나리오

- GET /ping → ApiResponse 포맷, traceId 확인
- POST /auth/signup → 회원가입 성공, memberId 반환
- POST /auth/login → accessToken, refreshToken 발급
- POST /auth/refresh (유효 refreshToken) → 새 accessToken
- POST /auth/logout → refreshToken 삭제

<br/>

## 🧪 JUnit5 테스트 아이디어

- JwtProviderTest: 발급/파싱 Claims 확인
- AuthServiceTest:
  - 회원가입 시 중복 예외
  - 로그인 성공/실패
  - refreshToken mismatch 예외
  - GlobalExceptionHandlerTest: 유효성 검증 실패 시 LP-VALIDATION 코드 확인
  - HealthIntegrationTest: ping 호출 시 traceId 헤더 응답 확인

---

# ✅ Day3 테스트 요약 (Service / Controller / Repository)


## 1. Service 테스트

- **AuthServiceTest**
  - 회원가입 중복 예외 검증
  - 로그인 성공/실패 케이스 검증
  - RefreshToken mismatch 예외 처리 확인
  - Redis에 RefreshToken 저장 동작 확인

<br/>

## 2. Controller 테스트

- **AuthControllerTest**
  - `/auth/signup` 성공 → memberId 반환 검증
  - `/auth/login` 성공 → accessToken, refreshToken 발급 검증
  - 요청 유효성 실패 → GlobalExceptionHandler 통해 `LP-VALIDATION` 코드 확인

- **HealthControllerTest**
  - `/ping` 호출 시 ApiResponse 포맷과 traceId 헤더 확인

- **SecurityPermitPathsTest**
  - `/auth/login`, `/auth/signup`, `/ping` → permitAll 확인

<br/>

## 3. Repository 테스트

- **MemberRepositoryTest (@DataJpaTest + Testcontainers(PostgreSQL))**
  - `findByUsername` 정상 조회 검증
  - username 중복 저장 시 유니크 제약 예외 발생 검증

<br/>

## 📌 정리

- Service, Controller, Repository 세 계층에 대한 핵심 단위/슬라이스 테스트 완료
- JWT 토큰 불일치 문제는 매개변수 캡처/접두사 확인으로 보완 가능
- Day3 테스트 목표(회원가입/로그인/리프레시/로그아웃 흐름 검증) 달성
