<div align="right">

~~EN~~ | **KO** |  ~~JP~~

</div>

<pre>
      
█▀█ █░█ ▀█▀ █ █▄░█ █▀▀ █▀ ▄▄ █▀█ █▀▀ █▀▀ █▀█ █▀▄▀█ █▀▄▀█ █▀▀ █▄░█ █▀▄ █▀▀ █▀█ 
█▀▄ ▀▀█ ░█░ █ █░▀█ █▄█ ▄█ ░░ █▀▄ ██▄ █▄▄ █▄█ █░▀░█ █░▀░█ ██▄ █░▀█ █▄▀ ██▄ █▀▄
  
</pre>

## TOC

- [개요](#개요)
- [시작하기(Getting Started)](#시작하기getting-started)
  - [전제조건(Prerequisites)](#전제조건prerequisites)
  - [프로젝트 구성하기(Project settings)](#프로젝트-구성하기project-settings)
- [평점 정규화](#평점-정규화)
  - [평균 중심 정규화](#평균-중심-정규화)
  - [Z점수 정규화](#z점수-정규화)
  - [최소-최대 정규화](#최소-최대-정규화)
  - [소수 자릿수 정규화](#소수-자릿수-정규화)
  - [이진 임계 이진화](#이진-임계-이진화)
- [유사도 계산](#유사도-계산)
  - [코사인 유사도](#코사인-유사도)
  - [피어슨 상관계수와 유사도](#피어슨-상관계수와-유사도)
  - [유클리드 거리와 유사도](#유클리드-거리와-유사도)
  - [이진 속성과 유사도](#이진-속성과-유사도)
- [이웃 기반 협업 필터링 추천](#이웃-기반-협업-필터링-추천)
- [특잇값 분해 기반 협업 필터링 추천](#특잇값-분해-기반-협업-필터링-추천)
- [TF-IDF 콘텐츠 기반 필터링 추천](#tf-idf-기반-콘텐츠-기반-필터링-추천)
- [연관규칙 기반 추천](#연관규칙-기반-추천)
- [피드백과 기여](#피드백과-기여)
- [라이선스](#라이선스)

## 개요

추천 시스템은 많은 양의 정보 안에서 사용자가 적합한 정보를 선택할 수 있도록 도와주는 시스템으로, GroupLens Research의 [LensKit](https://lenskit.org/), 아파치 소프트웨어 재단의 [Apache Mahout](https://mahout.apache.org/)과 [Apache PredictionIO](https://predictionio.apache.org/) 등, 다양한 형태의 추천 컴포넌트나 시스템들이 오픈 소스로도 제공되고 있으나, 추천 모델이 기본 수식만 구현되어 있거나, 블랙박스(black-box)로 제공되는 등 학술 연구나 상용화 목적의 개념 증명(PoC, Proof of Concept)을 위한 프로토타입 설계 및 구현 단계에서, 적용 영역에 따라 수식과 데이터의 내부 흐름을 미세 조정하고 유연하게 대응하기가 쉽지 않습니다. 또한, 웹 기반의 Notebook을 제공하는 Apache Zeppelin이나 Jupyter Notebook, 또는 Rmarkdown으로 추천 시스템을 구현해볼 수 있으나, 이는 분석가의 업무 흐름에 따라 하나의 Notebook에서 데이터와 처리를 표현하게 하는 목적으로 실제로 독립 시스템으로 구현하기에는 고려할 사항이 적지 않습니다. 

이러한 이유로 “R4tings Recommender 오픈 소스 추천 엔진”은 추천을 위한 통계나 기계 학습 기법들은 수정 없이 재사용 가능한 고차 함수로 제공하고, 수정되거나 새로운 기법을 적용하여 만들어진 고차 함수는 기존 고차 함수와 조합하거나, 컴포넌트로 제공되는 파이프라인을 통하여 다양한 도메인에 적용할 수 있도록, 추천하는 과정들을 단계별로 분해하여 하나의 파이프라인으로 연결하여 병렬 처리 할 수 있게 하는 것을 목표로 합니다.  

“R4tings Recommender 오픈 소스 추천 엔진”은 전통적인 통계나 기계 학습 기반의 추천 모델들의 기본 구현체인 “[R4tings Recommender](https://github.com/r4tings/recommender/tree/main/recommender)”와 실행 예제들인 “[R4tings Recommender Examples](https://github.com/r4tings/recommender/tree/main/recommender-examples)"의 두 개의 프로젝트를 포함하고 있어 추천 처리 과정을 단계별로 분해하여 내부 흐름을 쉽게 이해하고, 추천을 위한 통계나 기계 학습 기법들을 손쉽게 수정하거나 확장 또는 재사용할 수 있습니다.

“**R4tings Recommender 오픈 소스 추천 엔진**”의 최종 목표는 1) 전통적인 통계나 기계 학습 기반 추천 모델들의 구현체 제공을 통한 추천 시스템의 학습과 이해, 2) 시뮬레이터나 프로토타이핑을 통한 학술 연구 목적에서의 이론 검증, 3) 상용 수준의 추천 시스템 구현을 용이하게 하는 것입니다.

## 시작하기(Getting Started)
<!--
참고: https://spring.io/guides/gs/gradle/
-->

### 전제조건(Prerequisites)

#### 필수(Mandatory)

|소프트웨어|버전|설명|
|------|---|---|
|JDK|11| <p>OpenJDK 또는 Oracle JDK <p>* OpenJDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/) 를 참고하세요 <p>* Oracle JDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/)를 참고하세요|

#### 선택(Optional)

|소프트웨어|버전|설명|
|------|---|---|
|Git|Latest| Git을 내려받고 구성하는 방법은 [링크](https://git-scm.com/downloads)를 참고하세요|
|Git Client|Latest| <p>GitHub Desktop 또는 Sourcetree <p>* GitHub Desktop을 내려받고 구성하는 방법은 [링크](https://docs.github.com/en/desktop/) 를 참고하세요 <p>* Sourcetree를 내려받고 구성하는 방법은 [링크](https://www.sourcetreeapp.com/)를 참고하세요|
|Gradle|Latest|Build Tool을 내려받고 구성하는 방법은 [링크](https://docs.gradle.org/current/userguide/what_is_gradle.html/)를 참고하세요|
|IntelliJ|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://www.jetbrains.com/idea/)를 참고하세요|
|R|Latest|R을 내려받고 구성하는 방법은 [링크](https://www.r-project.org/)를 참고하세요|
|RStudio Desktop|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://posit.co/products/open-source/rstudio/)를 참고하세요|

### 프로젝트 구성하기(Project settings)

> **Note**
> 필수 소프트웨어인 JDK 11의 설치와 구성이 사전에 완료되었다고 가정합니다.
> 
> 프로젝트 구성하기의 설명은 MS Windows 10 기준으로 작성되었습니다.
 
① Windows + R 단축키를 이용 해 실행 창을 열어 줍니다.

② powershell 이라고 타이핑 후 확인을 클릭합니다.

③ PowerShell을 실행한 뒤, 루트 경로로 이동하기 위해 "cd /"를 입력하여 실행합니다.

④ C:에 "mkdir r4tings"를 입력하여 실행하여 r4tings 폴더를 생성하고 생성된 폴더로 이동하기 위해 "cd r4tings"를 입력하여 실행합니다.

⑤ R4tings Recommender 리파지토리의 GitHub 소스 코드 보관 파일을 내려받기 위해 "Invoke-WebRequest https://github.com/r4tings/recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip"를 입력하여 실행합니다.

⑥ 내려받은 소스 코드 보관 파일의 압축 해제를 위해 "Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath ."를 입력하여 실행합니다.

⑦ 압축이 해제된 폴더의 이름을 변경하기 위해 "Rename-Item -Path recommender-main -NewName r4tings-recommender"를 입력하여 실행합니다.

⑧ "cd r4tings-recommender"를 입력하여 프로젝트 폴더로 이동하고 "ls"를 입력하고 실행하여 r4tings-recommender-master 폴더의 내용을 확인합니다.

⑨ 마지막으로 "./gradlew clean build -x test"를 입력하여 프로젝트를 빌드합니다.

```powershell

Windows PowerShell
Copyright (C) Microsoft Corporation. All rights reserved.

새로운 크로스 플랫폼 PowerShell 사용 https://aka.ms/pscore6

PS C:\Users\r4tings> cd /
PS C:\> mkdir r4tings


    디렉터리: C:\


Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d-----      2023-10-01  오전 11:38                r4tings

PS C:\> cd r4tings
PS C:\r4tings> Invoke-WebRequest https://github.com/r4tings/recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip
PS C:\r4tings> Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath .
PS C:\r4tings> Rename-Item -Path recommender-main -NewName r4tings-recommender
PS C:\r4tings> cd r4tings-recommender
PS C:\r4tings\r4tings-recommender> ls


    디렉터리: C:\r4tings\r4tings-recommender


Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d-----      2023-10-01  오전 11:40                dataset
d-----      2023-10-01  오전 11:40                gradle
d-----      2023-10-01  오전 11:40                lib
d-----      2023-10-01  오전 11:40                recommender
d-----      2023-10-01  오전 11:40                recommender-examples
-a----      2023-10-01   오후 6:58            151 .gitignore
-a----      2023-10-01   오후 6:58            275 .whitesource
-a----      2023-10-01   오후 6:58           3857 build.gradle
-a----      2023-10-01   오후 6:58            209 gradle.properties
-a----      2023-10-01   오후 6:58           8639 gradlew
-a----      2023-10-01   오후 6:58           2776 gradlew.bat
-a----      2023-10-01   오후 6:58          14480 README.md
-a----      2023-10-01   오후 6:58             87 settings.gradle

PS C:\r4tings\r4tings-recommender> ./gradlew clean build -x test
⋯ - 일부 생략 -
PS C:\r4tings\r4tings-recommender>
```

<br/>

**R4tings Recommender 오픈 소스 추천 엔진**의 디렉토리 구조는 다음과 같습니다

```
C:\r4tings
   ├── r4tings-recommender-main.zip                <- R4tings Recommender 소스 코드 보관 파일
   └── r4tings-recommender
       ├── dataset                                 <- 예제 데이터셋 
       │   └── r4tings                             <- r4tings 데이터셋
       ├── gradle                                  
       │   └── wrapper                             <- Gradle Wrapper
       ├── lib                                     
       │   └── hadoop-2.8.3                        <- Microsoft Windows용 Hadoop 바이너리
       ├── recommender                             <- 추천 모델들의 기본 구현체
       │   └── src
       ├── recommender-examples                    <- 구현체의 실행 예제 
       │   └── src
       ├── ⋯                                       <- 일부 생략  
       ├── build.gradle                            <- Gradle 구성 파일
       ├── gradle.properties                       <- Gradle 구성 파일
       ├── gradlew                                 <- Gradle Wrapper 구성 파일
       ├── gradlew.bat                             <- Gradle Wrapper 구성 파일
       └── settings.gradle                         <- Gradle 구성 파일
```
> **Warning**
> 
> 프로젝트 폴더 명에는 **-main**이 없습니다.
> 
> 프로젝트 폴더는 r4tings-recommender 입니다.
> 
> Microsoft Windows용 Hadoop 바이너리는 [링크](https://github.com/cdarlint/winutils/)를 참고하세요.
> 
> 리포지토리 뷰에서 소스 코드 보관 파일 다운로드하는 자세한 내용은 [링크](https://docs.github.com/ko/repositories/working-with-files/using-files/downloading-source-code-archives#downloading-source-code-archives-from-the-repository-view)를 참고하세요.

#### 공개 데이터셋 내려받기(Download Public Datasets)

<br/>

여기에서는 테스트 클래스인 [**DatasetLoadTest**](src/test/java/com/r4tings/recommender/examples/DatasetLoadTest.java) 클래스의 테스트 메서드인 downloadExtenalDatasets를 실행하여 외부 데이터셋을 내려받고 압축을 해제합니다. 

* [downloadPublicDatasets](./src/test/java/com/r4tings/recommender/examples/ch02/DatasetLoadTest.java#L39)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetLoadTest 클래스의 테스트 메서드인 downloadPublicDatasets 실행하고 실행 결과를 살펴봅니다.

```
./gradlew :test --tests com.r4tings.recommender.examples.DatasetLoadTest.downloadPublicDatasets
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

Gradle Wrapper로 DatasetLoadTest 클래스의 테스트 메서드인 downloadPublicDatasets 실행 후, R4tings Recommender 프로젝트의 dataset 디렉토리 구조는 다음과 같습니다

```
├── r4tings-recommender
    ├── dataset                                      <- 예제 데이터셋
    │   ├── Book-Crossing                            <- Book-Crossing 데이터셋
    │   │   ├── BX-CSV-Dump.zip                      <- Book-Crossing 데이터셋 압축 파일
    │   │   └── ⋯ - 일부 생략 -    
    │   └── MovieLens                                <- MovieLens 데이터셋
    │        ├── ml-coursera                          <- MovieLens Coursera 예제 데이터셋 
    │        ├── ml-latest                            <- MovieLens Latest 데이터셋 
    │        ├── ml-latest-samll                      <- MovieLens Latest(Small) 데이터셋   
    │        ├── ml-latest.zip                        <- MovieLens Latest 데이터셋 압축 파일
    │        └── ml-latest-samll.zip                  <- MovieLens Latest(Small) 데이터셋 압축 파일
    │
    │   ⋯ - 일부 생략 - 
```

<br/>
