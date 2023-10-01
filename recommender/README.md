<div align="right">

~~EN~~ | **KO** |  ~~JP~~

</div>

<pre>
      
█▀█ █░█ ▀█▀ █ █▄░█ █▀▀ █▀ ▄▄ █▀█ █▀▀ █▀▀ █▀█ █▀▄▀█ █▀▄▀█ █▀▀ █▄░█ █▀▄ █▀▀ █▀█ 
█▀▄ ▀▀█ ░█░ █ █░▀█ █▄█ ▄█ ░░ █▀▄ ██▄ █▄▄ █▄█ █░▀░█ █░▀░█ ██▄ █░▀█ █▄▀ ██▄ █▀▄
  
</pre>

<br/>

“**R4tings Recommender**”는 Java 언어와 [Apache Spark 라이브러리](https://spark.apache.org/) 기반으로 구현된 오픈 소스 추천 엔진입니다.

<br/>
 
## Index

<br/>

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

<br/>

## 개요

<br/>

추천 시스템은 많은 양의 정보 안에서 사용자가 적합한 정보를 선택할 수 있도록 도와주는 시스템으로, GroupLens Research의 [LensKit](https://lenskit.org/), 아파치 소프트웨어 재단의 [Apache Mahout](https://mahout.apache.org/)과 [Apache PredictionIO](https://predictionio.apache.org/) 등, 다양한 형태의 추천 컴포넌트나 시스템들이 오픈 소스로도 제공되고 있으나, 추천 모델이 기본 수식만 구현되어 있거나, 블랙박스(black-box)로 제공되는 등 학술 연구나 상용화 목적의 개념 증명(PoC, Proof of Concept)을 위한 프로토타입 설계 및 구현 단계에서, 적용 영역에 따라 수식과 데이터의 내부 흐름을 미세 조정하고 유연하게 대응하기가 쉽지 않습니다. 

또한, 웹 기반의 Notebook을 제공하는 Apache Zeppelin이나 Jupyter Notebook, 또는 Rmarkdown으로 추천 시스템을 구현해볼 수 있으나, 이는 분석가의 업무 흐름에 따라 하나의 Notebook에서 데이터와 처리를 표현하게 하는 목적으로 실제로 독립 시스템으로 구현하기에는 고려할 사항이 적지 않습니다. 

이러한 점에서 “R4tings Recommender 오픈 소스 추천 엔진 패키지”는 추천을 위한 통계나 기계 학습 기법들은 수정 없이 재사용 가능한 고차 함수로 제공하고, 수정되거나 새로운 기법을 적용하여 만들어진 고차 함수는 기존 고차 함수와 조합하거나, 컴포넌트로 제공되는 파이프라인을 통하여 다양한 도메인에 적용할 수 있도록, 추천하는 과정들을 단계별로 분해하여 하나의 파이프라인으로 연결하여 병렬 처리 할 수 있게 하는 것을 개발 목표로 둡니다. 

또한 전통적인 통계나 기계 학습 기반의 추천 모델들의 기본 구현체인 “**R4tings Recommender**”와 응용 예제들인 “[**R4tings Recommender Examples**](https://github.com/r4tings/r4tings-recommender-examples)”를 통해 추천 처리 과정을 단계별로 분해하여 내부 흐름을 쉽게 파악하고, 어느 도메인에서도 손쉽게 수정하거나 확장 또는 재사용할 수 있게 처리 과정을 하나의 파이프라인으로 병렬 처리 할 수 있습니다.

이를 통해 1) 전통적인 통계나 기계 학습 기반 추천 모델들의 구현체 제공을 통한 추천 시스템의 학습과 이해, 2) 시뮬레이터나 프로토타이핑을 통한 학술 연구 목적에서의 이론 검증, 3) 상용 수준의 추천 시스템 구현을 용이하게 하는 것이 “**R4tings Recommender 오픈 소스 추천 엔진 패키지**”의 최종 목적입니다.

<br/>

## 시작하기(Getting Started)

<br/>

<!--
참고: https://spring.io/guides/gs/gradle/
-->
<br/>

### 전제조건(Prerequisites)

<br/>

#### 필수(Mandatory)

<br/>

|소프트웨어|버전|설명|
|------|---|---|
|JDK|11| <p>OpenJDK 또는 Oracle JDK <p>* OpenJDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/) 를 참고하세요 <p>* Oracle JDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/)를 참고하세요|

<br/>

#### 선택(Optional)

<br/>

|소프트웨어|버전|설명|
|------|---|---|
|Git|Latest| Git을 내려받고 구성하는 방법은 [링크](https://git-scm.com/downloads)를 참고하세요|
|Git Client|Latest| <p>GitHub Desktop 또는 Sourcetree <p>* GitHub Desktop을 내려받고 구성하는 방법은 [링크](https://docs.github.com/en/desktop/) 를 참고하세요 <p>* Sourcetree를 내려받고 구성하는 방법은 [링크](https://www.sourcetreeapp.com/)를 참고하세요|
|Gradle|Latest|Build Tool을 내려받고 구성하는 방법은 [링크](https://docs.gradle.org/current/userguide/what_is_gradle.html/)를 참고하세요|
|IntelliJ|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://www.jetbrains.com/idea/)를 참고하세요|
|R|Latest|R을 내려받고 구성하는 방법은 [링크](https://www.r-project.org/)를 참고하세요|
|RStudio Desktop|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://posit.co/products/open-source/rstudio/)를 참고하세요|

<br/>

### 프로젝트 구성하기(Project settings)

<br/>

#### GitHub 소스 코드 아카이브 내려받기(Downloading GitHub source code archive)

<br/>

> **Note**
> 필수 소프트웨어인 JDK 11의 설치와 구성이 사전에 완료되었다고 가정합니다.
> 
> 프로젝트 구성하기의 설명은 MS Windows 10 기준으로 작성되었습니다.
 
① Windows + R 단축키를 이용 해 실행 창을 열어 줍니다.

② powershell 이라고 타이핑 후 확인을 클릭합니다.

③ PowerShell을 실행한 뒤, 루트 경로로 이동하기 위해 "cd /"를 입력하여 실행합니다.

④ C:에 "mkdir r4tings"를 입력하여 실행하여 r4tings 폴더를 생성하고 생성된 폴더로 이동하기 위해 "cd r4tings"를 입력하여 실행합니다.

⑤ R4tings Recommender 리파지토리의 GitHub 소스 코드 보관 파일을 내려받기 위해 "Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/master.zip utFile r4tings-recommender-master.zip"를 입력하여 실행합니다.

⑥ 내려받은 소스 코드 보관 파일의 압축 해제를 위해 "Expand-Archive -LiteralPath r4tings-recommender-master.zip -DestinationPath ."를 입력하여 실행합니다.

⑦ 압축이 해제된 폴더의 이름을 변경하기 위해 "Rename-Item -Path r4tings-recommender-master -NewName r4tings-recommender"를 입력하여 실행합니다.

⑧ 마지막으로 "cd r4tings-recommender"를 입력하여 프로젝트 폴더로 이동하고 "ls"를 입력하고 실행하여 r4tings-recommender-master 폴더의 내용을 확인합니다.

```powershell

Windows PowerShell
Copyright (C) Microsoft Corporation. All rights reserved.

새로운 크로스 플랫폼 PowerShell 사용 https://aka.ms/pscore6

PS C:\Users\r4tings> cd /
PS C:\> mkdir r4tings
                                                                                                                                                                                                                                                    디렉터리: C:\                                                                                                                                                                                                                                                                                                                                                       Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d-----      2023-09-03  오전 10:02                r4tings


PS C:\> cd r4tings
PS C:\r4tings> Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/master.zip -OutFile r4tings-recommender-master.zip
PS C:\r4tings> Expand-Archive -LiteralPath r4tings-recommender-master.zip -DestinationPath .
PS C:\r4tings> Rename-Item -Path r4tings-recommender-master -NewName r4tings-recommender
PS C:\r4tings> cd r4tings-recommender
PS C:\r4tings\r4tings-recommender> ls


    디렉터리: C:\r4tings\r4tings-recommender


Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d-----      2023-09-03  오전 10:02                dataset
d-----      2023-09-03  오전 10:02                gradle
d-----      2023-09-03  오전 10:02                hadoop-2.8.3
d-----      2023-09-03  오전 10:02                src
-a----      2023-09-02   오후 5:51             46 .gitattributes
-a----      2023-09-02   오후 5:51            247 .gitignore
-a----      2023-09-02   오후 5:51           7875 build.gradle.kts
-a----      2023-09-02   오후 5:51           2211 CONTRIBUTORS.md
-a----      2023-09-02   오후 5:51            268 gradle.properties
-a----      2023-09-02   오후 5:51           8497 gradlew
-a----      2023-09-02   오후 5:51           2776 gradlew.bat
-a----      2023-09-02   오후 5:51          11357 LICENSE
-a----      2023-09-02   오후 5:51          91914 README.md


PS C:\r4tings\r4tings-recommender> ./gradlew clean build -x test
⋯ - 일부 생략 -
PS C:\r4tings\r4tings-recommender> ./gradlew :test --tests com.r4tings.recommender.examples.DatasetLoadTest.bookCrossingDatasetExamples
⋯ - 일부 생략 -
```

<br/>

R4tings Recommender 프로젝트의 디렉토리 구조는 다음과 같습니다

```
├── r4tings-recommender-master.zip                   <- R4tings Recommender 소스 코드 보관 파일
│
├── r4tings-recommender                              <- R4tings Recommender 프로젝트
│   ├── dataset                                      <- 예제 데이터셋 TODO 삭제?
│   ├── gradle                                       <- Gradle Wrapper
│   ├── hadoop-2.8.3                                 <-  Microsoft Windows용 Hadoop 바이너리
│   ├── src                                          <- 소스
│   │   ⋯ - 일부 생략 - 
│   ├── build.gradle.kts                             <- Gradle 빌드 파일
│   ├── gradle.properties                            <- Gradle 설정 파일
│   ├── gradlew                                      <- Gradle Wrapper 스크립트
│   └── gradlew.bat                                  <- Gradle Wrapper 스크립트
```
> **Warning**
> 
> 프로젝트 디렉토리 명에는 **-master**가 없습니다.
> 
> 프로젝트 디렉토리는 r4tings-recommender 폴더입니다.
> 
> Microsoft Windows용 Hadoop 바이너리는 [링크](https://github.com/cdarlint/winutils/)를 참고하세요.
> 
> 리포지토리 뷰에서 소스 코드 보관 파일 다운로드하는 자세한 내용은 [링크](https://docs.github.com/ko/repositories/working-with-files/using-files/downloading-source-code-archives#downloading-source-code-archives-from-the-repository-view)를 참고하세요.

<br/>

#### Gradle로 프로젝트 빌드하기(Building Projects with Gradle)

<br/>

```
./gradlew clean build -x test
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

<br/>

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

#### Book-Crossing 데이터셋 Parquet 유형으로 변환하기

<br/>

CSV 파일 형식의 Book-Crossing 데이터셋을 로드하여 Parquet 형식으로 저장합니다.

> Apache Spark는 CSV와 Parquet 파일 형식 모두 지원하지만, 여기에서는 Raw 데이터를 전처리하기 위해 CSV 형식의 데이터셋 파일을 읽어들여 Parquet 형식으로 변환합니다.

예제 테스트 클래스인 DatasetLoadTest 클래스의 테스트 메서드인 bookCrossingDatasetExamples 실행 결과를 살펴봅니다.

* [bookCrossingDatasetExamples](./src/test/java/com/r4tings/recommender/examples/DatasetLoadTest.java#L61)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetLoadTest 클래스의 테스트 메서드인 bookCrossingDatasetExamples를 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.examples.DatasetLoadTest.bookCrossingDatasetExamples
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

<br/>

## 평점 정규화

<br/>

### 평균 중심 정규화

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/MeanCenteringNormalizer_Class_Diagram.svg)

- 평균 중심 정규화 구현체인 [**MeanCenteringNormalizer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/data/normalize/MeanCenteringNormalizer.java)  클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 평균 중심화된 평점 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. MeanCenteringNormalizer 클래스는 평균 중심 정규화를 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음의 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
MeanCenteringNormalizer normalizer =
                new MeanCenteringNormalizer()
                        .setGroup(Group.USER)
                        .setGroupCol("user")
                        .setVerbose(true)
                        .setOutputCol("rating")
                        .setUserCol("user")
                        .setItemCol("item")
                        .setRatingCol("rating");
```

MeanCenteringNormalizer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수  | 유형    | 필수여부 | 기본값    | 설명                                                                                             |
|-----------|---------|----------|-----------|--------------------------------------------------------------------------------------------------|
| group     | Enum    | Ｘ       | \-        | 평점 정규화 기준을 Enum 유형으로 설정(기본값/null: 전체, Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol  | String  | Ｘ       | \-        | 평점 정규화 기준을 문자열로 설정(기본값/null: 전체, user: 사용자, item:아이템).                  |
| verbose   | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                   |
| userCol   | String  | Ｘ       | user      | 평점 데이터의 사용자 칼럼명(기본값: user)                                                        |
| itemCol   | String  | Ｘ       | item      | 평점 데이터의 아이템 칼럼명(기본값: item)                                                        |
| ratingCol | String  | Ｘ       | rating    | 평점 데이터의 평점 칼럼명(기본값: rating)                                                        |
| outputCol | String  | Ｘ       | ratingCol | 출력 칼럼명(기본값: ratingCol)으로 정규화된 평점                                                 |

생성된 MeanCenteringNormalizer 인스턴스는 RatingNormalizer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 평점을 평균 중심화된 평점으로 변환할 수
있습니다.

```java
Dataset<Row> normalizedRatingDS = normalizer.transform(ratingDS);
```

<br/>

예제 테스트 클래스인 MeanCenteringNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**MeanCenteringNormalizerTest**](./src/test/java/com/r4tings/recommender/data/normalize/MeanCenteringNormalizerTest.java) 클래스는 MeanCenteringNormalizer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 MeanCenteringNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.data.normalize.MeanCenteringNormalizerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 전체 평균 중심화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = {r_{u,i}} - \mu $$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $\mu $은 평점 집합 $R$의 평균입니다.

(2) 사용자 평균 중심화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = {r_{u,i}} - {\mu _u}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu _u}$은 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점 평균입니다.

(3) 아이템 평균 중심화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = {r_{u,i}} - {\mu _i}$$

여기에서 ${r_{u,i}}$은 아이템 $i$를 평가한 사용자 $u$의 평점, ${\mu _i}$은 아이템 $i$를 평가한 사용자 집합 ${U_i}$의 평점 평균입니다.

<br/>

### Z점수 정규화

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/ZScoreNormalizer_Class_Diagram.svg)

- Z점수 정규화 구현체인[**ZScoreNormalizer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/data/normalize/ZScoreNormalizer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 Z점수화된 평점 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. ZScoreNormalizer 클래스는 Z점수 정규화를 위해 필요한 매개변수의 설정이나 기본값 변경이 필요할 때는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
ZScoreNormalizer normalizer =
                new ZScoreNormalizer()
                        .setGroup(Group.USER)
                        .setGroupCol("user")
                        .setVerbose(true)
                        .setOutputCol("rating")
                        .setUserCol("user")
                        .setItemCol("item")
                        .setRatingCol("rating");
```

ZScoreNormalizer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수  | 유형    | 필수여부 | 기본값    | 설명                                                                                              |
|-----------|---------|----------|-----------|---------------------------------------------------------------------------------------------------|
| group     | Enum    | Ｘ       | \-        | Z점수 정규화 기준을 Enum 유형으로 설정(기본값/null: 전체, Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol  | String  | Ｘ       | \-        | Z점수 정규화 기준을 문자열로 설정(기본값/null: 전체, user: 사용자, item:아이템).                  |
| verbose   | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                    |
| userCol   | String  | Ｘ       | user      | 평점 데이터의 사용자 칼럼명(기본값: user)                                                         |
| itemCol   | String  | Ｘ       | item      | 평점 데이터의 아이템 칼럼명(기본값: item)                                                         |
| ratingCol | String  | Ｘ       | rating    | 평점 데이터의 평점 칼럼명(기본값: rating)                                                         |
| outputCol | String  | Ｘ       | ratingCol | 출력 칼럼명(기본값: ratingCol)으로 정규화된 평점                                                  |

생성된 ZScoreNormalizer 인스턴스는 RatingNormalizer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 평점을 Z점수화된 평점으로 변환할 수 있습니다.

```java
Dataset<Row> normalizedRatingDS = normalizer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 ZScoreNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**ZScoreNormalizerTest**](./src/test/java/com/r4tings/recommender/data/normalize/ZScoreNormalizerTest.java) 클래스는 ZScoreNormalizer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 ZScoreNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.data.normalize.ZScoreNormalizerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 전체 Z점수화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{{r_{u,i}} - \mu }}{\sigma }$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $\mu $와 $\sigma $는 평점 집합 $R$의 평균과 표준편차입니다.

(2) 사용자 Z점수화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{{r_{u,i}} - {\mu _u}}}{{{\sigma _u}}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu _u}$와 ${\sigma _u}$는 아이템 집합 ${I_u}$의 평점 평균과 표준편차입니다.

(3) 아이템 Z점수화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{{r_{u,i}} - {\mu _i}}}{{{\sigma _i}}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu _i}$와 ${\sigma _i}$는 아이템 집합 ${U_i}$의 평점 평균과 표준편차입니다.

<br/>

### 최소-최대 정규화

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/MinMaxNormalizer_Class_Diagram.svg)

- 최소-최대 정규화 구현체인 [**MinMaxNormalizer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/data/normalize/MinMaxNormalizer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 최소-최대화된 평점 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. MinMaxNormalizer 클래스는 최소-최대 정규화를 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
MinMaxNormalizer normalizer =
                new MinMaxNormalizer()
                        .setGroup(Group.USER)
                        .setGroupCol("user")
                        .setLower(1d)
                        .setUpper(5d)
                        .setVerbose(true)
                        .setOutputCol("rating")
                        .setUserCol("user")
                        .setItemCol("item")
                        .setRatingCol("rating");
```

MinMaxNormalizer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수  | 유형    | 필수여부 | 기본값    | 설명                                                                                                  |
|-----------|---------|----------|-----------|-------------------------------------------------------------------------------------------------------|
| group     | Enum    | Ｘ       | \-        | 최소-최대 정규화 기준을 Enum 유형으로 설정(기본값/null: 전체, Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol  | String  | Ｘ       | \-        | 최소-최대 정규화 기준을 문자열로 설정(기본값/null: 전체, user: 사용자, item:아이템).                  |
| lower     | Double  | Ｘ       | 0d        | 새로운 평점 최솟값                                                                                    |
| upper     | Double  | Ｘ       | 1d        | 새로운 평점 최댓값                                                                                    |
| verbose   | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                        |
| userCol   | String  | Ｘ       | user      | 평점 데이터의 사용자 칼럼명(기본값: user)                                                             |
| itemCol   | String  | Ｘ       | item      | 평점 데이터의 아이템 칼럼명(기본값: item)                                                             |
| ratingCol | String  | Ｘ       | rating    | 평점 데이터의 평점 칼럼명(기본값: rating)                                                             |
| outputCol | String  | Ｘ       | ratingCol | 출력 칼럼명(기본값: ratingCol)으로 정규화된 평점                                                      |

생성된 MinMaxNormalizer 인스턴스는 RatingNormalizer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 평점을 최소-최대화된 평점으로 변환할 수 있습니다.

```
Dataset<Row> normalizedRatingDS = normalizer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 MinMaxNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**MinMaxNormalizerTest**](./src/test/java/com/r4tings/recommender/data/normalize/MinMaxNormalizerTest.java) 클래스는 MinMaxNormalizer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 MinMaxNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.data.normalize.MinMaxNormalizerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 전체 최소-최대화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{{r_{u,i}} - {r_{\min }}}}{{{r_{\max }} - {r_{\min }}}}({r_{new\_\max }} - {r_{new\_\min }}) + {r_{new\_\min }}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${r_{\min }}$과 ${r_{\max }}$는 평점 집합 $R$의 최솟값과 최댓값, ${r_{new\_\min }}$ 과 ${r_{new\_\max }}$는 새로운 평점 집합 $R'$의 최솟값과 최댓값입니다.

(2) 사용자 최소-최대화는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/minMaxExamples01.svg">

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${r_u}{{\min}}$과 ${r_u}{{\max}}$는 아이템 집합 ${I_u}$의 평점 최솟값과 최댓값, ${r_{new\_\min }}$과 ${r_{new\_\max }}$는 새로운 평점 최솟값과 최댓값입니다.

(3) 아이템 최소-최대화는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/minMaxExamples02.svg">

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${r_i}{{\min}}$과 ${r_i}{{\max}}$는 사용자 집합 ${U_i}$의 평점 최솟값과 최댓값, ${r_{new\_\min }}$과 ${r_{new\_\max }}$는 새로운 평점 최솟값과 최댓값입니다.

<br/>

### 소수 자릿수 정규화 

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/DecimalScaling_Class_Diagram.svg)

- 소수 자릿수 정규화 구현체인 [**DecimalScalingNormalizer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/data/normalize/DecimalScalingNormalizer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 소수 자릿수화된 평점 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. DecimalScalingNormalizer 클래스는 소수 자릿수 정규화를 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
DecimalScalingNormalizer normalizer =
                new DecimalScalingNormalizer()
                        .setGroup(Group.USER)
                        .setGroupCol("user")
                        .setVerbose(true)
                        .setOutputCol("rating")
                        .setUserCol("user")
                        .setItemCol("item")
                        .setRatingCol("rating");
```

DecimalScalingNormalizer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수  | 유형    | 필수여부 | 기본값    | 설명                                                                                                    |
|-----------|---------|----------|-----------|---------------------------------------------------------------------------------------------------------|
| group     | Enum    | Ｘ       | \-        | 소수 자릿수 정규화 기준을 Enum 유형으로 설정(기본값/null: 전체, Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol  | String  | Ｘ       | \-        | 소수 자릿수 정규화 기준을 문자열로 설정(기본값/null: 전체, user: 사용자, item:아이템).                  |
| verbose   | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                          |
| userCol   | String  | Ｘ       | user      | 평점 데이터의 사용자 칼럼명(기본값: user)                                                               |
| itemCol   | String  | Ｘ       | item      | 평점 데이터의 아이템 칼럼명(기본값: item)                                                               |
| ratingCol | String  | Ｘ       | rating    | 평점 데이터의 평점 칼럼명(기본값: rating)                                                               |
| outputCol | String  | Ｘ       | ratingCol | 출력 칼럼명(기본값: ratingCol)으로 정규화된 평점                                                        |

생성된 DecimalScalingNormalizer 인스턴스는 RatingNormalizer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 평점을 소수 자릿수화된 평점으로 변환할 수 있습니다.

```java
Dataset<Row> normalizedRatingDS = normalizer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 DecimalScalingNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**DecimalScalingNormalizerTest**](./src/test/java/com/r4tings/recommender/data/normalize/DecimalScalingNormalizerTest.java) 클래스는 DecimalScalingNormalizer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DecimalScalingNormalizerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.data.normalize.DecimalScalingNormalizerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

소수 자릿수 정규화는 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{{r_{u,i}}}}{{{{10}^j}}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $j$는 새로운 평점 집합 $R'$에서 $\max \left| {R'} \right| < 1$ 이 성립되는 가장 가까운 정수입니다.

<br/>

### 이진 임계 이진화 

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/ThresholdBinarizer_Class_Diagram.svg)

-  이진 임계 이진화 구현체인 [**ThresholdBinarizer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/data/normalize/ThresholdBinarizer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 이진 임계화된 평점 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. ThresholdBinarizer 클래스는 이진 임계 이진화를 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
ThresholdBinarizer binarizer =
                new ThresholdBinarizer()
                        .setGroup(Group.USER)
                        .setGroupCol("user")
                        .setThreshold(3)
                        .setVerbose(true)
                        .setOutputCol("rating")
                        .setUserCol("user")
                        .setItemCol("item")
                        .setRatingCol("rating");
```

ThresholdBinarizer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수  | 유형    | 필수여부 | 기본값    | 설명                                                                                                  |
|-----------|---------|----------|-----------|-------------------------------------------------------------------------------------------------------|
| group     | Enum    | Ｘ       | \-        | 이진 임계 이진화 기준을 Enum 유형으로 설정(기본값/null: 전체, Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol  | String  | Ｘ       | \-        | 이진 임계 이진화 기준을 문자열로 설정(기본값/null: 전체, user: 사용자, item:아이템).                  |
| threshold | Double  | O        | 없음      | 임곗값                                                                                                |
| verbose   | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                        |
| userCol   | String  | Ｘ       | user      | 평점 데이터의 사용자 칼럼명(기본값: user)                                                             |
| itemCol   | String  | Ｘ       | item      | 평점 데이터의 아이템 칼럼명(기본값: item)                                                             |
| ratingCol | String  | Ｘ       | rating    | 평점 데이터의 평점 칼럼명(기본값: rating)                                                             |
| outputCol | String  | Ｘ       | ratingCol | 출력 칼럼명(기본값: ratingCol)으로 이진화된 평점                                                      |

생성된 ThresholdBinarizer 인스턴스는 RatingNormalizer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 평점을 이진 임계화된 평점으로 변환할 수 있습니다.

```java
Dataset<Row> binarizedRatingDS = binarizer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 ThresholdBinarizerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**ThresholdBinarizerTest**](./src/test/java/com/r4tings/recommender/data/normalize/ThresholdBinarizerTest.java) 클래스는 ThresholdBinarizer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 ThresholdBinarizerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.data.normalize.ThresholdBinarizerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

이진 임계 이진화는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch03/binaryThresholdingExamples01.svg">

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $\gamma $는 임곗값입니다.

<br/>

## 유사도 계산

<br/>

### 코사인 유사도

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/CosineSimilarity_Class_Diagram.svg)

- 코사인 유사도 구현체인 [**CosineSimilarityMeasurer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/measures/similarity/CosineSimilarityMeasurer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 유사도 데이터로 변환하는 transform 메서드를 구현한 클래스입니다.

CosineSimilarityMeasurer 클래스는 유사도 계산을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다

```java
CosineSimilarityMeasurer measurer =
    new CosineSimilarityMeasurer()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setImputeZero(false)
        .setIds("u4", "u5")
        .setVerbose(true)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("similarity");  
```

CosineSimilarityMeasurer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수   | 유형    | 필수여부 | 기본값       | 설명                                                                                  |
|------------|---------|----------|--------------|---------------------------------------------------------------------------------------|
| group      | Enum    | Ｘ       | Group.USER   | 유사도 계산 기준을 Group 열거형 유형으로 설정(Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol   | String  | Ｘ       | criterion    | 유사도 계산 기준을 문자열로 설정(“user”: 사용자, “item”:아이템).                      |
| imputeZero |         | Ｘ       |              | 쌍대 열의 어느 한쪽 값이 결측인 경우의 결측치 처리(true: 0 대체, false: 쌍대 열 제외) |
| ids        |         | Ｘ       | \-           | 유사도 계산 대상 ID(LHS) 지정(미지정 시 전체 대상)                                    |
| verbose    | boolean | Ｘ       | false        | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                        |
| userCol    | String  | Ｘ       | user         | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                           |
| itemCol    | String  | Ｘ       | item         | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                           |
| ratingCol  | String  | Ｘ       | rating       | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                           |
| outputCol  | String  | Ｘ       | “similarity” | 출력 컬럼명(기본값: “similarity”)으로 정규화된 평점                                   |

생성된 CosineSimilarityMeasurer 인스턴스는 RatingSimilarityMeasurer 클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 유사도를 계산 할 수 있습니다.

```java
Dataset<Row> similarityDS = measurer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 CosineSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**CosineSimilarityMeasurerTest**](./src/test/java/com/r4tings/recommender/model/measures/similarity/CosineSimilarityMeasurerTest.java) 클래스는 CosineSimilarityMeasurer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 CosineSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.measures.similarity.CosineSimilarityMeasurerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 코사인 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/cosineSimilarityExamples01.svg">

여기에서, ${I_u} \cap {I_v}$는 사용자 $u$와 사용자 $v$ 모두에게 평가된 아이템 집합, ${r_{u,i}}$와 ${r_{v,i}}$는 사용자 $u$와 사용자 $v$가 아이템 $i$에 매긴 평점입니다.

(2) 사용자 코사인 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/cosineSimilarityExamples01.svg">

여기에서, ${I_u} \cap {I_v}$는 사용자 $u$와 사용자 $v$ 모두에게 평가된 아이템 집합, ${r_{u,i}}$와 ${r_{v,i}}$는 사용자 $u$와 사용자 $v$가 아이템 $i$에 매긴 평점입니다.

(3) 아이템 코사인 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/cosineSimilarityExamples02.svg">

여기에서, ${U_i} \cap {U_j}$는 아이템 $i$와 아이템 $j$를 모두 평가한 사용자 집합, ${r_{u,i}}$와 ${r_{u,j}}$는 사용자 $u$가 아이템 $i$와 아이템 $j$에 매긴 평점입니다.

<br/>

### 피어슨 상관계수와 유사도

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/PearsonSimilarity_Class_Diagram.svg)

피어슨 상관계수와 유사도 구현체인 [**PearsonSimilarityMeasurer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/measures/similarity/PearsonSimilarityMeasurer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 유사도 데이터로 변환하는 transform 메서드를 구현한 클래스입니다. 

PearsonSimilarityMeasurer 클래스는 유사도 계산을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
PearsonSimilarityMeasurer measurer =
    new PearsonSimilarityMeasurer()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setImputeZero(false)
        .setIds("u4", "u5")
        .setVerbose(true)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("similarity");
```

PearsonSimilarityMeasurer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수   | 유형    | 필수여부 | 기본값       | 설명                                                                                  |
|------------|---------|----------|--------------|---------------------------------------------------------------------------------------|
| group      | Enum    | Ｘ       | Group.USER   | 유사도 계산 기준을 Group 열거형 유형으로 설정(Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol   | String  | Ｘ       | criterion    | 유사도 계산 기준을 문자열로 설정(“user”: 사용자, “item”:아이템).                      |
| imputeZero |         | Ｘ       |              | 쌍대 열의 어느 한쪽 값이 결측인 경우의 결측치 처리(true: 0 대체, false: 쌍대 열 제외) |
| ids        |         | Ｘ       | \-           | 유사도 계산 대상 ID(LHS) 지정(미지정시 전체 대상)                                     |
| verbose    | boolean | Ｘ       | false        | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                        |
| userCol    | String  | Ｘ       | user         | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                           |
| itemCol    | String  | Ｘ       | item         | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                           |
| ratingCol  | String  | Ｘ       | rating       | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                           |
| outputCol  | String  | Ｘ       | “similarity” | 출력 컬럼명(기본값: “similarity”)으로 정규화된 평점                                   |

생성된 PearsonSimilarityMeasurer 인스턴스는 RatingSimilarityMeasurer클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 유사도를 계산 할 수 있습니다.

```java
Dataset<Row> similarityDS = measurer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 PearsonSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**PearsonSimilarityMeasurerTest**](./src/test/java/com/r4tings/recommender/model/measures/similarity/PearsonSimilarityMeasurerTest.java) 클래스는 PearsonSimilarityMeasurer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 PearsonSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.measures.similarity.PearsonSimilarityMeasurerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 피어슨 상관계수는 다음과 같이 정의됩니다.

$$pearson({{\bf{x}}_a},{{\bf{x}}_b}) = \frac{{{\mathop{\rm cov}} ({{\bf{x}}_a},{{\bf{x}}_b})}}{{{\sigma _{{{\bf{x}}_a}}}{\sigma _{{{\bf{x}}_b}}}}} = \frac{{\frac{{\sum\nolimits_i^n {({a_i} - {\mu _a})({b_i} - {\mu _b})} }}{{n - 1}}}}{{\sqrt {\frac{{\sum\nolimits_1^n {{{({a_i} - {\mu _a})}^2}} }}{{n - 1}}} \sqrt {\frac{{\sum\nolimits_1^n {{{({b_i} - {\mu _b})}^2}} }}{{n - 1}}} }} = \frac{{\sum\nolimits_1^n {({a_i} - {\mu _a})({b_i} - {\mu _b})} }}{{\sqrt {\sum\nolimits_1^n {{{({a_i} - {\mu _a})}^2}} } \sqrt {\sum\nolimits_1^n {{{({b_i} - {\mu _b})}^2}} } }}$$

여기에서 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$는 벡터인 ${{\bf{x}}_a} = ({a_1},{a_2}, \cdots ,{a_n})$와 ${{\bf{x}}_b} = ({b_1},{b_2}, \cdots ,{b_n})$이고, ${\mathop{\rm cov}} ({{\bf{x}}_a},{{\bf{x}}_b})$은 ${{\bf{x}}_a}$가 변할 때 ${{\bf{x}}_b}$가 변하는 정도를 나타내는 표본 공분산(Covariance), ${\sigma _{{{\bf{x}}_a}}}$와 ${\sigma _{{{\bf{x}}_b}}}$는 표본 표준 편차입니다.

두 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$간의 피어슨 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/pearsonSimilarityExamples01.svg">

여기에서 $pearson({{\bf{x}}_a},{{\bf{x}}_b})$는 두 벡터 간의 피어슨 상관계수로 0보다 큰 값(양의 상관관계)입니다.

이 식에서 보는 바와 같이 음이 아닌 유사도를 구하기 위해 $pearson({{\bf{x}}_a},{{\bf{x}}_b})$가 양의 상관관계인 경우에는 $1 - pearson({{\bf{x}}_a},{{\bf{x}}_b})$과 같이 1에서 상관계수 값을 빼서 비유사도를 구한 다음 1을 더하고 역수를 취하여 0과 1 사이의 유사도를 구합니다. $pearson({{\bf{x}}_a},{{\bf{x}}_b})$가 음의 상관관계이거나 무상관관계인 경우에는 유사도는 0입니다.

(2) 사용자 피어슨 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/pearsonSimilarityExamples02.svg">

여기에서 ${N_{{I_u} \cap {I_v}}}$ $n$은 아이템 집합 ${I_u} \cap {I_v}$의 원소 개수, ${r_{u,i}}$와 ${r_{v,i}}$는 사용자 $u$와 사용자 $v$가 아이템 $i$에 매긴 평점입니다. 

임의의 사용자 $u$와 사용자 $v$의 사용자 피어슨 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/pearsonSimilarityExamples03.svg">

여기에서 ${\mathop{\rm pearson}\nolimits} (u,v)$는 사용자 $u$와 사용자 $v$의 피어슨 상관계수입니다.

(3) 아이템 피어슨 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/pearsonSimilarityExamples04.svg">

여기에서 ${U_i} \cap {U_j}$는 아이템 $i$와 $j$를 모두 평가한 사용자 평점 집합, ${r_{u,i}}$와 ${r_{u,j}}$는 사용자 $u$가 아이템 $i$와 아이템 $j$에 매긴 평점, ${\mu _i}$은 아이템 $i$를 평가한 사용자 집합 ${U_i}$의 평점 평균, ${\mu_j}$는 아이템 $i$를 평가한 사용자 ${U_j}$집합의 평점 평균입니다.

임의의 아이템 $i$와 아이템 $j$의 아이템 피어슨 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/pearsonSimilarityExamples05.svg">

여기에서 ${\mathop{\rm pearson}\nolimits} (i,j)$는 아이템 $i$와 아이템 $j$의 피어슨 상관계수입니다.

<br/>

### 유클리드 거리와 유사도

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/EuclideanSimilarity_Class_Diagram.svg)

- 유클리드 거리와 유사도 구현체인 [**EuclideanSimilarityMeasurer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/measures/similarity/EuclideanSimilarityMeasurer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 유사도 데이터로 변환하는 transform 메서드를 구현한 클래스입니다.

EuclideanSimilarityMeasurer 클래스는 유사도 계산을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
EuclideanSimilarityMeasurer measurer =
    new EuclideanSimilarityMeasurer()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setImputeZero(false)
        .setWeight(10)
        .setIds("u4", "u5")
        .setVerbose(true)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("similarity");
```

EuclideanSimilarityMeasurer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수   | 유형    | 필수여부 | 기본값       | 설명                                                                                  |
|------------|---------|----------|--------------|---------------------------------------------------------------------------------------|
| group      | Enum    | Ｘ       | Group.USER   | 유사도 계산 기준을 Group 열거형 유형으로 설정(Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol   | String  | Ｘ       | criterion    | 유사도 계산 기준을 문자열로 설정(“user”: 사용자, “item”:아이템).                      |
| imputeZero |         | Ｘ       |              | 쌍대 열의 어느 한쪽 값이 결측인 경우의 결측치 처리(true: 0 대체, false: 쌍대 열 제외) |
| weight     | Integer |          | null         | 가중 유클리드 유사도 계산을 위한 환산 계수                                            |
| ids        |         | Ｘ       | \-           | 유사도 계산 대상 ID(LHS) 지정(미지정시 전체 대상)                                     |
| verbose    | boolean | Ｘ       | false        | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                        |
| userCol    | String  | Ｘ       | user         | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                           |
| itemCol    | String  | Ｘ       | item         | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                           |
| ratingCol  | String  | Ｘ       | rating       | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                           |
| outputCol  | String  | Ｘ       | “similarity” | 출력 컬럼명(기본값: “similarity”)으로 정규화된 평점                                   |

생성된 EuclideanSimilarityMeasurer 인스턴스는 RatingSimilarityMeasurer클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 유사도를 계산 할 수 있습니다.

```java
Dataset<Row> similarityDS = measurer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 EuclideanSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**EuclideanSimilarityMeasurerTest**](./src/test/java/com/r4tings/recommender/model/measures/similarity/EuclideanSimilarityMeasurerTest.java) 클래스는 EuclideanSimilarityMeasurer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 EuclideanSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.measures.similarity.EuclideanSimilarityMeasurerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 임의의 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$간의 유클리드 거리는 다음과 같이 정의됩니다.

$${\mathop{\rm dist}\nolimits} (a,b) = \sqrt {\sum\nolimits_1^n {{{({a_i} - {b_i})}^2}} } $$

유클리드 거리의 확장인 가중 유클리드 거리(WED: Weighted Euclidean Distance)는 다음과 같이 정의됩니다.

$$dis{t_{wed}}(a,b) = \sqrt {\sum\nolimits_1^n {{w_i}{{({a_i} - {b_i})}^2}} } $$

여기에서 $w$은 가중치(Weight)로 이 책에서는 축척을 나타내는 환산 계수(Scaling Factor)를 사용합니다.

축척을 나타내는 환산 계수는 다음과 같이 정의됩니다.

$$SF = \frac{{AS}}{{PS}}$$

여기에서 $SF$는 환산 계수(Scale Factor), $AS$는 실제 거리(Actual Scale)로 $PS$는 기준 축척(Principle Scale)입니다.

(2) 사용자 유클리드 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/euclideanSimilarityExamples01.svg">

여기에서 ${{\mathop{\rm dist}\nolimits} _{euclidean}}(u,v)$는 사용자 $u$와 사용자 $v$의 유클리드 거리입니다.

(3) 아이템 유클리드 유사도는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/euclideanSimilarityExamples02.svg">

여기에서 ${{\mathop{\rm dist}\nolimits} _{euclidean}}(i,j)$는 아이템 $i$와 아이템 $j$의 유클리드 거리입니다.

<br/>

### 이진 속성과 유사도

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/ExtendedJaccardSimilarity_Class_Diagram.svg)

- 이진 속성과 유사도 구현체인 [**ExtendedJaccardSimilarityMeasurer**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/measures/similarity/ExtendedJaccardSimilarityMeasurer.java) 클래스는 Apache Spark ML 패키지의 추상 클래스인 Transformer 클래스를 상속받아 평점 데이터를 유사도 데이터로 변환하는 transform 메서드를 구현한 클래스입니다.

ExtendedJaccardSimilarityMeasurer 클래스는 유사도 계산을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
ExtendedJaccardSimilarityMeasurer measurer =
    new ExtendedJaccardSimilarityMeasurer()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setImputeZero(true)
        .setIds("u4", "u5")
        .setVerbose(true)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("similarity");
```

ExtendedJaccardSimilarityMeasurer 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수   | 유형    | 필수여부 | 기본값       | 설명                                                                                  |
|------------|---------|----------|--------------|---------------------------------------------------------------------------------------|
| group      | Enum    | Ｘ       | Group.USER   | 유사도 계산 기준을 Group 열거형 유형으로 설정(Group.USER: 사용자, Group.ITEM: 아이템) |
| groupCol   | String  | Ｘ       | criterion    | 유사도 계산 기준을 문자열로 설정(“user”: 사용자, “item”:아이템).                      |
| imputeZero |         | Ｘ       | true         | 쌍대 열의 어느 한쪽 값이 결측인 경우의 결측치 처리(true: 0 대체, false: 쌍대 열 제외) |
| ids        |         | Ｘ       | \-           | 유사도 계산 대상 ID(LHS) 지정(미지정시 전체 대상)                                     |
| verbose    | boolean | Ｘ       | false        | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                        |
| userCol    | String  | Ｘ       | user         | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                           |
| itemCol    | String  | Ｘ       | item         | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                           |
| ratingCol  | String  | Ｘ       | rating       | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                           |
| outputCol  | String  | Ｘ       | “similarity” | 출력 컬럼명(기본값: “similarity”)으로 정규화된 평점                                   |

생성된 ExtendedJaccardSimilarityMeasurer 인스턴스는 RatingSimilarityMeasurer클래스에 구현된 transform 메서드를 사용하여 평점 데이터를 입력받아 유사도를 계산 할 수 있습니다.

```java
Dataset<Row> similarityDS = measurer.transform(ratingDS);
```


<br/>

예제 테스트 클래스인 ExtendedJaccardSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**ExtendedJaccardSimilarityMeasurerTest**](./src/test/java/com/r4tings/recommender/model/measures/similarity/binary/ExtendedJaccardSimilarityMeasurerTest.java) 클래스는 ExtendedJaccardSimilarityMeasurer 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 EuclideanSimilarityMeasurerTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.measures.similarity.binary.ExtendedJaccardSimilarityMeasurerTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

다음은 임의의 벡터 ${{\bf{x}}_i}$와 ${{\bf{x}}_j}$의 값을 비교하여 분할표로 나타낸 것입니다. 

이진 데이터 분할표 $M$
<table>
    <colgroup>
        <col />
        <col />
        <col />
        <col />
        <col />
    </colgroup>
    <thead>
        <tr class="header">
            <th colspan="2" rowspan="2"></th>
            <th colspan="3">${{\bf{x}}_i}$ </th>
        </tr>
        <tr class="odd">
            <th>1 (긍정)</th>
            <th>0 (부정)</th>
            <th>합계</th>
        </tr>
    </thead>
    <tbody>
        <tr class="odd">
            <td rowspan="3">${{\bf{x}}_j}$</td>
            <td>1 (긍정)</td>
            <td>$a$</td>
            <td>$b$</td>
            <td>$a+b$</td>
        </tr>
        <tr class="even">
            <td>0 (부정)</td>
            <td>$c$</td>
            <td>$d$</td>
            <td>$c+d$</td>
        </tr>
        <tr class="odd">
            <td>합계</td>
            <td>$a+c$</td>
            <td>$b+d$</td>
            <td>$n=a+b+c+d$</td>
        </tr>
    </tbody>
</table>

여기에서 $a$는 i와 j의 값이 모두 1인 값을 가지는 속성의 수로 긍정 일치(Positive Match)인 ${M_{11}}$, $b$ 와 $c$는 i와 j의 값 중 하나의 값이 0인 불일치(Mismatch)인 ${M_{01}}$과 ${M_{10}}$, $d$는 i와 j의 값이 모두 0인 값을 가지는 속성의 수로 부정 일치(Negative Match)인 ${M_{00}}$이며, $n$은 속성의 전체 개수입니다.

(1) 임의의 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$ 간의 단순 일치 계수(Simple matching coefficient)는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/extendedJaccardSimilarityExamples01.svg">

(2) 임의의 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$ 간의 자카드 계수(Jaccard Coefficient)는 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch04/extendedJaccardSimilarityExamples02.svg">

또한 다음과 같이 이진 속성에만 적용할 수 있는 확장로 두 벡터 간의 자카드 계수를 좀더 간단하게 계산할 수 있습니다.

(3) 임의의 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$간의 확장 자카드 계수(Extended Jaccard Coefficient)는 다음과 같이 정의됩니다.

$${\rm{ExtendedJaccard}}({{\bf{x}}_a},{{\bf{x}}_b}) = \frac{{{\bf{x}}_a^{\rm T}{{\bf{x}}_b}}}{{\left\| {{{\bf{x}}_a}}
\right\|_2^2 + \left\| {{{\bf{x}}_b}} \right\|_2^2 - {\bf{x}}_a^{\rm T}{{\bf{x}}_b}}} = \frac{{\sum\nolimits_1^n
{{a_i}{b_i}} }}{{\sum\nolimits_1^n {a_i^2 + } \sum\nolimits_1^n {b_i^2} - \sum\nolimits_1^n {{a_i}{b_i}} }}$$

여기에서 ${\bf{x}}_a^{\rm T}{{\bf{x}}b} = \sum\nolimits{i = 1}^n {{a_i}{b_i}} = {a_1}{b_1} + {a_2}{b_2} + \cdots {a_n}{b_n}$로 두 벡터의 내적(Dot Product), $\left\| {{{\bf{x}}_a}} \right\|_2^2$와 $\left\| {{{\bf{x}}_a}} \right\|_2^2$는 각 벡터의 유클리드 노름(L2 Norm)의 제곱입니다.

> **Note**
> 확장 자카드 계수는 타니모토 계수(Tanimoto Coefficient)라고도 지칭합니다. 집합 개념의 자카드 계수와 달리 이진 속성에만 적용할 수 있는 코사인 유사도를 확장한 확장 자카드 계수는 두 데이터 포인트의 각도와 상대적인 거리를 모두 고려하며 자카드 계수와는 다른 개념으로 내용이 다소 혼란스러울 수 있어 유의해서 사용해야 합니다

<br/>

## 이웃 기반 협업 필터링 추천

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch05/KNearestNeighbors_Class_Diagram.svg)

KNearestNeighborsParams 클래스와 KNearestNeighbors 클래스는 이웃 기반 협업 필터링 구현체입니다. KNearestNeighborsParams 클래스는 Apache Spark ML 패키지의 추상 클래스인 JavaParams 클래스를 상속받는 CommonParams 클래스를 구현하고 있는 클래스로 KNearestNeighbors 클래스의 생성자에 매개변수를 전달합니다. 

- KNearestNeighbors 클래스는 AbstractRecommender 클래스를 상속받아 평점 데이터(사용자/아이템/평점)를 사용자 기반 또는 아이템 기반의 추천 결과로 변환하는 recommend 메서드를 구현하고 있는 협업 필터링 추천을 처리하는 추천기(Recommender)입니다.

- KNearestNeighborsParams 클래스는 이웃 기반 협업 필터링 추천을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
KNearestNeighborsParams params =
    new KNearestNeighborsParams()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setRatingNormalizer(new MeanCenteringNormalizer())
        .setSimilarityMeasurer(new CosineSimilarityMeasurer().setCriterion(Criterion.USER))
        .setWeightedAverage(WeightedAverage.SIMPLE)
        .setK(20)
        .setMinimumNeighbors(2)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");
```

KNearestNeighborsParams 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수           | 유형                     | 필수여부 | 기본값                 | 설명                                                                                                                                                                                                    |
|--------------------|--------------------------|----------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| group              | Enum                     | Ｘ       | Group.USER             | 유사도 계산 기준을 Group 열거형 유형으로 설정(Group.USER: 사용자, Group.ITEM: 아이템)                                                                                                                   |
| groupCol           | String                   | Ｘ       | user                   | 유사도 계산 기준을 문자열로 설정(“user”: 사용자, “item”:아이템).                                                                                                                                        |
| ratingNormalizer   | RatingNormalizer         | Ｘ       | \-                     | 평점 정규화를 처리하는 RatingNormalizer 객체                                                                                                                                                            |
| similarityMeasurer | RatingSimilarityMeasurer | O        | \-                     | 유사도 계산을 처리하는 RatingSimilarityMeasurer 객체                                                                                                                                                    |
| weightedAverage    | Enum                     | Ｘ       | WeightedAverage.SIMPLE | 평점 예측을 위한 가중평균 계산 방법을 WeightedAverage 열거형 유형(WeightedAverage.SIMPLE: 단순 가중 평균, WeightedAverage.MEAN_CENTERING: 평균 중심 가중 평균, WeightedAverage.Z_SCORE: Z점수 가중평균) |
| k                  |                          | Ｘ       | 20                     | 최근접 이웃의 수(기본값: 20).                                                                                                                                                                           |
| minimumNeighbors   | Integer                  | Ｘ       | 2                      | 최소 이웃의 수(기본값: 2).                                                                                                                                                                              |
| verbose            | boolean                  | Ｘ       | false                  | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                                                                                                                          |
| userCol            | String                   | Ｘ       | user                   | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                                                                                                                                             |
| itemCol            | String                   | Ｘ       | item                   | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                                                                                                                                             |
| ratingCol          | String                   | Ｘ       | rating                 | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                                                                                                                                             |
| outputCol          | String                   | Ｘ       | “score”                | 출력 컬럼명(기본값: “score”)으로 정규화된 평점                                                                                                                                                          |

KNearestNeighbors 클래스는 다음 코드와 같이 생성된 KNearestNeighborsParams 클래스의 인스턴스를 생성자의 인자로 전달받아 인스턴스를 생성한 후 recommend 메서드를 사용하여 평점 데이터를 입력받아 예측 평점 높은 순의 Top-N 아이템 추천 데이터로 변환할 수 있습니다.

```java
KNearestNeighborsParams params =
    new KNearestNeighborsParams()
        .setGroup(Group.USER)
        .setGroupCol("user")
        .setRatingNormalizer(new MeanCenteringNormalizer())
        .setSimilarityMeasurer(new CosineSimilarityMeasurer().setCriterion(Criterion.USER))
        .setWeightedAverage(WeightedAverage.SIMPLE)
        .setK(20)
        .setMinimumNeighbors(2)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");

KNearestNeighbors recommender = new KNearestNeighbors(params);

Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, 10, “u4”);
```

KNearestNeighbors 클래스의 recommend 메서드에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수 | 유형           | 필수여부 | 기본값 | 설명                                        |
|----------|----------------|----------|--------|---------------------------------------------|
| ratings  | Dataset<Row> | O        | \-     | Spark의 Dataset<Row> 유형인 평점 데이터셋 |
| topN     | Integer        | O        | \-     | 추천 아이템 수                              |
| userId   | Object         | O        | \-     | 추천 받을 사용자 ID                         |


<br/>

예제 테스트 클래스인 KNearestNeighborsTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**KNearestNeighborsTest**](./src/test/java/com/r4tings/recommender/model/knn/KNearestNeighborsTest.java) 클래스는 KNearestNeighbors 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 KNearestNeighborsTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.knn.KNearestNeighborsTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

<br/>

### 유사도 가중 평균 기반 평점 예측

<br/>

(1) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(사용자 기반 추천)은 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{\sum\nolimits_{v \in {N_i}(u)} {{w_{u,v}}{r_{v,i}}} }}{{\sum\nolimits_{v \in {N_i}(u)} {\left| {{w_{u,v}}} \right|} }} = \frac{{\sum\nolimits_{v \in {N_i}(u)} {{\mathop{\rm sim}\nolimits} (u,v) \cdot {r_{v,i}}} }}{{\sum\nolimits_{v \in {N_i}(u)} {\left| {{\mathop{\rm sim}\nolimits} (u,v)} \right|} }}$$

여기에서, ${N_i}(u)$는 아이템 $i$를 평가한 사용자 $u$와 가장 유사한 $k$명의 사용자 집합($k$-근접 이웃), ${w_{u,v}}$는 사용자 $u$와 이웃 사용자 $v$의 유사도($u \ne v$), ${r_{v,i}}$은 사용자 $v$가 아이템 $i$에 매긴 평점입니다.

(2) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(아이템 기반 추천)은 다음과 같이 정의됩니다.

$${\hat r_{u,i}} = \frac{{\sum\nolimits_{j \in {N_u}(i)} {{w_{i,j}}{r_{u,j}}} }}{{\sum\nolimits_{j \in {N_u}(i)} {\left| {{w_{i,j}}} \right|} }} = \frac{{\sum\nolimits_{j \in {N_u}(i)} {{\mathop{\rm sim}\nolimits} (i,j) \cdot {r_{u,j}}} }}{{\sum\nolimits_{j \in {N_u}(i)} {\left| {{\mathop{\rm sim}\nolimits} (i,j)} \right|} }}$$

여기에서, ${N_u}(i)$는 사용자 $u$가 평가한 아이템 $i$와 가장 유사한 k개의 아이템 집합(k-근접 이웃), ${w_{i,j}}$는 아이템 $i$와 이웃 아이템 $j$의 유사도($i \ne j$), ${r_{u,j}}$은 사용자 $u$가 아이템 $j$에 매긴 평점입니다.

<br/>

### 평점 평균과 유사도 평균 중심 가중 평균 기반 평점 예측

<br/>

(1) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(사용자 기반 추천)은 다음과 같이 정의됩니다.

사용자 평균 중심으로 정규화된 평점은 다음과 같이 정의됩니다.

$${s_{u,i}} = {r_{u,i}} - {\mu _u}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu _u}$은 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점 평균입니다.

$${\hat r_{ui}} = {\mu_u} + \frac{{\sum\nolimits{v \in {N_i}(u)} {{w_{u,v}}{s_{v,i}}} }}{{\sum\nolimits_{v \in {N_i}(u)} {\left| {{w_{u,v}}} \right|} }} = {\mu_u} + \frac{{\sum\nolimits{v \in {N_i}(u)} {{\mathop{\rm sim}\nolimits} (u,v) \cdot ({r_{v,i}} - {\mu_v})} }}{{\sum\nolimits{v \in {N_i}(u)} {\left| {{\mathop{\rm sim}\nolimits} (u,v)} \right|} }}$$

여기에서, ${\mu_u}$는 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점 평균, ${N_i}(u)$는 아이템 $i$를 평가한 사용자 $u$와 가장 유사한 $k$명의 사용자 집합($k$-근접 이웃), ${w_{u,v}}$는 사용자 $u$와 이웃 사용자 $v$의 유사도($u \ne v$), ${s_{v,i}}$는 사용자 평균 중심으로 정규화된 평점, ${r_{v,i}}$은 사용자 $v$가 아이템 $i$에 매긴 평점, ${\mu _v}$는 이웃 사용자 $v$에게 평가된 아이템 집합 ${I_u}$의 평점 평균 입니다.

(2) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(아이템 기반 추천)은 다음과 같이 정의됩니다.

아이템 평균 중심으로 정규화된 평점은 다음과 같이 정의됩니다.

$${s_{u,i}} = {r_{u,i}} - {\mu _i}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu _i}$은 아이템 $i$를 평가한 사용자 집합 ${U_i}$의 평점 평균입니다.

$${\hat r_{ui}} = {\mu_i} + \frac{{\sum\nolimits_{j \in {N_u}(i)} {{w_{i,j}}{s_{u,j}}} }}{{\sum\nolimits_{j \in {N_u}(i)} {\left| {{w_{i,j}}} \right|} }} = {\mu_i} + \frac{{\sum\nolimits_{j \in {N_u}(i)} {{\mathop{\rm sim}\nolimits} (i,j) \cdot ({r_{u,j}} - {\mu j})} }}{{\sum\nolimits{j \in {N_u}(i)} {\left| {{\mathop{\rm sim}\nolimits} (i,j)} \right|} }}$$

여기에서, ${\mu_i}$는 아이템 $i$에 매겨진 사용자 집합 ${U_i}$의 평점 평균, ${N_u}(i)$는 사용자 $u$가 평가한 아이템 $i$와 가장 유사한 k개의 아이템 집합(k-근접 이웃), ${w_{i,j}}$는 아이템 $i$와 이웃 아이템 $j$의 유사도($i \ne j$), ${s_{u,j}}$는 아이템 평균 중심으로 정규화된 평점, ${r_{u,j}}$은 사용자 $u$가 아이템 $j$에 매긴 평점, ${\mu _j}$는 이웃 아이템 $j$에 매겨진 사용자 집합 ${U_i}$의 평점 평균입니다.

<br/>

### 평점 평균과 유사도 Z점수 가중 평균 기반 평점 예측

<br/>

(1) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(사용자 기반 추천)은 다음과 같이 정의됩니다.

사용자 Z점수로 정규화된 평점은 다음과 같이 정의됩니다.

$${z_{u,i}} = \frac{{{r_{u,i}} - {\mu_u}}}{{{\sigma_u}}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu_u}$와 ${\sigma_u}$는 아이템 집합 ${I_u}$의 평점 평균과 표준 편차입니다.

$${\hat r_{ui}} = {\mu_u} + {\sigma_u}\frac{{\sum\nolimits_{v \in {N_i}(u)} {{w_{u,v}}{z_{v,i}}} }}{{\sum\nolimits_{v \in {N_i}(u)} {\left| {{w_{u,v}}} \right|} }} = {\mu_u} + {\sigma_u}\frac{{\sum\nolimits_{v \in {N_i}(u)} {{\mathop{\rm sim}\nolimits} (u,v) \cdot (\frac{{{r_{v,i}} - {\mu_v}}}{{{\sigma_v}}})} }}{{\sum\nolimits_{v \in {N_i}(u)} {\left| {{\mathop{\rm sim}\nolimits} (u,v)} \right|} }}$$

여기에서, ${\mu_u}$와 ${\sigma_u}$는 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점 평균과 표준 편차, ${N_i}(u)$는 아이템 $i$를 평가한 사용자 $u$와 가장 유사한
$k$명의 사용자 집합($k$-근접 이웃), ${w_{u,v}}$는 가중치로 사용자 $u$와 이웃 사용자 $v$의 유사도($u \ne v$), ${z_{v,i}}$는 사용자 Z점수화된 평점, ${r_{v,i}}$은
사용자 $v$가 아이템 $i$에 매긴 평점, ${\mu_v}$와 ${\sigma_v}$는 이웃 사용자 $v$에게 평가된 아이템 집합 ${I_u}$의 평점 평균과 표준 편차입니다.

(2) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 예측 평점(아이템 기반 추천)은 다음과 같이 정의됩니다.

아이템 Z점수로 정규화된 평점은 다음과 같이 정의됩니다.

$${z_{u,i}} = \frac{{{r_{u,i}} - {\mu_i}}}{{{\sigma_i}}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${\mu_i}$와 ${\sigma_i}$는 ${I_u}$의 평점 평균과 표준 편차입니다.

$${\hat r_{ui}} = {\mu_i} + {\sigma_i}\frac{{\sum\nolimits_{j \in {N_u}(i)} {{w_{i,j}}{z_{u,j}}} }}{{\sum\nolimits_{j \in {N_u}(i)} {\left| {{w_{i,j}}} \right|} }} = {\mu_i} + {\sigma_i}\frac{{\sum\nolimits_{j \in {N_u}(i)} {{\mathop{\rm sim}\nolimits} (i,j) \cdot (\frac{{{r_{u,j}} - {\mu_j}}}{{{\sigma_j}}})} }}{{\sum\nolimits_{j \in {N_u}(i)} {\left| {{\mathop{\rm sim}\nolimits} (i,j)} \right|} }}$$

여기에서, ${\mu_i}$와 ${\sigma_i}$는 아이템 $i$에 매겨진 사용자 집합 ${U_i}$의 평점 평균과 표준편차, ${N_u}(i)$는 사용자 $u$가 평가한 아이템 $i$와 가장 유사한 k개의 아이템 집합(k-근접 이웃), ${w_{i,j}}$는 아이템 $i$와 이웃 아이템 $j$의 유사도($i \ne j$), ${z_{u,j}}$는 아이템 Z점수화된 평점, ${r_{u,j}}$은 사용자 $u$가 아이템 $j$에 매긴 평점, ${\mu_j}$는 이웃 아이템 $j$에 매겨진 사용자 집합 ${U_i}$의 평점 평균, ${\mu_j}$와 ${\sigma_j}$는 이웃 아이템 $v$에 매겨진 사용자 집합 ${U_i}$의 평점 평균과 표준편차입니다

<br/>

## 특잇값 분해 기반 협업 필터링 추천

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch06/BaselineSVD_Class_Diagram.svg)

BaselineSingleValueDecompositionParams 클래스와 BaselineSingleValueDecomposition 클래스는 특잇값 분해 기반 협업 필터링 구현체입니다. BaselineSingleValueDecompositionParams 클래스는 Apache Spark ML 패키지의 추상 클래스인 JavaParams 클래스를 상속받는 CommonParams 클래스를 구현하고 있는 클래스로 BaselineSingleValueDecomposition 클래스의 생성자에 매개변수를 전달합니다.

- BaselineSingleValueDecomposition 클래스는 AbstractRecommender 클래스를 상속받아 평점 데이터(사용자/아이템/평점)를 특잇값 분해 기반의 추천 결과로 변환하는 recommend 메서드를 구현하고 있는 협업 필터링 추천을 처리하는 추천기(Recommender)입니다.

- BaselineSingleValueDecompositionParams 클래스는 이웃 기반 협업 필터링 추천을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
BaselineSingleValueDecompositionParams params =
        new BaselineSingleValueDecompositionParams()
        .setBaselineModel(baselineModel)
        .setSingleValueDecompositionModel(svdModel)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");
```

BaselineSingleValueDecompositionParams 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수      | 유형                          | 필수여부 | 기본값  | 설명                                                           |
|---------------|-------------------------------|----------|---------|----------------------------------------------------------------|
| baselineModel | MeanRatingBaselineModel       | O        |         | 평점 기준선 모델인 MeanRatingBaselineModel 객체                |
| svdModel      | SingleValueDecompositionModel | O        |         | 특잇값 분해 모델인 SingleValueDecompositionModel 객체          |
| verbose       | boolean                       | Ｘ       | false   | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false). |
| userCol       | String                        | Ｘ       | user    | 평점 데이터의 사용자 컬럼명(기본값: “user”)                    |
| itemCol       | String                        | Ｘ       | item    | 평점 데이터의 아이템 컬럼명(기본값: “item”)                    |
| ratingCol     | String                        | Ｘ       | rating  | 평점 데이터의 평점 컬럼명(기본값: “rating”)                    |
| outputCol     | String                        | Ｘ       | “score” | 출력 컬럼명(기본값: “score”)으로 정규화된 평점                 |

BaselineSingleValueDecomposition 클래스는 다음 코드와 같이 생성된 BaselineSingleValueDecompositionParams 클래스의 인스턴스를 생성자의 인자로 전달받아 인스턴스를 생성한 후 recommend 메서드를 사용하여 평점 데이터를 입력받아 예측 평점 높은 순의 Top-N 아이템 추천 데이터로 변환할 수 있습니다.

```java
BaselineSingleValueDecompositionParams params =
    new BaselineSingleValueDecompositionParams()
        .setBaselineModel(baselineModel)
        .setSingleValueDecompositionModel(svdModel)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");

BaselineSingleValueDecomposition recommender = new BaselineSingleValueDecomposition(params);

Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, 10, “u4”);
```

BaselineSingleValueDecomposition 클래스의 recommend 메서드에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수 | 유형           | 필수여부 | 기본값 | 설명                                        |
|----------|----------------|----------|--------|---------------------------------------------|
| ratings  | Dataset<Row> | O        | \-     | Spark의 Dataset<Row> 유형인 평점 데이터셋 |
| topN     | Integer        | O        | \-     | 추천 아이템 수                              |
| userId   | Object         | O        | \-     | 추천 받을 사용자 ID                         |


<br/>

<br/>

예제 테스트 클래스인 BaselineSingleValueDecompositionTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**BaselineSingleValueDecompositionTest**](./src/test/java/com/r4tings/recommender/model/svd/BaselineSingleValueDecompositionTest.java) 클래스는 BaselineSingleValueDecomposition 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 BaselineSingleValueDecompositionTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.svd.BaselineSingleValueDecompositionTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 사용자 $u$가 아이템 $i$에 매긴 평점의 기준선 추정값은 다음과 같이 정의됩니다.

$${b_{ui}} = \mu  + {b_u} + {b_i}$$

여기에서 $\mu $는 전체 평점 평균(Overall Average Rating), ${b_u}$와 ${b_i}$는 사용자 $u$와 아이템 $i$의 기준선(Baseline/Bias), ${b_{ui}}$는 평점 기준선 추정값(Baseline Estimate)입니다.

(2) 사용자와 아이템의 기준선 추정을 위한 단순한 방법은 다음과 같이 정의됩니다.

사용자 기준선 추정인 경우에는 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점을 대상으로 합니다. 
사용자 기준선 추정의 단순식은 다음과 같이 정의됩니다.

$${b_u} = {\mu _u} - \mu $$

여기에서 ${\mu _u}$은 ${I_u}$의 평점 평균, $\mu $는 전체 평점 평균, ${b_u}$는 기준선 추정값입니다.

아이템 기준선 추정인 경우에는 아이템 $i$에 매겨진 사용자 집합 ${U_i}$의 평점을 대상으로 합니다. 
아이템 기준선 추정은 다음과 같이 정의됩니다.

$${b_i} = {\mu _i} - \mu $$

여기에서 ${\mu _i}$은 ${U_i}$의 평점 평균, $\mu $는 전체 평점 평균, ${b_i}$는 기준선 추정값입니다.

(3) 사용자와 아이템의 기준선 추정을 위한 일반적인 방법은 다음과 같이 정의됩니다.

여기에서는 사용자 기준선을 계산하기위해 필요한 아이템 기준선을 먼저 살펴봅니다. 마찬가지로 아이템 기준선 추정인 경우에는 아이템 $i$에 매겨진 사용자 집합 ${U_i}$의 평점을 대상으로 합니다. 

아이템 기준선 추정은 다음과 같이 정의됩니다.

$${b_i} = \frac{{\sum\nolimits_{i \in {I_u}} {({r_{ui}} - \mu )} }}{{\left| {{U_i}} \right| + \lambda 2}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $\mu $는 전체 평점 평균, $\left| {{U_i}} \right|$는 ${U_i}$의 원소 개수, $\lambda 2$는 정규화 매개변수(Regularization Parameter), ${b_i}$는 기준선 추정값입니다.

사용자 기준선 추정인 경우에도 마찬가지로 사용자 $u$에게 평가된 아이템 집합 ${I_u}$의 평점을 대상으로 합니다. 

사용자 기준선 추정의 단순식은 다음과 같이 정의됩니다.

$${b_u} = \frac{{\sum\nolimits_{i \in {I_u}} {({r_{ui}} - \mu  - {b_i})} }}{{\left| {{I_u}} \right| + \lambda 3}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, $\left| {{I_u}} \right|$는 ${I_u}$의 원소 개수, $\lambda 3$는 정규화 매개변수, ${b_u}$는 기준선 추정값입니다.

(4) 사용자 $u$가 아이템i에 매긴 평점의 잔차(Residual, Fitting Error)는 다음과 같이 정의됩니다.

$${z_{ui}} = {r_{ui}} - {b_{ui}}$$

여기에서 ${r_{u,i}}$은 사용자 $u$가 아이템 $i$에 매긴 평점, ${b_{ui}}$는 기준선 추정값, ${z_{ui}}$는 평점 잔차입니다.

(5) 크기가 $m \times n$인 행렬 $A$를 세 개의 행렬로 분해하는 특잇값 분해는 다음과 같이 정의됩니다.

$$A = {U}{\Sigma }{V^T}$$

여기에서, $m \times m$ 직교 행렬인 $U$와 $n \times n$ 직교 행렬인 ${V^T}$의 행(또는 $V$의 열 )들은 $A{A^T}$와 ${A^T}A$의 고유 벡터(eigenvector)들이며 각각을 행렬 A의 왼쪽 특이 벡터(left singular vector)와 오른쪽 특이 벡터(right singular vector)라 합니다. $T$는 전치 행렬 연산자입니다. $\sum $는 행렬 A의 특잇값(Singular Value)들을 대각 행렬의 값으로 하는 $m \times n$ 행렬입니다.

(6) k-계수(Rank)로 절단된 특잇값 분해(Truncated SVD)는 다음과 같이 정의됩니다.

$${A_k} \approx {U_k}{\Sigma _k}{V_k}^{\rm{T}}$$

여기에서 ${U_k}$, ${\Sigma _k}$, ${V_k}$는 $m \times k$, $k \times k$, $n \times k$ 행렬입니다. $k$는 계수로 $k \le \min \\{ {m,n} \\}$입니다.

(7) 절단된 특잇값 분해로 분해된 근사된 예측 평점 매트릭스 ${\hat R}$은 다음과 같이 정의됩니다.

$${\rm{\hat R}} \approx {\rm{B + }}{{\rm{P}}_{\rm{k}}}{\Sigma _k}Q_k^{\rm T}$$

여기에서 ${\rm{B}}$는 평점 기준선, ${{\rm{P}}_{\rm{k}}}$와 ${\Sigma _k}$, 그리고 ${Q_k}$는 평점 잔차 행렬($R - B$)을 $k$-절단된 특잇값 분해로 분해된 사용자 특징 행렬, 특잇값 행렬, 그리고 아이템 특징 행렬입니다.

(*) 사용자 $u$가 평가하지 않은 아이템 $i$에 대한 근사된 예측 평점은 다음과 같이 정의됩니다.

$${\hat r_{ui}} = {b_{ui}} + ({p_{u}} \times {\sigma }) \cdot {q_{i}} = {b_{ui}} + \sum\limits_k {p_{uk}}{\sigma_{k}}{q_{ik}} $$

여기에서 ${b_{ui}}$는 평점 기준선 추정값, ${p_{uk}}$는 사용자 $u$의 사용자 특징 벡터 ${p_{u}}$의 $k$번째 값, $\sigma_k$는 특잇값 벡터 $\sigma$의 $k$번째 값, ${q_{ik}}$는 아이템 $i$의 아이템 특징 벡터 ${{q}_i}$의 $k$번째 값입니다.
<!--
> **Note**
> 이 예제의 자세한 설명은 **_[WIKI](https://github.com/r4tings/r4tings-recommender-examples/wiki/)_** 를 참고하세요.
> * #### [6. 특잇값 분해 기반 협업 필터링 추천](https://github.com/r4tings/r4tings-recommender-examples/wiki/[Korean]-ch-06)
>   * ##### [6.1 모델 기반 협업 필터링](https://github.com/r4tings/r4tings-recommender-examples/wiki/[Korean]-ch-06-sec-01)
>   * ##### [6.2 기준선 추정과 특잇값 분해 기반 평점 예측](https://github.com/r4tings/r4tings-recommender-examples/wiki/[Korean]-ch-06-sec-02)
>   * ##### [6.3 요약(Summary)](https://github.com/r4tings/r4tings-recommender-examples/wiki/[Korean]-ch-06-sec-03)
-->
<br/>

## TF-IDF 기반 콘텐츠 기반 필터링 추천

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch07/TermFrequencyInverseDocumentFrequency_Class_Diagram.svg)

TermFrequencyInverseDocumentFrequencyParams 클래스와 TermFrequencyInverseDocumentFrequency 클래스는 TF-IDF 콘텐츠 기반 필터링 구현체입니다. TermFrequencyInverseDocumentFrequencyParams 클래스는 Apache Spark ML 패키지의 추상 클래스인 JavaParams 클래스를 상속받는 CommonParams 클래스를 구현하고 있는 클래스로 TermFrequencyInverseDocumentFrequency 클래스의 생성자에 매개변수를 전달합니다.

- TermFrequencyInverseDocumentFrequency 클래스는 AbstractRecommender 클래스를 상속받아 평점 데이터(사용자/아이템/평점)를 사용자 기반 또는 아이템 기반의 추천 결과로 변환하는 recommend 메서드를 구현하고 있는 협업 필터링 추천을 처리하는 추천기(Recommender)입니다.

- TermFrequencyInverseDocumentFrequencyParams 클래스는 TF-IDF 콘텐츠 기반 필터링 추천을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
TermFrequencyInverseDocumentFrequencyParams params =
    new TermFrequencyInverseDocumentFrequencyParams()
        .setSimilarityMeasure(SimilarityMeasure.get("COSINE").invoke(true, false))
        .setThreshold(3.5)
        .setTermCol("tag")
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");
```

TermFrequencyInverseDocumentFrequencyParams 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수          | 유형                | 필수여부 | 기본값  | 설명                                                                                            |
|-------------------|---------------------|----------|---------|-------------------------------------------------------------------------------------------------|
| similarityMeasure | UserDefinedFunction | O        | \-      | 유사도 계산을 위한 UserDefinedFunction 호출 열거형 유형(“cosine”: 코사인, “pearson”: 피어슨 등) |
| threshold         | Double              | O        | \-      | 평점 임곗값                                                                                     |
| termCol           | String              | Ｘ       | term    | 태그 데이터의 단어 컬럼명(기본값: “term”)                                                       |
| verbose           | boolean             | Ｘ       | false   | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false).                                  |
| userCol           | String              | Ｘ       | user    | 평점 데이터의 사용자 컬럼명(기본값: “user”)                                                     |
| itemCol           | String              | Ｘ       | item    | 평점 데이터의 아이템 컬럼명(기본값: “item”)                                                     |
| ratingCol         | String              | Ｘ       | rating  | 평점 데이터의 평점 컬럼명(기본값: “rating”)                                                     |
| outputCol         | String              | Ｘ       | “score” | 출력 컬럼명(기본값: “score”)으로 정규화된 평점                                                  |

TermFrequencyInverseDocumentFrequency 클래스는 다음의 코드와 같이 생성된 TermFrequencyInverseDocumentFrequencyParams 클래스의 인스턴스를 생성자의 인자로 전달받아 인스턴스를 생성한 후 recommend 메서드를 사용하여 평점 데이터와 태그 데이터를 입력받아 프로필 유사도 높은 순의 Top-N 아이템 추천 데이터로 변환할 수 있습니다.

```java
TermFrequencyInverseDocumentFrequencyParams params =
    new TermFrequencyInverseDocumentFrequencyParams()
        .setSimilarityMeasure(SimilarityMeasure.get("COSINE").invoke(true, false))
        .setThreshold(3.5)
        .setTermCol("tag")
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");

TermFrequencyInverseDocumentFrequency recommender = new TermFrequencyInverseDocumentFrequency(params);

Dataset<Row> recommendedItemDS = recommend(ratingDS, termDS, 10, "u4");
```

TermFrequencyInverseDocumentFrequency 클래스의 recommend 메서드에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수 | 유형           | 필수여부 | 기본값 | 설명                                        |
|----------|----------------|----------|--------|---------------------------------------------|
| ratings  | Dataset<Row> | O        | \-     | Spark의 Dataset<Row> 유형인 평점 데이터셋 |
| terms    | Dataset<Row> | O        | \-     | Spark의 Dataset<Row> 유형인 태그 데이터셋 |
| topN     | Integer        | O        | \-     | 추천 아이템 수                              |
| userId   | Object         | O        | \-     | 추천 받을 사용자 ID                         |


<br/>

예제 테스트 클래스인 TermFrequencyInverseDocumentFrequencyTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**TermFrequencyInverseDocumentFrequencyTest**](./src/test/java/com/r4tings/recommender/model/tfidf/TermFrequencyInverseDocumentFrequencyTest.java) 클래스는 TermFrequencyInverseDocumentFrequency 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 TermFrequencyInverseDocumentFrequencyTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.tfidf.TermFrequencyInverseDocumentFrequencyTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

문서 단어 행렬(Document-Term Matrix, DTM)은 문서(아이템)는 행, 단어(태그)는 열로 나타낸 이원 도수 분포표입니다.

이원 도수 분포표

|     | ${t_1}$ | ${t_2}$ | $\cdots$ | ${t_n}$ |
|-----|-----|-----|-----|-----|
| ${d_1}$ | ${w_{11}}$ | ${w_{12}}$ | $\cdots$ | ${w_{1n}}$ |
| ${d_2}$ | ${w_{21}}$ | ${w_{22}}$ | $\cdots$ | ${w_{2n}}$ |
| $\cdots$ | $\cdots$ | $\cdots$ | $\cdots$ | $\cdots$ |
| ${d_m}$ | ${w_{m1}}$ | ${w_{m2}}$ | $\cdots$ | ${w_{mn}}$ |

(1) 문서에서 출현하는 단어의 중요도를 나타내는 단어 빈도와 역문서 빈도(Term Frequency- Inverse Document Frequency, TF-IDF)는 다음과 같이 정의됩니다.

$${\mathop{\rm tf}\nolimits}  - idf({t_i},{d_j},D) = {\mathop{\rm tf}\nolimits} ({t_i},{d_j}) \times {\mathop{\rm idf}\nolimits} ({t_i},D)$$

여기에서, ${\mathop{\rm tf}\nolimits} ({t_i},{d_j})$는 문서 ${d_j}$에서 단어 ${t_i}$의 발생 수로 단어 빈도, ${\mathop{\rm idf}\nolimits} ({t_i},D)$는 전체 문서 집합 $D$에서 단어 ${t_i}$의 역문서 빈도입니다. ${\mathop{\rm tf}\nolimits}  - idf({t_i},{d_j},D)$는 단어 빈도와 역문서 빈도입니다.

단어 빈도(Term Frequency, TF)는 다음과 같이 정의됩니다.

$${\mathop{\rm tf}\nolimits} ({t_i},{d_j}) = {w_{{t_i},{d_j}}}$$

여기에서 ${w_{{t_i},{d_j}}}$는 문서 ${d_j}$에서 단어 ${t_i}$의 출현 횟수입니다.

역문서 빈도(Inverse Document Frequency, IDF)는 다음과 같이 정의됩니다.

$${\mathop{\rm idf}\nolimits} ({t_i},D) = \log_{10}(\frac{|D|}{n_{t_i}})$$

여기에서 $\left| D \right|$는 전체 문서 집합 $D$의 원소 개수로 전체 문서의 수, ${n_{{t_i}}}$는 단어 ${t_i}$가 출현한 문서의 수입니다.

(2) 벡터 길이 정규화(Unit Length Normalization)는 다음과 같이 정의됩니다.

벡터 ${\bf{x}}$의 ${L_2}$ 노름은 다음과 같이 정의됩니다.

<img src="https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch07/termFrequencyInverseDocumentFrequencyExamples01.svg">

벡터 길이 정규화는 다음과 같이 정의됩니다.

$${\bf{x'}} = \frac{{\bf{x}}}{{\left\| {\bf{x}} \right\|}}$$

여기에서 $\left\| {\bf{x}} \right\|$는 벡터의 유클리드 노름입니다.

(3) 두 벡터 ${{\bf{x}}_a}$와 ${{\bf{x}}_b}$간의 코사인 유사도는 다음과 같이 정의됩니다.

$${\mathop{\rm sim}\nolimits} ({{\bf{x}}_a},{{\bf{x}}_b}) = cos({{\bf{x}}_a},{{\bf{x}}_b}) = \frac{{{\bf{x}}_a^{\rm T}{{\bf{x}}_b}}}{{{{\left\| {{{\bf{x}}_a}} \right\|}_2}{{\left\| {{{\bf{x}}_b}} \right\|}_2}}} = \frac{{\sum\nolimits_1^n {{a_i}{b_i}} }}{{\sqrt {\sum\nolimits_1^n {a_i^2} } \sqrt {\sum\nolimits_1^n {b_i^2} } }}$$

여기에서 ${\bf{x}}_a^{\rm T}{{\bf{x}}_b} = \sum\nolimits_1^n {{a_i}{b_i}} = {a_1}{b_1} + {a_2}{b_2} + \cdots {a_n}{b_n}$로 두 벡터의 내적(Dot Product), ${\left\| {{{\bf{x}}_a}} \right\|_2}$와 ${\left\| {{{\bf{x}}_b}} \right\|_2}$는 각 벡터의 유클리드 노름(L2 Norm)입니다.

<br/>

## 연관규칙 기반 추천

<br/>

![Download](https://github.com/r4tings/r4tings-recommender-examples/raw/master/src/test/puml/ch08/AssociationRuleMining_Class_Diagram.svg)

AssociationRuleMiningParams 클래스와 AssociationRuleMining 클래스는 연관규칙 기반 필터링 구현체입니다. AssociationRuleMiningParams 클래스는 Apache Spark ML 패키지의 추상 클래스인 JavaParams 클래스를 상속받는 CommonParams 클래스를 구현하고 있는 클래스로 AssociationRuleMining 클래스의 생성자에 매개변수를 전달합니다. 

- [**AssociationRuleMining**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/arm/AssociationRuleMining.java) 클래스는 AbstractRecommender 클래스를 상속받아 평점 데이터(사용자/아이템/평점)를 사용자 기반 또는 아이템 기반의 추천 결과로 변환하는 recommend 메서드를 구현하고 있는 추천을 처리하는 추천기(Recommender)입니다.

- [**AssociationRuleMiningParams**](https://github.com/r4tings/r4tings-recommender/blob/master/src/main/java/com/r4tings/recommender/model/arm/AssociationRuleMiningParams.java) 클래스는 연관규칙 기반 필터링 추천을 위해 필요한 매개변수의 설정이나 기본값 변경이 필요한 경우에는 필요에 따라 다음 코드와 같이 빌더 패턴을 사용하여 인스턴스를 생성할 수 있습니다.

```java
AssociationRuleMiningParams params =
        new AssociationRuleMiningParams()
        .setMinSupport(0.5)
        .setMinConfidence(0.5)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("score");
```

AssociationRuleMiningParams 클래스의 인스턴스에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수      | 유형    | 필수여부 | 기본값    | 설명                                                           |
|---------------|---------|----------|-----------|----------------------------------------------------------------|
| minSupport    | Double  | O        | \-        | 지지도 최소 임곗값                                             |
| minConfidence | String  | O        | \-        | 신뢰도 최소 임곗값                                             |
| verbose       | boolean | Ｘ       | false     | 처리과정에 대한 정보를 출력할 것인지 여부 체크(기본값: false). |
| userCol       | String  | Ｘ       | user      | 평점 데이터의 사용자 컬럼명(기본값: “user”)                    |
| itemCol       | String  | Ｘ       | item      | 평점 데이터의 아이템 컬럼명(기본값: “item”)                    |
| ratingCol     | String  | Ｘ       | rating    | 평점 데이터의 평점 컬럼명(기본값: “rating”)                    |
| outputCol     | String  | Ｘ       | support | 출력 컬럼명(기본값: “support”)으로 흥미도                      |

AssociationRuleMining 클래스는 다음 코드와 같이 생성된 AssociationRuleMiningParams 클래스의 인스턴스를 생성자의 인자로 전달받아 인스턴스를 생성한 후 recommend 메서드를 사용하여 평점 데이터와 태그 데이터를 입력받아 프로필 유사도 높은 순의 Top-N 아이템 추천 데이터로 변환할 수 있습니다.

```java
AssociationRuleMiningParams params =
    new AssociationRuleMiningParams()
        .setMinSupport(0.5)
        .setMinConfidence(0.5)
        .setVerbose(false)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
        .setOutputCol("support");

AssociationRuleMining recommender = new AssociationRuleMining(params);

Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, 10, "i3");
```

AssociationRuleMining 클래스의 recommend 메서드에 설정 가능한 매개변수는 다음과 같습니다.

주요 매개변수

| 매개변수 | 유형           | 필수여부 | 기본값 | 설명                                        |
|----------|----------------|----------|--------|---------------------------------------------|
| ratings  | Dataset<Row> | O        | \-     | Spark의 Dataset<Row> 유형인 평점 데이터셋 |
| topN     | Integer        | O        | \-     | 추천 아이템 수                              |
| itemId   | Object         | O        | \-     | 추천 받을 아이템 ID                         |


<br/>

예제 테스트 클래스인 AssociationRuleMiningTest 클래스의 테스트 메서드인 testWithExample 실행 결과를 살펴봅니다.

- [**AssociationRuleMiningTest**](./src/test/java/com/r4tings/recommender/model/arm/AssociationRuleMiningTest.java) 클래스는 AssociationRuleMining 클래스를 테스트하기 위한 JUnit으로 작성된 예제 소스 코드입니다.

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 AssociationRuleMiningTest 클래스의 테스트 메서드인 testWithExample 실행해 봅니다.

```
./gradlew :test --tests com.r4tings.recommender.model.arm.AssociationRuleMiningTest.testWithExample
```
<!--
https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e
-->

> **Note**
> 구현 수식

(1) 항목 집합(Itemset)은 다음과 같이 정의됩니다.

데이터에 존재하는 모든 항목의 집합 $I = \{ {i_1},{i_2},{i_3}, \cdots ,{i_m}\} $는 전체 항목 집합이라고 하고 전체 트랜잭션 집합 $T = \{ {t_1},{t_2},{t_3}, \ldots ,{t_n}\} $를 고유의 트랜잭션 ID를 가진 각각의 거래 내역인 트랜잭션(Transaction) ${t_i}$의 집합이라고 하면 ${t_i}$는 구매 항목들이 존재하게 되어 ${t_i} \subseteq I$가 됩니다. 항목 집합(Itemset)은 하나 이상의 항목들의 집합으로 트랜잭션 ${t_i}$의 부분 집합입니다. 덧붙여서 항목 집합에 속하는 항목 개수가 $k$개이면 이를 $k$-항목 집합이라고 합니다.

(2) 항목 집합의 지지도 카운트는 다음과 같이 정의됩니다.

$$\sigma (X) = \left| {\{ {t_i}|X \subseteq {t_i},{t_i} \in T\} } \right|$$

여기에서 $X$는 항목 집합, $t$는 개별 트랜잭션, $T$는 전체 트랜잭션 집합, $\left| {{\rm{ }} \cdot {\rm{ }}} \right|$는 집합의 원소 개수(Cardinality), $\sigma (X)$는 지지도 카운트입니다.

(3) 항목 집합 $X$와 $Y$간의 연관규칙은 다음과 같이 정의됩니다.

$$X \Rightarrow Y$$

여기에서 $X$와 $Y$가 하나 이상의 항목들로 구성되는 항목 집합이라고 할 때 $X \subseteq {t_i}$, $Y \subseteq {t_i}$, 그리고 $X \cap Y = \emptyset $입니다. 연관규칙 $X \Rightarrow Y$에서 각각의 항목 집합은 $X$와 $Y$이며 $X$는 "만일 \~라면"에 해당하는 부분인 조건절(Left Hand Side, LHS)이고 $Y$는 그 뒷부분에 해당하는 결과절(Right Hand Side, RHS)입니다.

(4) 항목집합의 지지도는 전체 트랜잭션에서 특정 항목집합을 포함하고 있는 트랜잭션의 비율로 특정 항목집합의 발생 확률로 항목집합의 지지도는 다음과 같이 정의됩니다.

$${\mathop{\rm support}\nolimits} (X) = P(X) = \frac{{\sigma (X)}}{{\left| T \right|}}$$

여기에서, $\sigma (X)$는 항목 집합 $X$의 지지도 카운트, $\left| T \right|$는 전체 트랜잭션 집합 $T$의 원소 개수입니다.

(5) 연관규칙의 지지도는 전체 트랜잭션에서 조건절과 결과절의 항목 집합을 모두 포함하고 있는 트랜잭션의 비율로 두 항목 집합의 동시 발생 확률로 다음과 같이 정의됩니다.

$${\mathop{\rm support}\nolimits} (X \Rightarrow Y) = P(X,Y) = P(X \wedge Y) = P(X \cap Y) = \frac{{\sigma (X,Y)}}{{\left| T \right|}}$$

여기에서, $\sigma (X,Y)$는 항목 집합 $X$와 $Y$의 지지도 카운트, $\left| T \right|$는 전체 트랜잭션 집합 $T$의 원소 개수입니다.

(6) 연관규칙의 신뢰도는 다음과 같이 정의됩니다.

$${\mathop{\rm confidence}\nolimits} (X \Rightarrow Y) = P(Y|X) = \frac{{P(X,Y)}}{{P(X)}} = \frac{{support(X \Rightarrow Y)}}{{support(X)}}$$

여기에서 ${\mathop{\rm support}\nolimits} (X \Rightarrow Y)$는 연관규칙 $X \Rightarrow Y$의 지지도, ${\mathop{\rm support}\nolimits} (X)$는 항목 집합 $X$의 지지도입니다.

(7) 연관규칙의 향상도는 다음과 같이 정의됩니다.

$${\mathop{\rm lift}\nolimits} (X \Rightarrow Y) = \frac{{P(Y|X)}}{{P(Y)}} = \frac{{P(X,Y)}}{{P(X) \cdot P(Y)}} = \frac{{{\mathop{\rm confidence}\nolimits} (X \Rightarrow Y)}}{{{\mathop{\rm support}\nolimits} (Y)}}$$

여기에서 ${\mathop{\rm confidence}\nolimits} (X \Rightarrow Y)$는 연관규칙 $X \Rightarrow Y$의 신뢰도, ${\mathop{\rm support}\nolimits} (Y)$는 항목 집합 $Y$의 지지도입니다.

<br/>

## 피드백과 기여

<br/>

기능 요청이 있는 경우 **[ISSUES](https://github.com/r4tings/r4tings-recommender-examples/issues/)** 에 등록하세요. **[DISCUSSIONS](https://github.com/r4tings/r4tings-recommender-examples/discussions/)** 을 통해서도 질문하실 수 있습니다. 

R4tings Recommender 프로젝트의 참여나 기여도 환영합니다. 자세한 정보는 **[여기](CONTRIBUTORS.md)** 에서 확인할 수 있습니다. 

<br/>

## 라이선스

<br/>

Released under the Open Publication License, v1.0 or later.

<br/>

<div align="right">
      
~~[EN]~~ | **KO** |  ~~JP~~

</div>

<!--

“**R4tings Recommender 오픈 소스 추천 엔진 패키지**”는 JVM(Java와 Scala)과 [Apache Spark](https://spark.apache.org/) 기반의 학술 연구/상용 목적의 추천 시스템을 구현하기 위한 오픈 소스 추천 엔진 패키지로 통계나 기계 학습 기반 추천 모델들의 기본 구현체인 “**[R4tings Recommender](https://github.com/r4tings/r4tings-recommender)**”와 응용 예제들인 “**R4tings Recommender Examples**”를 통해 추천 과정을 단계별로 분해하여 흐름을 쉽게 파악하고, 어느 도메인에서도 손쉽게 수정하거나 확장 또는 재사용할 수 있고, 이를 하나의 파이프라인으로 연결하여 병렬 처리 할 수 있습니다.

R4tings Recommender는 데이터의 이해와 전처리 과정에서 사용하는 모듈과 추천 모델 생성과 추천 예측을 위해 사용하는 모듈로 구성되어있다.
전자는 데이터 로딩, 평점 표준화, (비)유사도 측정에 사용한다. 후자는 여러 알고리즘을 이용해 추천 및 예측 모델 생성을 위해 사용한다.

R4tings Recommender는 머신러닝 시스템의 학습과 이해, 추천 시스템 개념증명 및 구현, 중/대규모의 추천 데이터 처리에 사용할 것으로 기대된다.
또한, 학습 및 연구 목적의 프로토타이핑, 개인화된 추천을 위한 데이터 분석에 활용될 것이다.

-->
