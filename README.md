# 💑 Withus (위더스) - 커플을 위한 데일리 질문 & 추억 기록 서비스

> "사진으로 이어지는, 커플의 일상"

WITHUS는 사진으로 연인과 하루와 마음을  나누는 서비스입니다. 말을 길게 꺼내지 않아도, 부담스러운 질문을 하지 않아도, 같은 질문에 답하고 같은 순간을 공유하며
서로의 하루를 함께한 추억으로 기록합니다.
<img width="3579" height="5032" alt="2 _ Custom _ 297x420 mm" src="https://github.com/user-attachments/assets/55ae6211-230d-45fc-a745-79df2c4d7f08" />
<br/>

## 🔗 링크
[PlayStore에서 보기](https://play.google.com/store/apps/details?id=com.yeogijeogi.android.withus&hl=ko)  
[AppStore에서 보기](https://apps.apple.com/kr/app/%EC%9C%84%EB%8D%94%EC%8A%A4/id6758986808)

## ✨ 핵심 기능

<!-- 여기에 핵심 기능을 보여주는 스크린샷 1~5장을 가로로 넣기 -->
<img width="1920" height="1080" alt="#9" src="https://github.com/user-attachments/assets/65ed3212-fe34-4d36-884f-8ba0d68546bf" />
<img width="1920" height="1080" alt="#11" src="https://github.com/user-attachments/assets/63501de3-cdc8-4828-8812-90cc759e0bb3" />
<img width="1920" height="1080" alt="#10" src="https://github.com/user-attachments/assets/03043688-01ba-4a33-986e-d0cd0ac7a10f" />
<img width="1920" height="1080" alt="#12" src="https://github.com/user-attachments/assets/2df02465-be88-4ea9-ab20-10a55cd7b376" />
<img width="1920" height="1080" alt="#13" src="https://github.com/user-attachments/assets/aca662ee-beba-453e-abbf-930b8d762d36" />

### 1. 🔗 커플 연결 & 초대
- **초대 코드 기반 연결:** 한 명이 초대 코드를 발급하면, 상대방이 코드를 입력해 커플로 연결됩니다.
- **초대 페이지:** 초대 링크 접속 시 앱 설치 또는 실행을 유도하는 랜딩 페이지를 제공합니다.
- **온보딩 플로우:** 로그인 → 프로필 설정 → 커플 연결 → 키워드 설정으로 이어지는 단계별 온보딩 상태를 서버에서 관리합니다.

### 2. 💬 매일 하나의 커플 질문
- **자동 질문 배정:** 매일 자정에 스케줄러가 커플에게 새로운 질문을 자동으로 배정합니다.
- **함께 답하기 + 사진 기록:** 같은 질문에 두 사람이 각자 답하고, 질문마다 사진을 함께 첨부해 그날의 순간을 남길 수 있습니다.

### 3. 🏷️ 관계 키워드
- **우리만의 키워드:** 커플이 함께 정한 관계 키워드를 설정하고, 매일의 키워드 기록을 사진과 함께 남길 수 있습니다.

### 4. 📸 네컷 사진 & 추억 타임라인
- **주간 추억 사진:** 커플이 해당 주차에 올린 사진을 모아 추억 사진을 자동으로 만들어줍니다.
- **커스텀 추억 사진:** 커플이 직접 갤러리에서 사진을 선택해 커스텀 추억 사진을 만들 수 있습니다.

### 5. 🗓️ 캘린더 아카이브
- **한눈에 보는 기록:** 그동안 보낸 사진들을 캘린더에 모아, 날짜별로 둘의 기록을 다시 꺼내 볼 수 있습니다.

### 6. 🔔 푸시 알림
- **실시간 알림:** Firebase FCM을 통해 새 질문 배정, 상대방의 답변/사진 등록 등 주요 이벤트를 푸시로 전달합니다.

<br/>

## 🛠 기술 스택

### Backend
- **Language & Framework:** Java 21, Spring Boot 3.5.9
- **Database & ORM:** Spring Data JPA, MySQL, Redis (토큰 캐싱)
- **Security:** Spring Security, JWT, OAuth 2.0 (Kakao / Apple)
- **외부 연동:** AWS S3(이미지), AWS lambda(이미지 압축), Firebase FCM(푸시), Kakao/Apple Oauth
- **Documentation:** Swagger
- **View:** Thymeleaf (초대 랜딩 페이지)

### Infrastructure & DevOps
- **Cloud:** AWS (EC2, RDS, S3)
- **Container:** Docker, Docker Compose
- **Web Server:** Nginx
- **CI/CD:** GitHub Actions

<br/>

## 🏗 아키텍처 다이어그램

<!-- 여기에 시스템 아키텍처 다이어그램 이미지를 넣기 -->

<br/>
