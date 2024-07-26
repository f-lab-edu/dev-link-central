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

- [[#1] Java에서 블로킹, 논블로킹, 동기, 비동기 코드](https://alstjr706.tistory.com/372)
- [[#2] 자바와 스프링 프레임워크에서의 싱글톤 패턴과 전략 패턴](https://alstjr706.tistory.com/373)
- [[#3] MySQL과 S3를 활용한 효율적인 설계](https://alstjr706.tistory.com/374)
- [[#4] MySQL 외래키 제약 조건 설정](https://alstjr706.tistory.com/375)
- [[#5] AWS에서 MySQL 연결 문제](https://alstjr706.tistory.com/376)
- [[#6] 닉네임 중복 체크 기능 구현 및 문제 해결 과정](https://alstjr706.tistory.com/377)
- [[#7] 이메일 인증 기능 구현 및 문제 해결](https://alstjr706.tistory.com/378)
- [[#8] 비밀번호 찾기 및 재설정 기능 구현(session 사용해보기)](https://alstjr706.tistory.com/379)
- [[#9] NonUniqueResultException 해결하기](https://alstjr706.tistory.com/380)
- [[#10] 이메일 전송 및 트랜잭션 문제 해결](https://alstjr706.tistory.com/381)
- [[#11] JPA 엔터티 수정 과정의 문제 해결](https://alstjr706.tistory.com/382)
- [[#12] 안전한 비밀번호 검증 및 로그인 기능 구현하기](https://alstjr706.tistory.com/383)
- [[#13] 회원가입 기능 구현 및 문제 해결](https://alstjr706.tistory.com/384)
- [[#14] ]()
- [[#15] ]()
- [[#16] ]()
- [[]]()
- [[]]()
- [[]]()
- [[]]()
- [[]]()




<br/><br/>

## 📌 시퀀스 다이어그램

- [시퀀스 다이어그램 확인하기](https://github.com/f-lab-edu/dev-link-central/blob/master/demo/%EC%8B%9C%ED%80%80%EC%8A%A4.md)

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

- [브랜치 전략 확인하기](https://github.com/f-lab-edu/dev-link-central/blob/master/demo/%EB%B8%8C%EB%9E%9C%EC%B9%98%20%EA%B4%80%EB%A6%AC%20%EC%A0%84%EB%9E%B5.md)


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
