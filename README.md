# 스프링부트 웹앱 프로젝트 [![Build Status](https://travis-ci.org/Integerous/Restful-WebApp.svg?branch=develop)](https://travis-ci.org/Integerous/Restful-WebApp)

## 0. 개발 환경
- RESTful API 기반 Web application
- Spring Boot 2.0.4
- Gradle 
- Mustache
- lombok 1.18.2
- PostgreSQL
- H2
- Eclipse
- Github
- Travis CI
- AWS RDS (PostreSQL)
- AWS EC2 (Linux)
- AWS S3
- AWS CodeDeploy
- ...

## 1. 개발 계획
|일정|주제|비고|
|---|---|---|
|9/4 화|0. 프로젝트 시작|  |
||1. 개발환경 세팅|Spring boot 프로젝트 구조 학습|
||2. DB 모델링|제3정규화|
||3. DB 생성 및 연동|AWS RDS PostgreSQL 활용|
||4. 회원가입/로그인 구현|JWT 활용|
||5. 상품목록 게시판 구현|다른 게시판은 추후에 추가|
||(6. 상품구매 구현)|결제기능은 가상 처리|
||(7. 구매이력확인 구현)|  |
||8. 화면 구현|jQuery,Bootstrap 활용|
||9. 테스트 및 배포자동화 구축|AWS CodeDeploy 및 CI도구 활용|
|~9/14 금|10. 배포 및 피드백|NginX 활용|

## 2. 개발 과정
### 2.1. 프로젝트 개발 환경 설정
- ***Spring boot starter*** 사용
- 빌드 도구는 ***Gradle*** 사용
- 템플릿 엔진은 ***Mustache*** 사용
- 테스트용 메모리DB ***H2*** 사용
- 데이터베이스는 ***AWS RDS PostgreSQL*** 사용
- 서버는 ***AWS EC2 Linux*** 사용
- CI 도구는 ***Travis CI*** 사용
- ...

### 2.2. DB 모델링
- Lucid Chart 사용
- 제3정규화
### 2.3. 회원가입 기능 구현
- 회원 목록
- 회원 정보 수정
- 회원 탈퇴(추후 구현)
### 2.4. 로그인 기능 구현
### 2.5. 문의/답변 게시판 구현
- LocalDateTime 사용
### 2.6. 공지사항 게시판 구현
### 2.7. 작품 게시판 구현
...

### 2.99.(번외)
- gradle 빌드 속도 높이기
  - `${HOME}/.gradle/gradle.properties` 생성해서 `org.gradle.daemon=true` 입력

## 3. 테스트/빌드/배포 자동화 (요약정리)
>Github + EC2 + Travis CI + AWS S3 + AWS CodeDeploy  
>[이동욱님 블로그](https://jojoldu.tistory.com/265?category=635883)에 상세한 설명이 있다.
### 3.1. 한 문장으로 요약  
- 프로젝트를 생성(수정)하여 Github에 push하면, Travis CI가 자동으로 테스트 및 빌드하여 S3에 배포 파일을 업로드하고, CodeDeploy가 배포 파일을 S3에서 받아와서 EC2에 올리면, EC2에 작성한 쉘스크립트에 의해 배포 파일이 실행되어 배포가 완료된다.

### 3.2. 각 도구의 역할
- Travis CI
  - 프로젝트 테스트 및 빌드 자동화
  - 빌드 완료 시, S3에 빌드된 배포 파일 업로드
  - S3에 업로드 완료 시, 배포 파일을 EC2에 올리도록 CodeDeploy 실행
- AWS S3
  - Travis CI가 빌드한 배포 파일의 저장소
- AWS Code Deploy
  - S3에서 가져온 배포 파일을 EC2에 올림(배포)
  - EC2에 올라간 파일이 실행되도록 하는 쉘스크립트 파일을 우회하여 실행

### 3.3. 전체 과정 Index
1. [EC2 생성](https://github.com/Integerous/Restful-WebApp#ec2-%EC%83%9D%EC%84%B1)
2. [AWS RDS PostgreSQL 생성](https://github.com/Integerous/Restful-WebApp#aws-rds-postgresql-%EC%83%9D%EC%84%B1)
3. [EC2에 Git 설치 및 프로젝트 Clone](https://github.com/Integerous/Restful-WebApp#ec2%EC%97%90-git-%EC%84%A4%EC%B9%98-%EB%B0%8F-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-clone)
4. [Travis CI 연동 (테스트&빌드 자동화)](https://github.com/Integerous/Restful-WebApp#travis-ci-%EC%97%B0%EB%8F%99-%ED%85%8C%EC%8A%A4%ED%8A%B8%EB%B9%8C%EB%93%9C-%EC%9E%90%EB%8F%99%ED%99%94)
5. [AWS CodeDeploy 연동 (배포 자동화)](https://github.com/Integerous/Restful-WebApp#aws-code-deploy-%EC%97%B0%EB%8F%99-%EB%B0%B0%ED%8F%AC-%EC%9E%90%EB%8F%99%ED%99%94)
6. [Travis CI와 AWS S3 연동](https://github.com/Integerous/Restful-WebApp#travis-ci%EC%99%80-aws-s3-%EC%97%B0%EB%8F%99)
7. [Travis CI & S3 & AWS CodeDeploy 연동](https://github.com/Integerous/Restful-WebApp#travis-ci--s3--codedeploy-%EC%97%B0%EB%8F%99)


### 3.4. 구어체로 표현한 전체 과정
>더 잘 이해하기 위해 구어체로 써보는 테스트&빌드&배포 자동화 과정
1. 테스트와 빌드를 자동화하기 위해 Travis CI에서 프로젝트 저장소를 연동시키고,
2. 프로젝트에 `.Travis.yml` 파일을 생성해서 로컬에서 push할때 Travis CI가 테스트와 빌드하도록 설정한다.
3. 배포 자동화를 위해 AWS CodeDeploy와 연동해야 하는데, CodeDeploy를 쓰려면 Travis CI가 사용할 수 있는 사용자계정이 필요하다.
4. AWS IAM에서 Travis CI가 AWS CodeDeploy용으로 사용할 사용자 계정을 생성한다.
5. 사용자 생성시 만들어진 access key와 secret key를 다운로드하여 보관한다.
6. 생성한 사용자 계정이 S3와 CodeDeploy에 접근할 수 있도록 FullAccess 정책들을 선택하여 연결한다.
7. 빌드된 배포파일(.jar)을 보관할 S3 버킷을 생성한다.
8. IAM에서 생성한 사용자에 EC2와 CodeDeploy을 위한 역할 2가지를 추가한다.(EC2RoleForCodeDeploy, CodeDeployRole)
9. EC2 인스턴스에 IAM 역할(EC2RoleForCodeDeploy)을 연결하고, CodeDeploy Agent를 설치한다.
10. CodeDeploy Agent보다 AWS CLI를 우선 설치하여 AWS를 커맨드로 다룰 수 있도록 한다.
11. EC2가 CodeDeploy 이벤트를 수신할 수 있도록 CodeDeploy Agent를 설치해야한다.
12. AWS CodeDeploy CLI를 설치하고, 이 때 생성되는 ./install 파일을 이용해 CodeDeploy Agent를 설치하고 실행한다.
13. EC2 인스턴스가 부팅되면 자동으로 CodeDeploy Agent가 실행되도록 `/etc/init.d`에 쉘스크립트 파일을 생성한다.
14. CodeDeploy에는 저장 기능이 없으므로 Travis CI가 빌드한 결과물을 S3에 보관하고 CodeDeploy가 가져가도록 `.travis.yml` 파일에 설정한다.
15. Github에 AWS access key와 secret key가 노출하지 않기 위해 Travis CI에서 키-값 등록
16. AWS CodeDeploy 콘솔에서 어플리케이션을 생성하는데, CodeDeploy가 EC2에 접근 가능하게 하는 CodeDeployRole을 ARN으로 선택한다.
17. Travis CI에서 빌드가 끝나면 S3에 zip파일이 전송된다. 이 파일을 받아올 디렉토리를 생성한다.
18. 프로젝트에 CodeDeploy 설정용 `appspec.yml` 파일을 생성하여 zip파일을 받아오기 위해 생성한 디렉토리를 입력한다.
19. CodeDeploy는 `appspec.yml` 파일을 통해서 어떤 파일들을 어느 위치에 배포하고, 이후에 어떤 스크립트를 실행할 것인지 관리한다.
19. Travis CI가 CodeDeploy도 실행시키도록 `.travis.yml` 파일에 설정 추가한다.
20. 배포 파일(.jar)을 실행해야 실제로 배포되는 것이므로, 배포 파일을 실행시키는 `deploy.sh` 파일을 EC2에 생성한다.
21. CodeDeploy가 EC2에 배포를 마치면 `deploy.sh` 파일을 실행하도록 `appspec.yml`에 설정한다.
22. `deploy.sh` 파일은 프로젝트 내부에 있지 않기 때문에 CodeDeploy가 바로 실행할 수 없다. 그러므로 우회가 필요하다.
23. `deploy.sh`가 실행되도록 하는 `execute-deploy.sh` 파일을 프로젝트 내부에 생성한다.

## 4. 테스트/빌드/배포 자동화 (상세 과정)

### 4.1. EC2 생성
- Amazon Linux 2018 선택
- harusketch pem 키 생성
- Elastic IP 적용 (적용안하면 인스턴스 재시작 될 때마다 IP 바뀜)
- 보안그룹 설정(내 작업공간 IP, HTTP, HTTPS)
- EC2 터미널 접속
  - SSH 접속 쉽게하기
    - 키파일(.pem)을 `~./ssh/`로 복사 `$ cp harusketch.pem ~/.ssh/`
    - `~/.ssh/`에서 키 권한 변경 `$ chmod 600 ./harusketch.pem`
    - `~/.ssh/` 디렉토리에 `config`파일 생성 `$ nano config`
    - config 파일에 아래와 같이 설정
      ~~~config
      ### Haru Sketch
      Host haru(원하는 이름)
            HostName Elastic IP
            User ec2-user (ubuntu 사용시 ubuntu. 그 외에는 ec2-user)
            IdentityFile ~/.ssh/harusketch.pem
      ~~~
    - `ssh haru` 로 EC2 접속
### 4.2. AWS RDS PostgreSQL 생성
#### 4.2.1. RDS 보안그룹 생성 (PostgreSQL 유형으로 생성)
- EC2 인스턴스의 보안그룹 ID를 IP에 입력
- 작업환경 IP 입력
- RDS 보안그룹을 방금 생성한 보안그룹으로 변경
#### 4.2.2. 로컬 작업환경과 RDS 연동
- DBeaver 실행하여 연결 추가
- Host 주소에 RDS 인스턴스의 endpoint 입력
- Database에 인스턴스 생성할때 지정한 DB이름 입력
- 생성
#### 4.2.3. PostgreSQL 사용 시
- Client-encoding(MySQL에서는 Character-set) 값이 UTF8이 default로 지정되어 있어서 default parameter group 사용 가능

### 4.3. EC2에 Git 설치 및 프로젝트 Clone
#### 4.3.1. Java8 설치
- 현재 EC2의 자바 기본버전이 Java7 이므로 Java8로 버전업
  - `$ sudo yum install -y java-1.8.0-openjdk-devel.x86_64`
  - `$ sudo /usr/sbin/alternatives --config java`
- 사용하지 않는 Java7 삭제
  - `$ sudo yum remove java-1.7.0-openjdk`
#### 4.3.2. Git 설치
- `$ sudo yum install git`
#### 4.3.3. 프로젝트 Clone
- 프로젝트를 저장할 디렉토리 생성
  - `$ mkdir app`
  - `$ mkdir app/git`
- 생성한 디렉토리에서 프로젝트 clone
  - `$ git clone -b develop --single-branch [프로젝트 저장소 ssh주소]`
- 프로젝트 잘 받아졌는지 테스트
  - `$ ./gradlew test`
  - `gradlew`파일 : EC2에 Gradle 설치하지 않았거나 Gradle 버전이 달라도 해당 프로젝트에 한해서 Gradle을 쓸 수 있도록 지원하는 Wrapper 파일
#### 4.3.4. 배포 준비
- `$~app/git`에 `deploy.sh` 파일 생성
  ~~~sh
  #!/bin/bash

  REPOSITORY=/home/ec2-user/app/git
  cd $REPOSITORY/Restful-WebApp

  echo "> Git Pull"
  git pull

  echo "> 프로젝트 Build 시작"
  ./gradlew build

  echo "> Build 파일 복사"
  cp ./build/libs/*.jar $REPOSITORY/

  echo "> 현재 구동중인 애플리케이션 pid 확인"
  CURRENT_PID=$(pgrep -f harusketch)

  echo "$CURRENT_PID"
  if [ -z $CURRENT_PID ]; then
      echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
  else
      echo "> kill -2 $CURRENT_PID"
      kill -9 $CURRENT_PID
      sleep 5
  fi

  echo "> 새 어플리케이션 배포"
  JAR_NAME=$(ls $REPOSITORY/ |grep 'harusketch' | tail -n 1)
  echo "> 새 배포 버전의 이름은? ===> $JAR_NAME"

  nohup java -jar $REPOSITORY/$JAR_NAME &
  ~~~
- `deploy.sh`에 실행권한 부여 `$ chmod 755 ./deploy.sh`
#### 4.3.5. 배포
- `$ ./deploy.sh`
- `ps -ef|grep harusketch`로 프로세스 실행 확인
#### 4.3.6. 서비스 접속 테스트
- EC2 인바운드 규칙 편집
  - 8080 포트 오픈
- EC2 인스턴스의 퍼블릭 DNS에 :8080 붙여서 접속 확인

### 4.4. Travis CI 연동 (테스트&빌드 자동화)
#### 4.4.1. 시작
- [Travis CI](https://travis-ci.org/)에 github 아이디로 로그인
- 프로젝트 저장소의 상태 활성화
#### 4.4.2. 프로젝트 설정
- 프로젝트에 `.travis.yml` 파일 생성
  ~~~yml
  language: java
  jdk:
    - openjdk8

  branches:
    only:
      - develop ## 오직 develop 브랜치에 push 될 때만 수행

  # Travis CI 서버의 Home
  cache: ## Gradle을 통해 의존성을 받게 되면 이를 해당 디렉토리에 캐시하여, 같은 의존성은 다음 배포때부터 다시 받지 않도록 설정
    directories:
      - '$HOME/.m2/repository'
      - '$HOME/.gradle'

  script: "./gradlew clean build"
  ## develop 브랜치에 Push 되었을때 수행하는 명령어. 프로젝트 내부에 둔 gradlew를 통해 clean & build 수행

  # CI 실행 완료시 메일로 알람 (slack도 가능)
  notifications:
    email:
      recipients:
        - ryanhan@cloudcash.kr
  ~~~
- Commit & Push 후 Travis CI 저장소 페이지 확인

>여기까지가 테스트와 빌드 자동화.

### 4.5. AWS Code Deploy 연동 (배포 자동화)
#### 4.5.1. IAM 계정 생성
- Travis CI가 사용 할 수 있도록 AWS Code Deploy용 계정 추가
- IAM 콘솔에서 사용자 추가
  - 사용자 이름 입력 (harusketch-deploy)
  - 액세스 유형: 프로그래밍 방식 액세스 체크
- 정책 선택
  - 기존 정책 직접 연결
  - S3로 검색하여 `AmazonS3FullAccess` 선택
  - deploy로 검색하여 `AWSCodeDeployFullAccess` 선택
- 사용자 생성
  - 액세스키와 비밀키 .csv 다운로드
#### 4.5.2. AWS S3 버킷 생성
- 빌드 된 jar 파일을 보관할 S3 버킷 생성
- 버킷 이름 입력 (harusketch-deploy)
- 다른 옵션 없이 생성
#### 4.5.3. IAM Role 추가
- 나 대신 access key & secret key를 사용해 원하는 기능을 진행하게 할 AWS Role 생성
- IAM - 역할 - 역할만들기
- 사용사례 선택 EC2
- 권한 정책 연결
  - EC2RoleForAWSCodeDeploy 로 검색하여 선택
- 역할 이름 입력 (harusketch-EC2CodeDeployRole) 및 생성
#### 4.5.4. IAM Role 추가2
- 사용사례 선택 CodeDeploy
- 권한 정책 연결 (AWSCodeDeployRole)
- 역할 이름 입력 (harusketch-CodeDeployRole) 및 생성
#### 4.5.5. 역할들을 각 AWS 서비스에 할당 
- EC2에 CodeDeploy Role 추가
  - EC2 인스턴스 설정 - IAM 역할 연결/바꾸기
  - `harusketch-EC2CodeDeployRole` 선택
- EC2에 CodeDeploy Agent 설치 (CodeDeploy에서 실행하는 이벤트를 EC2에서 받아서 처리할 수 있도록)
  - EC2 ssh 접속
  - AWS CLI 설치 (AWS를 커맨드로 다루기 위해)
    - `$ sudo yum -y update`
    - `$ sudo yum install -y aws-cli`
  - AWS CLI 기본 설정
    - `/home/ec2-user/` 에서 `$ sudo aws configure`
    - 액세스키와 비밀키 입력 (사용자 생성할 때 다운받은 .csv 파일)
    - region name 입력 (ap-northeast-2)
    - output format 입력 (json)
  - AWS CodeDeploy CLI 설치
    - `/home/ec2-user/` 에서
    - `$ aws s3 cp s3://aws-codedeploy-ap-northeast-2/latest/install . --region ap-northeast-2`
    - 다운로드가 끝나면 생기는 ./install 파일 실행권한 부여 `$ chmod +x ./install`
  - AWS CodeDeploy Agent 설치 및 실행
    - 생성한 파일(./install)을 이용해 설치 `$ sudo ./install auto`
    - 설치가 완료되면 Agent 실행 확인 `$ sudo service codedeploy-agent status`
- CodeDeploy 실행 자동화
  - EC2 인스턴스가 부팅되면 자동으로 AWS CodeDeploy Agent가 실행되도록 
  - `/etc/init.d` 에 쉘스크립트 파일 생성
    - `$ sudo nano /etc/init.d/codedeploy-startup.sh`
      ~~~sh
      #!/bin/bash

      echo 'Starting codedeploy-agent'
      sudo service codedeploy-agent start
      ~~~
### 4.6. Travis CI와 AWS S3 연동
>CodeDeploy는 저장 기능이 없다. 따라서 Travis CI가 빌드한 결과물을 받아서 CodeDeploy가 가져갈 수 있도록 보관할 수 있는 공간이 필요한데, 보통 S3 쓴다.
#### 4.6.1. 프로젝트 내부의 `.travis.yml 파일에 아래 코드 추가
~~~yml
# Travis CI & S3 연동
before_deploy:  ## 매번 Travis CI에서 파일을 하나하나 복사하면 시간이 많이 걸리므로 프로젝트 폴더 채로 압축해서 S3에 전달하도록 설정
  - zip -r harusketch * ## 현재 위치의 모든 파일을 `harusketch` 이름으로 압축
  - mkdir -p deploy ## deploy 라는 디렉토리를 Travis CI가 실행중인 위치에서 생성
  - mv harusketch.zip deploy/harusketch-zip

deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY   ## Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY   ## Travis repo settings에 설정된 값
    bucket: harusketch-deploy
    region: ap-northeast-2
    skip_cleanup: true
    acl: public_read
    local_dir: deploy  ## before_deploy에서 생성한 디렉토리
    wait-until-deployed: true
    on:
      repo: integerous/Restful-WebApp
      branch: develop
  ~~~
- Travis CI에서 키 값 등록 (Github에 AWS access key와 secret key를 노출하지 않기 위해)
- Travis CI - settings - Environment Variables
- `AWS_ACCESS_KEY`와 `AWS_SECRET_KEY`를 변수로 하는 .csv의 키 값들 등록
- `.travis.yml` 파일 전체 코드
~~~yml
language: java
jdk:
  - openjdk8

branches:
  only:
    - develop ## 오직 develop 브랜치에 push 될 때만 수행


# Travis CI 서버의 Home
cache: ## Gradle을 통해 의존성을 받게 되면 이를 해당 디렉토리에 캐시하여, 같은 의존성은 다음 배포때부터 다시 받지 않도록 설정
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

script: "./gradlew clean build" ## develop 브랜치에 Push 되었을때 수행하는 명령어. 프로젝트 내부에 둔 gradlew를 통해 clean & build 수행


# Travis CI & S3 연동
before_deploy:  ## 매번 Travis CI에서 파일을 하나하나 복사하면 시간이 많이 걸리므로 프로젝트 폴더 채로 압축해서 S3에 전달하도록 설정
  - zip -r harusketch * ## 현재 위치의 모든 파일을 `harusketch` 이름으로 압축
  - mkdir -p deploy ## deploy 라는 디렉토리를 Travis CI가 실행중인 위치에서 생성
  - mv harusketch.zip deploy/harusketch-zip

deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY   ## Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY   ## Travis repo settings에 설정된 값
    bucket: harusketch-deploy
    region: ap-northeast-2
    skip_cleanup: true
    acl: public_read
    local_dir: deploy  ## before_deploy에서 생성한 디렉토리
    wait-until-deployed: true
    on:
      repo: integerous/Restful-WebApp
      branch: develop

# CI 실행 완료시 메일로 알람
notifications:
  email:
    recipients:
      - ryanhan@cloudcash.kr
~~~

### 4.7. Travis CI & S3 & AWS CodeDeploy 연동
#### 4.7.1. AWS CodeDeploy 콘솔에서 어플리케이션 생성
- 어플리케이션 이름 입력 (harusketch)
- 배포 그룹 이름 입력 (harusketch-group)
- 현재 위치 배포
- ARN 으로 기존에 생성한 `CodeDeployRole` 선택 (`EC2CodeDeployRole` 아님)
- 어플리케이션 생성
#### 4.7.2. S3에서 zip을 받아올 디렉토리 생성
- `$ mkdir /home/ec2-user/app/travis`
- `$ mkdir /home/ec2-user/app/travis/build`
- Travis CI의 빌드가 끝나면 S3에 zip파일이 전송되고, 이 zip파일은 `/home/ec2-user/app/travis/build`로 복사되어 압축을 푼다.
#### 4.7.3. AWS CodeDeploy는 `appspec.yml` 에서 설정
- `.travis.yml`과 같은 위치에 `appspec.yml` 파일을 아래와 같이 생성
  ~~~yml
  version: 0.0  ## CodeDeploy 버전. 프로젝트 버전이 아니기 때문에 0.0 외에 다른 버전을 사용하면 오류 발생
  os: linux
  files:
    - source:  /  ## S3 버킷에서 복사할 파일의 위치를 나타냄
      destination: /home/ec2-user/app/travis/build/  ## zip파일을 복사해 압축을 풀 위치를 지정
  ~~~
- Travis CI가 CodeDeploy도 실행시키도록 `.travis.yml` 파일에 설정 추가한 최종 파일
  ~~~yml
  language: java
  jdk:
    - openjdk8

  branches:
    only:
      - develop ## 오직 develop 브랜치에 push 될 때만 수행


  # Travis CI 서버의 Home
  cache: ## Gradle을 통해 의존성을 받게 되면 이를 해당 디렉토리에 캐시하여, 같은 의존성은 다음 배포때부터 다시 받지 않도록 설정
    directories:
      - '$HOME/.m2/repository'
      - '$HOME/.gradle'

  script: "./gradlew clean build" ## develop 브랜치에 Push 되었을때 수행하는 명령어. 프로젝트 내부에 둔 gradlew를 통해 clean & build 수행


  # Travis CI & S3 연동
  before_deploy:  ## 매번 Travis CI에서 파일을 하나하나 복사하면 시간이 많이 걸리므로 프로젝트 폴더 채로 압축해서 S3에 전달하도록 설정
    - zip -r harusketch * ## 현재 위치의 모든 파일을 `harusketch` 이름으로 압축
    - mkdir -p deploy ## deploy 라는 디렉토리를 Travis CI가 실행중인 위치에서 생성
    - mv harusketch.zip deploy/harusketch-zip

  deploy:
    - provider: s3
      access_key_id: $AWS_ACCESS_KEY   ## Travis repo settings에 설정된 값
      secret_access_key: $AWS_SECRET_KEY   ## Travis repo settings에 설정된 값
      bucket: harusketch-deploy  ## S3 버킷
      region: ap-northeast-2
      skip_cleanup: true
      acl: public_read
      local_dir: deploy  ## before_deploy에서 생성한 디렉토리
      wait-until-deployed: true
      on:
        repo: Integerous/Restful-WebApp
        branch: develop

    - provider: codedeploy
      access_key_id: $AWS_ACCESS_KEY   ## Travis repo settings에 설정된 값
      secret_access_key: $AWS_SECRET_KEY   ## Travis repo settings에 설정된 값
      bucket: harusketch-deploy  ## S3 버킷
      key: harusketch.zip  ## S3 버킷에 저장된 harusketch.zip 파일을 EC2로 배포
      bundle_type: zip
      application: harusketch  ## AWS 콘솔로 등록한 CodeDeploy 어플리케이션
      deployment_group: harusketch-group  ## AWS 콘솔로 등록한 CodeDeploy 배포 그룹
      region: ap-northeast-2
      wait-until-deployed: true
      on:
        repo: Integerous/Restful-WebApp
        branch: develop

  # CI 실행 완료시 메일로 알람
  notifications:
    email:
      recipients:
        - ryanhan@cloudcash.kr
  ~~~
#### 4.7.4. CodeDeploy로 스크립트 실행
- application.jar 파일을 실행시키는 것까지가 배포이므로
- EC2에 AWS CodeDeploy로 받은 파일을 실행시키는 배포 스크립트 생성
  - jar 파일들을 모아둘 디렉토리 생성 `$ mkdir /home/ec2-user/app/travis/jar`
  - jar 디렉토리에 옮겨진 application.jar를 실행시킬 `deploy.sh` 파일 생성
    - `nano /home/ec2-user/app/travis/deploy.sh`
    ~~~sh
    #!/bin/bash

    REPOSITORY=/home/ec2-user/app/travis

    echo "> 현재 구동중인 어플리케이션 PID 확인"
    CURRENT_PID=$(pgrep -f harusketch)
    echo "$CURRENT_PID"

    if [ -z $CURRENT_PID ]; then
        echo "> 현재 구동중인 어플리케이션이 없으므로 종료하지 않습니다."
    else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 3
    fi

    echo "> 새로운 어플리케이션 배포!"
    echo "> Build 파일 복사"
    cp $REPOSITORY/build/build/libs/*.jar $REPOSITORY/jar/

    JAR_NAME=$(ls -tr $REPOSITORY/jar/ |grep 'harusketch' | tail -n 1)

    echo "> 새로 배포된 JAR 이름은?? ===> $JAR_NAME"

    nohup java -jar $REPOSITORY/jar/$JAR_NAME &
    ~~~
#### 4.7.5. AWS CodeDeploy가 배포를 마치면 `deploy.sh`를 실행하도록 `appspec.yml` 설정 변경
- `appspec.yml` 전체 코드
  ~~~yml
  version: 0.0  ## CodeDeploy 버전. 프로젝트 버전이 아니기 때문에 0.0 외에 다른 버전을 사용하면 오류 발생
  os: linux
  files:
    - source:  /  ## S3 버킷에서 복사할 파일의 위치를 나타냄
      destination: /home/ec2-user/app/travis/build/  ## zip파일을 복사해 압축을 풀 위치를 지정


  hooks:
    AfterInstall:  ## 배포가 끝나면 아래 명령어를 실행
      - location: execute-deploy.sh  
        timeout: 180
        ## CodeDeploy에서 바로 deploy.sh 를 실행시킬 수 없으므로,
        ## deploy.sh 를 실행하는 execute-deploy.sh 파일을 실행하여 우회한다.
  ~~~
- CodeDeploy에서 바로 deploy.sh 를 실생시킬 수 없으므로, deploy.sh 를 실행하는 execute-deploy.sh 파일을 실행하여 우회한다.
#### 4.7.6. CodeDeploy가 실행할 수 있도록 `execute-deploy.sh` 파일을 프로젝트 내부에 생성
- `execute-deploy.sh`
  ~~~sh
  #!/bin/bash
  /home/ec2-user/app/travis/deploy.sh > /dev/null 2> /dev/null < /dev/null & 
  ~~~

>현재까지의 상황  
- 테스트, 빌드, 배포 모두 자동화
- 작업이 끝난 내용을 `develop` 브랜치에 push하면 자동으로 EC2에 배포
- 문제점
  - 배포하는 동안 스프링부트 프로젝트는 종료상태가 되어 서비스를 이용할 수 없다는 점.
  - Nginx를 사용해서 무중단 배포해야 함

## 5. 무중단 배포 구축 (Nginx)
### 5.1. Nginx 설치
- EC2에 접속해서 Nginx 설치 `$ sudo yum install nginx`
- Nginx 실행 `$ sudo service nginx start`
- Nginx 실행 확인 1 `$ ps -ef | grep nginx`
- Nginx 실행 확인 2 `EC2 인스턴스의 퍼블릭 DNS 주소로 브라우져 접속하여 Welcome to nginx 화면 확인`
### 5.2. 리버스 프록시 설정
- Nginx가 현재 실행중인 프로젝트를 연결하도록 설정
- Nginx 설정 파일 편집
  - `sudo nano /etc/nginx/nginx.conf`
  - 파일 내용 중 `location / { 이 위치에 }`
  - ~~~
    proxy_pass http://localhost:8080; //요청이 오면 http://localhost:8080 으로 전달
    # 아래 설정들은 실제 요청 데이터를 header의 각 항목에 할당
    proxy_set_header X-Real-IP $remote_addr;  //Request Header의 X-Real-IP에 요청자의 IP를 저장
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $http_host;
    ~~~
- Nginx 재시작 `$ sudo service nginx restart`
- 위의 `Nginx 실행확인 2` 로 Nginx가 프로젝트를 프록시하는 것 확인
### 5.3. 실행중인 프로젝트의 Profile 확인하는 API 작성
- 프로젝트의 환경설정 값을 다루는 `Environment`Bean을 DI받아 현재 활성화된 Profile을 반환하는 코드 작성
~~~java
@RestController
@AllArgsConstructor
public class WebRestController  {
  
  private PostsService postsService;
  private Environment env;
  ...
  
  @GetMapping("/profile")
  public String getProfile()  {
    return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
  }
}
~~~
- `WebRestControllerTest` 생성하여 테스트
~~~java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WebRestControllerTest  {
  
  @Autowired
  private TestRestTemplate restTemplate;
  
  @Test
  public void Profile확인() {
    //when
    String profile = this.restTemplate.getForObject("/profile", String.class);
    
    //then
    asserThat(profile).isEqualTo("local");
  }
}
~~~
- 테스트 내용은 `/profile`로 요청하면 현재 활성화된 Profile값 (local)이 반환되는지 비교
### 5.4. 운영 환경의 yml 파일 생성
- 운영 환경의 yml 파일은 프로젝트 외부에 생성 (내부에 생성해서 git으로 올리면 운영환경의 중요 정보가 같이 올라감)
  - `/app/config/haru-sketch/real-application.yml`에 생성
  - ~~~yml
    ---
    spring:
      profiles: set1
    server:
      port: 8081
    
    ---
    spring:
      profiles: set2
    server:
      port: 8082
    ~~~
- 운영 환경의 yml 파일을 프로젝트가 호출할 수 있도록 `Application.java`의 코드 변경
- 원래 코드
~~~java
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("====== SERVER STARTED ======");
	}
}
~~~
- 변경 후 코드
~~~java
@EnableJpaAuditing //JPA Auditing 활성화
@SpringBootApplication
public class Application  {

  public static final String APPLICATION_LOCATIONS = 
    "spring.config.location=classpath:application.yml,"
    + "/app/config/haru-sketch/real-application.yml";

  public static void main(String[] args)  {
    new SpringApplicationBuilder(Application.class)
              .properties(APPLICATION_LOCATIONS)
              .run(args);
  }
}
~~~
- 스프링부트 프로젝트가 실행될 때, 프로젝트 내부에 있는 `application.yml`과 외부에 위치한 `/app/config/haru-sketch/real-application`을 모두 불러오는지 확인해보기
  - Eclipse의 경우 `Application.java` 파일 우클릭 -> Debug As -> Debug Configuration 로 이동
  - Spring Boot App 하위의 프로젝트명 - Application 을 복사
  - Name을 `harusketch - [set1] Application`으로 수정하고 Profile에 `set1` 입력
  - [set1] Application을 실행하고 localhost:8081/profile 로 접속했을때 set1 뜨면 정상
- EC2에도 로컬과 같이 `/app/config/haru-sketch/real-application.yml` 생성하여 설정값 등록
  - 인스턴스의 `퍼블릭DNS/profile`로 접속하여 기본값인 `local` 출력되는지 확인

### 5.5. 배포 스크립트 작성
#### 5.5.1. 무중단 배포와 관련된 파일을 관리할 디렉토리와 스크립트 파일 생성
- 지금까지 `git`, `travis` 디렉토리를 생성했고, 3번째로 `nonstop` 디렉토리 생성
  - EC2 접속 후 `$ mkdir ~/app/nonstop` 
#### 5.5.2. 배포 스크립트 테스트
- 기존의 스프링프로젝트.jar 복사
- ```
  $ mkdir ~/app/nonstop/haru-sketch
  $ mkdir ~/app/nonstop/haru-sketch/build
  $ mkdir ~/app/nonstop/haru-sketch/build/libs
  $ cp ~/app/travis/build/build/libs/*.jar ~/app/nonstop/haru-sketch/build/lib
  ```
- jar 파일을 모아둘 디렉토리 생성 `$ mkdir ~/app/nonstop/jar`
- 스크립트 파일 생성 `$ nano ~/app/nonstop/deploy.sh`
~~~sh
#! /bin/bash

# 1. 스크립트에 필요한 변수값 할당
BASE_PATH=/home/ec2-user/app/nonstop
BUILD_PATH=$(ls $BASE_PATH/haru-sketch/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_PATH)
echo ">> bulid 파일명: $JAR_NAME"

# 2. 빌드된 Jar 파일을 jar 디렉토리로 복사
echo ">> bulid 파일 복사"
DEPLOY_PATH=$BASE_PATH/jar/
cp $BUILD_PATH $DEPLOY_PATH

# 3. 현재 구동중인 set 확인
echo ">> 현재 구동중인 Set 확인"
CURRENT_PROFILE=$(curl -s http://localhost/profile) # curl에서 -s 옵션은 상태진행바를 노출시키지 않는 옵션
echo ">> $CURRENT_PROFILE"


# 4. NginX에 연결되어 있지 않은 Profile 찾기
# 쉬고있는 set 찾기 (set1이 사용중이면 set2가 쉬는 중이므로 IDLE에 할당, vice versa)
if [ $CURRENT_PROFILE == set1 ]
then
  IDLE_PROFILE=set2
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == set2 ]
then
  IDLE_PROFILE=set1
  IDLE_PORT=8081
else
  echo ">> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo ">> set1을 할당합니다. IDLE_PROFILE: set1"
  IDLE_PROFILE=set1
  IDLE_PORT=8081
fi


# 5. 미연결된 Jar로 신규 Jar 심볼릭 링크 (ln)
echo ">> application.jar 교체"
IDLE_APPLICATION=$IDLE_PROFILE-haru-sketch.jar
IDLE_APPLICATION_PATH=$DEPLOY_PATH$IDLE_APPLICATION

ln -Tfs $DEPLOY_PATH$JAR_NAME $IDLE_APPLICATION_PATH
# -T : treat LINK_NAME as a normal file always
# -f : remove existing destination files
# -s : make symbolic links relative to link location


# 6. Nginx와 연결되지 않은 Profile을 종료
echo ">> $IDLE_PROFILE 에서 구동중인 어플리케이션 PID 확인"
IDLE_PID=$(pgrep -f $IDLE_APPLICATION)

if [ -z $IDLE_PID ]
then
  echo ">> 현재 구동중인 어플리케이션이 없으므로 종료하지 않습니다."
else
  echo ">> kill -15 $IDLE_PID"
  kill -15 $IDLE_PID
  sleep 5
fi


# 7.  6의 Profile로 Jar 실행
echo ">> $IDLE_PROFILE 배포"
nohup java -jar -Dspring.profiles.active=$IDLE_PROFILE $IDLE_APPLICATION_PATH &


echo ">> $IDLE_PROFILE 10초 후 Health Check 시작!"
echo ">> curl -s http://localhost:$IDLE_PORT/health "
sleep 10


# 8. 아래 코드를 10회 반복 수행
for retry_count in {1..10}
do
  # 9. /health 요청 결과 저장
  response=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
    ## /health의 결과는 {"status":"UP"}과 같이 나옴. spring-boot-starter-actuator 의존성 덕분.
    ## actuator는 스프링부트 프로젝트의 여러 상태를 확인해줄 수 있는 의존성
  up_count=$(echo $response | grep 'UP' | wc -l)
    ## response 결과에 "UP"이 있는지 확인
    ## echo $response | grep 'UP' 을 하면 UP이 포함된 문자열을 필터링 해줌
    ## | wc -l 로 필터링된 문자열의 갯수가 몇개인지 확인.(UP이 있다면 1개 이상)

  # 10. UP 문자열이 있는지 확인해서 있다면 for문 종료, 없다면 메세지 출력 후 아래 코드 실행
  if [ $up_count -ge 1 ]
  then # $up_count >= 1 ("UP" 문자열이 있는지 검증)
      echo ">> Health Check 성공"
      break
  else
      echo ">> Health Check의 응답을 알 수 없거나 혹은 status가 UP이 아닙니다."
      echo ">> Health Check : ${response}"
  fi


  # 11. 10회 다 실행될 동안 안되면 스크립트 종료
  if [ $retry_count -eq 10 ]
  then
      echo ">> Health Check 실패."
      echo ">> Nginx에 연결하지 않고 배포를 종료합니다."
      exit 1
  fi


  echo ">> Health Check 연결 실패. 재시도..."
  sleep 10
done
~~~
- 스크립트 실행 `$ ~/app/nonstop/deploy.sh`
- set1을 profile로 가진 프로젝트 실행 확인 `$ ps ef|grep java`

### 5.6. Nginx 동적 프록시 설정
>배포가 완료되어 어플리케이션이 실행되면, Nginx가 기존에 바라보던 Profile의 반대편을 바라보도록 변경하는 과정 필요

#### 5.6.1. Nginx 설정 변경
`$ cd /etc/nginx` 이 경로에 Nginx 설정에 관련된 모든 정보가 담겨있다.  
우선 Nginx가 동적으로 `Proxy Pass`를 변경할 수 있도록 설정 수정

~~~
$ sudo nano /etc/nginx/nginx.conf
~~~

아래와 같이 수정
~~~vim
#Load configuration files for the default server block
include /etc/nginx/default.d/*.conf;

include /etc/nginx/conf.d/service-url.inc; //새로 추가

location / {
	proxy_pass $srvice_url; // 수정
	...
~~~
- `include /etc/nginx/conf.d/service-url.inc;`
  - Java의 import와 같이 service-url.inc 파일을 포함
  - nginx.conf에서 service-url.inc에 있는 변수들을 그대로 사용 가능
- `proxy_pass $service_url;`
  - service-url.inc에 있는 `service_url` 변수를 호출

#### 5.6.2. srevice-url 파일 생성
`$ sudo nano /etc/nginx/conf.d/service-url.inc`으로 파일 생성하고, 아래 내용 입력

~~~
set $service_url http://127.0.0.1:8001;
~~~

- 저장 후, 변경내용 반영을 위해 nginx 재시작. `$ sudo service nginx restart`  
- `curl`로 테스트해보면 `$ curl -s localhost/profile`  
- 아래처럼 Proxy가 set1로 가는 것 확인
  ~~~
  set1[ec2-user@ip-~~~]$
  ~~~
  
#### 5.6.3. Nginx 스크립트 작성
동적 프록시 환경이 구축된 Nginx가  
배포 시점에 바라보는 Profile을 자동으로 변경하도록 스위치 스크립트 생성
