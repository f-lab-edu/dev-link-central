## dev-link-central

개발자들이 참여하고 소통하며, 연결점으로 되는 커뮤니티 서비스 입니다.

<br/>

## 📌 dev-link-central 서비스 설명

IT업계는 새로운 지식이 계속 업데이트 되기에, 트렌드를 잘 알아야 합니다.

하지만 신입 개발자 or 개발자를 준비하는 취업 준비생에게는 해당 정보를 구하기 어려운건 사실입니다.

그리하여, 신입 개발자로 취업하기 위해서는 어떤 것이 필요하고, 어떤 공부를 해야 하는지 알려주는 것입니다.

즉, 카카오톡 오픈채팅(5xx명)이 아닌, 소수의 스터디 인원을 구성하여 소통하고 정보를 얻을 수 있는 커뮤니티 서비스


<br/><br/>


## 📌 프로젝트 목표


- 프론트엔드의 부분은 생략하고 벡엔드에 초점을 맞춰 백엔드 개발에 주력합니다.


- 단순 기능 구현 뿐 아니라 성능, 코드의 재사용성 및 유지보수성을 고려하여 구현하는 것을 목표로 개발합니다.


- 대규모 트래픽을 처리할 수 있는 성능적으로 우수한 서비스 제작하기 위해 노력합니다.


- 객체 지향 원리를 적용하여 CleanCode를 목표로 유지보수가 용이한 코드 구현합니다.


- SOLID 원칙과 디자인패턴의 이해를 바탕을 하여 최대한 도메인 주도 설계를 하기 위해 노력합니다.


- 중복되는 코드들, 불필요하게 수정이 일어날 코드들을 최소화해 확장이 용이하게 노력합니다.



<br/><br/>


## 📌 사용 기술 및 개발 환경


### ✔️ 기술 스택

- Java, Spring Boot, IntelliJ, Gradle, JPA / QueryDsl, MySQL


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


## 📌 주요 기능 & 뷰페이지 화면

<details>
  <summary> 📱 로그인 및 유저 기능</summary>
  <img src="./demo/로그인_화면.png" alt="로그인 화면">
</details>

<details>
  <summary> 📱 프로필 기능</summary>
  <!-- 내용 -->
</details>

<details>
  <summary> 📱 스터디 그룹 기능</summary>
  <!-- 내용 -->
</details>

<details>
  <summary> 📱 친구 기능</summary>
  
</details>

<details>
  <summary> 📱 게시판 및 댓글 기능</summary>
  <!-- 내용 -->
</details>

<details>
  <summary> 📱 피드 기능</summary>
  <!-- 내용 -->
</details>

<details>
  <summary> 📱 알림 기능</summary>
  <!-- 내용 -->
</details>




<br/><br/>


## 📌 프로젝트를 진행하며 고민한 Technical Issue

- [#1] 하나씩 추가하며 내려가기







<br/><br/>

## 📌 브랜치 관리 전략

dev-link-central는 `Git-Flow` 를 이용하여 브랜치를 관리하였습니다.


```
나중에 그림 추가
```


✔️ `master` : 배포시 사용할 브랜치.

- 완벽히 제품으로 출시될 수 있는 브랜치를 의미합니다.

✔️ `develop` : 다음 버전을 개발하는 브랜치.
- feature에서 리뷰 완료한 브랜치를 Merge 하고 합니다.

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


<br/><br/>


## ER 다이어그램


![이미지](/schema/ERD.svg)


test
