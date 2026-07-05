<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&height=220&color=0:FFF4E8,45:FFB36B,100:FF7B10&text=HALO&fontAlign=50&fontAlignY=38&fontSize=64&fontColor=ffffff&desc=관계를%20이어가는%20작은%20행동들&descAlign=50&descAlignY=58&descSize=18" />

<br/>

### 익숙해서 놓치고 있던 관계를 다시 바라보다

**HALO**는 자녀와 부모님이  
매일 하나의 질문과 작은 행동을 통해  
관계를 한 권의 이야기로 완성해가는 서비스입니다.

<br/>

</div>

---

# 📌 프로젝트 소개

> 🖼️ 자녀와 부모님이 매일 하나의 질문과 작은 행동으로 관계를 한 권의 이야기로 완성해가는 서비스

HALO Backend는 Spring Boot 기반의 REST API 서버입니다.

회원 인증부터 스토리북, 테마 전시관, 캘린더, 알림까지 서비스 전반의 비즈니스 로직과 데이터를 관리하며 안정적인 API를 제공합니다.

---

# 🗓️ 개발 기간

**2026.06 ~ 진행 중**

---

# 👥 팀원 소개

|      이름      | 담당 도메인             |                       GitHub                       |
|:------------:|--------------------|:--------------------------------------------------:|
| **김서현 (PL)** | 테마 전시관, 캘린더        |    [@seohyeonS2](https://github.com/seohyeonS2)    |
|   **홍정민**    | 회원, 온보딩, 관계 정보, 약관 | [@jmjmin24](https://github.com/jmjmjmin24-commits) |
|   **한혜담**    | 스토리북, 장(Chapter)   |        [@hhd517](https://github.com/hhd517)        |
|   **신재현**    | 알림, 기념일            |  [@shin-jaehyun](https://github.com/shin-jaehyun)  |

> 역할은 프로젝트 진행 상황에 따라 변경될 수 있습니다.

---

# 🛠 Tech Stack

### 💻 Language

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

### 🌱 Framework

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=for-the-badge)
![Spring Validation](https://img.shields.io/badge/Spring_Validation-6DB33F?style=for-the-badge)

### 🔐 Security

![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens)

### 🗄️ Database

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### ⚙️ Build & Collaboration

![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)

---

# 🚀 주요 Backend 기능

### 👤 회원 / 온보딩

- 회원가입 및 로그인
- JWT 기반 사용자 인증
- 온보딩
- 관계 정보 관리
- 약관 동의 관리

### 📚 스토리북

- 스토리북 조회
- Chapter 조회 및 진행
- 스토리북 기록 관리

### 🖼️ 테마 전시관

- 테마별 스토리북 조회
- 완성한 스토리북 전시

### 📅 캘린더

- 월별 스토리북 수행 기록 조회
- 일정 및 활동 내역 관리

### 🔔 알림

- 서비스 알림
- 기념일 알림

---

# 📄 Documentation

| Document          | Link                                                     |
|-------------------|----------------------------------------------------------|
| API Specification | Notion                                                   |
| ERD               | [ERDCloud](https://www.erdcloud.com/d/hPCXfA4KaoptY5vMp) |

> 프로젝트 진행에 따라 문서는 지속적으로 업데이트됩니다.

---

# 📂 프로젝트 구조

```text
src
├── main
│   ├── java
│   │   └── com.umc.halo
│   │       ├── domain
│   │       │   ├── content
│   │       │   ├── member
│   │       │   ├── notification
│   │       │   ├── record
│   │       │   ├── setting
│   │       │   ├── tag
│   │       │   └── term
│   │       ├── global
│   │       └── HaloServerApplication.java
│   └── resources
└── test
```

| Package     | Description   |
|-------------|---------------|
| `domain`    | 도메인별 비즈니스 로직  |
| `global`    | 공통 설정 및 예외 처리 |
| `resources` | 설정 파일         |
| `test`      | 테스트 코드        |

---

# 📋 Requirements

- Java 21
- Spring Boot 4.1.0
- Gradle 8.x
- MySQL 8.x

---

# 📐 Convention

### 🌿 Branch Strategy

| Branch              | Description        |
|---------------------|--------------------|
| `main`              | 배포 브랜치             |
| `develop`           | 개발 브랜치             |
| `feat/#이슈번호-설명`     | 기능 개발              |
| `fix/#이슈번호-설명`      | 버그 수정              |
| `refactor/#이슈번호-설명` | 리팩토링               |
| `chore/#이슈번호-설명`    | 설정 및 기타 작업         |
| `docs/#이슈번호-설명`     | README, 문서 및 주석 수정 |
| `hotfix/#이슈번호-설명`   | 긴급 수정              |

---

### 💬 Commit Convention

| Type       | Description |
|------------|-------------|
| `feat`     | 새로운 기능 추가   |
| `fix`      | 버그 수정       |
| `refactor` | 리팩토링        |
| `docs`     | 문서 수정       |
| `chore`    | 설정 및 기타 작업  |
| `init`     | 프로젝트 초기 설정  |

---

### 🔀 Pull Request

- Base Branch : `develop`
- Reviewer 1명 이상 지정
- AI Code Review 확인 및 반영
- 팀원 1명 이상의 Approve 후 Merge
- PR 제목은 `[Type] 구현 내용` 형식을 사용

---

<div align="center">

### HALO Team

부모님과 나 사이의 작은 안녕을  
하루 한 장의 이야기로 기록합니다.

<br/>

<img src="https://capsule-render.vercel.app/api?type=waving&height=120&section=footer&color=0:FF7B10,100:FFF4E8" />

</div>