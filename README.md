# CINNE, 씬느 🍿
https://chxxyx0.notion.site/d7e7fcad0b4f4b83afe4c9ea35a4ab01

# 기획서

## 🍿 **프로젝트 주제**

- 코비스(영화진흥위원회) 오픈 API를 활용한 현재 상영 영화 예매 관리 서비스
    
    사용자들 카테고리별 영화 추천 기능과 관람 후기 공유 기능 제공
    

## **프로젝트 기획 배경**

- 최근 OTT 서비스 수요 증가 및 코로나 팬데믹 직격탄을 맞은 극장가 위기 극복
- 학습한 내용을 토대로 예매, 결제, 커뮤니티 서비스 구현해보려는 목적

## **프로젝트 구조**

## ERD

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fdf53ca4-221a-423b-a17a-e84a0abf5db7/Untitled.png)

## 사용 기술 스택

- SpringBoot 2.7.5
- Java 11
- MySQL
- JPA

## 프로젝트 기능

<aside>
💡 **주요 기능**

- [ ]  회원 관리 ( 회원가입 & 로그인 )
- [ ]  예매 관리 ( 예매하기 )
- [ ]  결제 관리 ( 예매 결제 / 결제 취소)
- [ ]  관람 후기 공유 ( 후기 등록 / 수정 / 삭제 / 목록 / 상세 보기 )
- [ ]  영화 검색 ( 카테고리별 검색 - 장르, 감독, 배우 )
- [ ]  관리자 ( 관리자 CRUD / 회원 관리 / 상영 영화 관리 )
</aside>

### 📽️  상세 기능

- **회원가입과 로그인**
    - User는 회원가입을 할 수 있다.
        - 회원가입시 이메일(아이디)과 비밀번호, 이름, 전화번호 정보가 필요하다.
        - 회원가입시 이미 회원가입된 이메일로 회원가입을 시도하면 에러를 발생한다.
    - User는 회원가입의 정보를 이용하여 로그인을 할 수 있다.
        - 로그인시 회원가입한적 없는 이메일을 이용하여 로그인을 시도하면 에러가 발생한다.
        - 로그인시 비밀번호가 일치하지 않는다면 에러가 발생한다.
    - User는 회원 상세 정보를 볼 수 있다.
        - 로그인 한 계정인 본인 정보만 확인할 수 있다.
    - User는 회원 정보를 수정할 수 있다.
        - 비밀번호, 이메일, 전화번호 변경이 가능하다.
        - 로그인 한 계정인 본인 정보만 수정이 가능하다.
    - User는 탈퇴(정지)가 가능하다.
        - 사용자 정보 삭제가 아닌 정지 회원 타입으로 변경된다.
        - 정지 회원이 될 경우, 정지된 계정으로 로그인이 불가능하다.
        - 본인이 로그인 한 계정만 탈퇴할 수 있다.
        - 탈퇴 전, 계정의 비밀번호를 입력해야 한다.
    
- **예매 관리 API**
    
    `*현장 결제만 가능하다는 전제 하에 구현 진행`
    
    - 영화 예매 하기
        - 예매는 로그인 한 유저만 예매할 수 있다.
        - 예매 일자(관람 일자), 예매 시간, 예매 좌석, 상영관, 상영극장, 작품을 필수 선택해야 예매가 가능하다.
    - 예매 내역 상세 보기
        - 본인의 예매 내역만 확인할 수 있다.
        - 예매 일자(관람 일자), 예매 시간, 예매 좌석, 상영관, 상영극장, 작품을 확인할 수 있다.
- **예매 결제 관리 API**
    - 결제 하기
        - 카카오페이 결제 API를 활용하여 단건 결제만 가능하다.
        - 결제 가격은 모두 동일하다는 전제 하에 구현 진행.
        - 결제가 완료되면 예매 완료.
    - 결제 취소하기
        - 본인의 결제 내역(예매 내역)만 취소할 수 있다.
        - 상영 일자와 관람 시작 시간(-10분)이 지난 내역은 취소할 수 없다.
    
- **관람 후기 공유 API**
    - 후기 등록
        - 후기 등록은 로그인 한 유저만 등록 가능하다.
        - 후기 게시판은 크게 국내, 해외 카테고리로 나눈 후 상영 상태로 등록 가능하다.
            - 상영 중인 영화에 대한 후기 등록이 가능하다.
                
                → 상영 중인 영화는 예매 내역이 있는 유저만 작성할 수 있다.
                
            - 상영 종료 영화에 대한 후기 등록이 가능하다.
                
                → 상영 종료 영화는 예매 내역과 관계없이 작성이 가능하다.
                
            - 🚫  상영 예정인 영화는 후기 등록이 불가능하다.
    - 별점 기능
        - 후기 작성 시에 별점 등록이 가능하다.
    - 후기 삭제
        - 본인이 등록한 후기만 삭제할 수 있다.
    - 후기 수정
        - 본인이 등록한 후기만 수정할 수 있다.
        - 내용, 별점 수 를 변경할 수 있다. (수정일자가 업데이트 됨)
    - 후기 상세 보기
        - 게시판의 글은 로그인 하지 않은 유저도 볼 수 있다.
        - 게시판 글 상세보기에서는 영화 제목, 작성일, 수정일, 작성자, 본문의 내용이 보인다.
            - 삭제된 글을 유저가 접근한다면 에러를 발생시킨다.
    
- **영화 검색 API**
    - 장르, 감독, 배우 별 검색 조회가 가능하다.
- **관리자 API**
    - 관리자 CRUD
        - 관리자 등록, 삭제, 관리자 정보 수정
    - 회원 관리
        - 관리자는 회원 목록을 조회할 수 있다.
        - 관리자는 회원의 상태를 지정할 수 있다. (회원, 관리자 지정, 정지 계정)
    - **상영 영화 관리 (추가 수정 삭제)**
        - 상영 상태 - 관리자는 예정, 상영 중, 상영 종료 변경이 가능하다.
        - 상영 일자 - 관리자는 상영 일정 지정이 가능하다.
    

### 📢 기능 분담

🟢 이 철 : 데이터 파싱, 관리자 API, 영화 검색 API

🟣 허정주 : 예매 관리 API, 결제 API

🔵 서채영 : 회원 관리 API, 후기 게시판 API