# 소설처럼 읽는 스프링부트 3

> 위키독스 교재: https://wikidocs.net/book/18840

Spring Boot 3로 게시판을 만들어가는 실습 중심 교재입니다. 각 챕터별 브랜치를 제공하므로, 원하는 시점의 완성된 코드를 바로 받아 실행할 수 있습니다.

## 기술 스택

| 항목 | 버전 |
|------|------|
| Spring Boot | 3.5.10 |
| Java | 17 |
| Gradle | Groovy DSL |
| Spring Data JPA | (Spring Boot 관리) |
| Spring Security | (Spring Boot 관리) |
| Thymeleaf | (Spring Boot 관리) |
| H2 Database | 개발용 내장 DB |
| MariaDB | 운영용 DB |
| Lombok | (Spring Boot 관리) |
| Validation | (Spring Boot 관리) |

## 저장소 사용법

### 저장소 클론

```bash
git clone https://github.com/sung2ne/textbook-springboot3.git
cd textbook-springboot3
```

### 원하는 챕터로 이동

각 브랜치에는 해당 챕터까지의 코드가 누적 적용되어 있습니다.

```bash
# PART 03의 05장까지 완성된 코드 (게시글 등록)
git checkout part03/chapter-05

# PART 04의 07장까지 완성된 코드 (로그인)
git checkout part04/chapter-07
```

### 프로젝트 실행

```bash
# 빌드 (Java 17 필요)
./gradlew build

# 개발 모드로 실행 (H2 메모리 DB)
./gradlew bootRun

# 브라우저에서 확인
# http://localhost:8080
```

## 브랜치 목록

### PART 01. Spring Boot 기초

| 브랜치 | 내용 |
|--------|------|
| `part01/chapter-00` | 시작하기 |
| `part01/chapter-01` | 개발환경 & 프로젝트 구조 |
| `part01/chapter-02` | Thymeleaf 기본 문법 |
| `part01/chapter-03` | Thymeleaf 레이아웃 |
| `part01/chapter-04` | 게시판 UI 구현 |

### PART 02. Spring Data JPA

| 브랜치 | 내용 |
|--------|------|
| `part02/chapter-01` | JPA 기초 |
| `part02/chapter-02` | 연관관계 매핑 |
| `part02/chapter-03` | 쿼리 메서드 & JPQL |
| `part02/chapter-04` | JPA 실습 |

### PART 03. 비인증 게시판

| 브랜치 | 내용 |
|--------|------|
| `part03/chapter-01` | 게시판 설계하기 |
| `part03/chapter-02` | Entity와 Repository |
| `part03/chapter-03` | 게시글 목록 만들기 |
| `part03/chapter-04` | Bean Validation 이해하기 |
| `part03/chapter-05` | 게시글 등록 만들기 |
| `part03/chapter-06` | 게시글 조회 만들기 |
| `part03/chapter-07` | 게시글 수정 만들기 |
| `part03/chapter-08` | 게시글 삭제 만들기 |
| `part03/chapter-09` | 댓글 기능 |
| `part03/chapter-10` | 파일 업로드 |

### PART 04. Spring Security 인증

| 브랜치 | 내용 |
|--------|------|
| `part04/chapter-01` | Spring Security 소개 |
| `part04/chapter-02` | SecurityConfig 설정하기 |
| `part04/chapter-03` | 비밀번호 암호화 |
| `part04/chapter-04` | 회원 도메인 설계하기 |
| `part04/chapter-05` | UserDetailsService 구현하기 |
| `part04/chapter-06` | 회원가입 기능 만들기 |
| `part04/chapter-07` | 로그인 기능 만들기 |
| `part04/chapter-08` | 인증 정보 활용하기 |
| `part04/chapter-09` | Remember Me 기능 |
| `part04/chapter-10` | 보안 예외 처리 |
| `part04/chapter-11` | 메서드 보안 |
| `part04/chapter-12` | 관리자 기능 |
| `part04/chapter-13` | 프로필 관리 |

### PART 05. 인증된 게시판 완성

| 브랜치 | 내용 |
|--------|------|
| `part05/chapter-01` | 게시판 인증 연동 |
| `part05/chapter-02` | 댓글 인증 연동 |
| `part05/chapter-03` | 마이페이지 |
| `part05/chapter-04` | 프로젝트 마무리 |

## 활용 팁

**교재를 따라가며 직접 코딩하는 것을 추천합니다.** 저장소의 코드는 다음 상황에서 활용하세요.

- 코드가 정상 동작하지 않을 때 비교 대상으로 활용
- 특정 챕터부터 학습을 시작하고 싶을 때 해당 브랜치에서 출발
- 전체 프로젝트 구조를 한눈에 파악하고 싶을 때 참고

> PART 03부터 실질적인 게시판 기능이 구현됩니다. PART 04부터 Spring Security가 적용되며, PART 05에서 완성된 인증 게시판이 완성됩니다.
