## dev-link-central

취업 준비생과 현직자들이 참여하고 소통하며 연결될 수 있는 커뮤니티 서비스입니다.

<br/>

## 📌 dev-link-central 서비스 설명

IT 업계는 새로운 지식이 계속 업데이트되기 때문에 트렌드를 잘 파악해야 합니다.

그러나 신입 개발자나 취업 준비생에게는 이러한 정보를 얻기가 어려운 것이 현실입니다.


그래서 신입 개발자로 취업하기 위해 필요한 것들과 어떤 공부를 해야 하는지 알려주는 커뮤니티

서비스를 제공하려 합니다. 이는 카카오톡 오픈채팅(500명 이상)과 같은 대규모 채팅방이 아닌,

소수의 스터디 인원을 구성하여 소통하고 정보를 얻을 수 있는 커뮤니티 서비스입니다.

<br/><br/>

## 📌 프로젝트를 진행하며 고민한 Technical Issue

블로그 작성 방법은 다음과 같습니다.

```text
- 어떻게 했기에 문제 상황을 마주하게 되었는지?
- 이게 왜 문제인지?
- 문제를 어떻게 감지했는지?
- 어떻게 해결했는지?
- 그렇게 하면 왜 해결되는지?
- 얼마나 개선되었는지?
- 배우는 것은 무엇인지?
- 무엇을 얻을 것인지?
- 이 방법이 최선이었는지?
- 다른 방법은 없었는지?
```

- [[#1] 비밀번호 검증 및 데이터 무결성 유지](https://alstjr706.tistory.com/384)
- [[#2] 닉네임 중복 검증과 예외 처리](https://alstjr706.tistory.com/387)
- [[#3] HTTP 415 오류(회원가입 + 로그인)](https://alstjr706.tistory.com/388)
- [[#4] 유효성 검사 및 이메일 인증 오류](https://alstjr706.tistory.com/408)
- [[#5] 비밀번호 찾기 및 재설정과 임시 비밀번호 문제](https://alstjr706.tistory.com/379)
- [[#6] 비밀번호 해싱과 Security로 보안 높이기](https://alstjr706.tistory.com/383)
- [[#7] JWT 로그인 및 Spring Security 인증 문제](https://alstjr706.tistory.com/395)
- [[#8] JWT 도입 후 API 인증 이슈](https://alstjr706.tistory.com/398)
- [[#9] JPA 엔티티 Dirty Checking 오류](https://alstjr706.tistory.com/389)
- [[#10] JPA 논리적 삭제와 데이터 무결성 문제](https://alstjr706.tistory.com/385)
- [[#11] 트랜잭션 및 외래키 제약 조건 문제](https://alstjr706.tistory.com/381)
- [[#12] REST API와 JPA 기반 프로필 및 친구 관리 오류](https://alstjr706.tistory.com/397)
- [[#13] 동시성 문제로 인한 조회수 및 좋아요](https://alstjr706.tistory.com/404)
- [[#14] 페이징, 무한 스크롤(댓글) 기능 구현 중 문제](https://alstjr706.tistory.com/391)
- [[#15] AWS S3 연동 및 이미지 업로드 문제](https://alstjr706.tistory.com/405)
- [[#16] AWS EC2 인스턴스와 MySQL 연결 문제](https://alstjr706.tistory.com/411)



<br/><br/>

## 📌 시퀀스 다이어그램

- [시퀀스 다이어그램 확인](https://github.com/f-lab-edu/dev-link-central/blob/master/demo/%EC%8B%9C%ED%80%80%EC%8A%A4.md)

![이미지](/demo/시퀀스_이미지1.png)



<br/><br/>

## 📌 서비스 아키텍처

![이미지](/demo/아키텍처.png)

<br/><br/>

## 📌 ER 다이어그램

![이미지](/schema/project-erd.svg)

<br/><br/>

## 📌 사용 기술 및 개발 환경

### ✔️ 기술 스택

- Java, Spring Boot, IntelliJ, Gradle, JPA / QueryDsl, MySQL, JUnit, AWS, EC2, RDS


### ✔️ layered architecture & 규칙

```
- presentation : Controller 등 외부(client)와 연결되는 코드를 작성하는 패키지
- service : 비즈니스 로직을 작성하는 패키지
- database : 데이터베이스에 저장되는 데이터를 정의하는 패키지(e.g. Jpa @Entity, @Repository 등)
- infrastructure : 애플리케이션에 필요한 설정 등을 작성하는 패키지(e.g. SecurityConfiguration 등)
- common : 모든 패키지에서 공통으로 사용할 수 있는 코드를 모아두는 패키지
```

- `presentation` -> `service` -> `database` 방향으로 참조는 하되 반대 방향으로는 참조하지 않는다

  - `presentation` 패키지 -> `service` 패키지

  - `service` 패키지 -> `database` 패키지


<br/><br/>

## 📌 브랜치 전략 (Git-Flow)

### dev-link-central는 `Git-Flow` 를 이용하여 브랜치를 관리하였습니다.

![이미지](/demo/깃.png)

✔️ `master` : 배포시 사용할 브랜치. -> (완벽히 제품으로 출시될 수 있는 브랜치를 의미합니다)

✔️ `develop` : 다음 버전을 개발하는 브랜치. -> (feature에서 리뷰 완료한 브랜치를 Merge 하고 합니다.)

✔️ `feature` : 기능을 개발하는 브랜치

✔️ `release` : 배포를 준비할 때 사용할 브랜치

✔️ `hotfix` : 배포 후에 발생한 버그를 수정 하는 브랜치


<br/><br/>

## 📌 Commit Message Convension

협업을 위한 git 커밋 컨벤션으로 진행하였습니다.

```
- feat : 새로운 기능에 대한 커밋
- build : 빌드 관련 파일 수정에 대한 커밋
- chore : 그 외 자잘한 수정에 대한 커밋(rlxk qusrud)
- docs : 문서 수정에 대한 커밋
- style : 코드 스타일 혹은 포맷 등에 관한 커밋
- refactor : 코드 리팩토링에 대한 커밋
```
