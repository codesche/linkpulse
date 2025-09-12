# 🚀 LinkPulse 

초고속 URL 단축 & 실시간 클릭 분석 시스템

## 목적
- 초고속 리다이렉트 + 실시간 클릭 분석을 동시에 달성하는 백엔드 시스템을 만든다.
- **사용자 경험**: 단축 URL을 눌렀을 때 체감 지연이 거의 없도록 (캐시 우선, 밀리초 단위) 리다이렉트 처리
- **데이터 가치**: 클릭 이벤트를 `Kafka → Kafka Streams → Elasticsearch` 파이프라인으로 흘려보내 실시간 집계·분석
- **운영 품질**: JWT 인증/인가, Redis RefreshToken, 전역 예외/공통 응답, 배치 보정, 모니터링/부하테스트까지 현업 수준 품질로 완성

---

## 핵심 컴포넌트

### Redirect API (초저지연)
- 단축코드 → 원본 URL 매핑을 **Redis 캐시** 최우선 조회 (미스 시 RDB 조회 후 캐시 적재)
- 클릭 발생 시 **비동기 Kafka 이벤트 발행** (리다이렉트 경로는 절대 블로킹하지 않음)
- 시간 타입은 전부 **Instant** 사용

### 실시간 파이프라인
- **Producer**: API가 Kafka 토픽에 클릭 이벤트 발행
- **Kafka Streams**: 윈도우 기반 집계(분/시/일), 중복 방지 처리 후 Elasticsearch 색인

### Thymeleaf 관리자 대시보드
- 실시간/히스토리 KPI (클릭수, referrer, UA, 지역별 등) 조회
- 백오피스 인증은 **JWT + Spring Security**

### 배치 보정 (Spring Batch)
- 누락/지연 이벤트 재집계
- Elasticsearch 색인 재동기화

---

## 운영 필수 요소
- 전역 예외 처리 (`@RestControllerAdvice`)
- 공통 API 응답 객체 (`ApiResponse<T>`)
- DTO 중심 계층 설계 (Controller ↔ DTO ↔ Service ↔ Repository)
- 캐싱 전략 (Cache-Aside, 단축코드→URL)
- JWT 인증 + RefreshToken Redis 저장
- 모니터링 & 부하테스트 (Actuator + Prometheus + Grafana + k6)
- 단위 테스트 (JUnit5 + Mockito)
- “서비스 계층에만 비즈니스 로직” 원칙 철저 준수
- LocalDateTime 대신 **Instant** 적용

---

## 기술 스택 (주요)
- **Backend**: Java 17, Spring Boot 3.5.x, Spring Security, Spring Data JPA, Spring Data Redis, Spring Batch
- **Infra**: PostgreSQL, Redis, Kafka, MongoDB, Elasticsearch, Kibana
- **Dashboard**: Thymeleaf, Prometheus, Grafana
- **Test**: JUnit5, Mockito, Testcontainers
