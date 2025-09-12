# LinkPulse Day1 Setup

## 1. 프로젝트 구조

MSA 멀티 모듈 형태로 뼈대를 구성했음.

```
linkpulse/
├─ build.gradle # 루트 공통 Gradle 설정
├─ settings.gradle # 서브모듈 등록
├─ gradle/ # Gradle wrapper
├─ gradlew* # Wrapper 실행 스크립트
├─ common-lib/ # 공통 모듈 (DTO, 예외, 응답객체)
├─ auth-service/ # 인증/인가 서비스 (JWT, Redis)
├─ link-service/ # 단축 URL 서비스 (캐시, 리다이렉트, Kafka 발행)
├─ analytics-service/ # 분석 서비스 (Kafka Streams, ES)
└─ infra/ # docker-compose.yml, prometheus 설정 등
```
<br/>

## 2. Gradle 설정

### 루트 build.gradle
- 공통 플러그인(Spring Boot, dependency-management) 정의
- Java 17 toolchain, JUnit5 플랫폼 적용


### 모듈별 build.gradle
- **common-lib**: validation, lombok
- **auth-service**: web, security, jpa, redis, jjwt, postgresql
- **link-service**: web, jpa, redis, kafka
- **analytics-service**: web, kafka, kafka-streams, elasticsearch, mongodb

<br/>

## 3. 애플리케이션 클래스

각 모듈별로 `@SpringBootApplication` 붙은 메인 클래스 생성:
- `AuthServiceApplication`
- `LinkServiceApplication`
- `AnalyticsServiceApplication`

<br/>

## 4. Docker 인프라 세팅

`infra/docker-compose.yml` 에 정의된 서비스:

- PostgreSQL 16.4 (5432)
- Redis 7.2 (6379)
- ZooKeeper 3.9 (2181)
- Kafka 3.9 (9092)
- MongoDB 7.0 (27017)
- Elasticsearch 8.18.6 (9200/9300)
- Kibana 8.18.6 (5601)
- Prometheus 2.53 (9090)
- Grafana 10.4 (3000)

실행 명령:
```bash
cd infra
docker compose up -d
docker compose ps
```

<br/>

## 5. 서비스별 기본 세팅

포트 충돌을 피하기 위해 지정:

- auth-service → 8081
- link-service → 8082
- analytics-service → 8083

```yml
spring:
  profiles:
    active: dev
```
- application-dev.yml 은 서비스별로 분리 (DB/Redis/Kafka/Mongo/ES 연결 정보 포함).

<br/>

## 6. 헬스 체크 컨트롤러

- 빠른 확인을 위해 각 서비스에 간단한 ping 컨트롤러 추가.

```java
@RestController
class HealthController {
    @GetMapping("/ping")
    public String ping() { return "link-ok"; }
}
```


### 브라우저 확인:

- http://localhost:8081/_ping
→ auth-ok

- http://localhost:8082/_ping
→ link-ok

- http://localhost:8083/_ping
→ analytics-ok

