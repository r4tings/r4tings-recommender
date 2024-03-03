# Get Started

## 전제조건(Prerequisites)

### 필수 소프트웨어(Required software)
|소프트웨어|버전|설명|
|------|---|---|
|JDK|11| <p>OpenJDK 또는 Oracle JDK <p>* OpenJDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/) 를 참고하세요. <p>* Oracle JDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/)를 참고하세요.|

### 선택 소프트웨어(Optional software)
|소프트웨어|버전|설명|
|------|---|---|
|Git|Latest| Git을 내려받고 구성하는 방법은 [링크](https://git-scm.com/downloads)를 참고하세요|
|Git Client|Latest| <p>GitHub Desktop 또는 Sourcetree <p>* GitHub Desktop을 내려받고 구성하는 방법은 [링크](https://docs.github.com/en/desktop/)를 참고하세요 <p>* Sourcetree를 내려받고 구성하는 방법은 [링크](https://www.sourcetreeapp.com/)를 참고하세요|
|Gradle|Latest|Build Tool을 내려받고 구성하는 방법은 [링크](https://docs.gradle.org/current/userguide/what_is_gradle.html/)를 참고하세요|
|IntelliJ|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://www.jetbrains.com/idea/)를 참고하세요|
|R|4.3|<p>R-4.3.2 for Windows를 내려받고 구성하는 방법은 [링크](https://cran.r-project.org/bin/windows/base/) 를 참고하세요.|
|RTools|4.3| <p>RTools: Toolchains for building R and R packages from source on Windows를 내려받고 구성하는 방법은 [링크](https://cran.r-project.org/bin/windows/Rtools/) 를 참고하세요.|
|RStudio Desktop|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://posit.co/products/open-source/rstudio/)를 참고하세요|

### 프로젝트 구성하기(Set up the project)

프로젝트 구성하기는 [링크](https://github.com/r4tings/r4tings-recommender#프로젝트-구성하기set-up-the-project) 를 참고하세요

## 데이터셋 준비하기(Prepare Dataset)

예제 테스트 클래스인 [**DatasetPrepareTest**](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java) 클래스의 테스트 메서드인 r4tingsDataset 실행 결과를 살펴봅니다.

* [r4tingsDatasetExamples](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java#L47)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 r4tingsDataset 실행해 봅니다.

```
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.r4tingsDataset
```


https://github.com/r4tings/r4tings-recommender/assets/123946859/be1e069e-00e9-41a2-bf43-3db9462ce2b0


Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 r4tingsDatasetExamples 실행 후, R4tings Recommender 오픈소스 추천엔진의 dataset 디렉토리 구조는 다음과 같습니다.

```
C:\r4tings
   └── r4tings-recommender
       ├── dataset                                 <- 예제 데이터셋 
       │   │
       │   ├──  ⋯ -일부 생략 -
       │   │
       │   └── r4tings                             <- r4tings 데이터셋
       │       ├── items.parquet                   <- 아이템 데이터 (Parquet 형식)
       │       ├── ratings.parquet                 <- 평점 데이터 (Parquet 형식)
       │       ├── tags.parquet                    <- 태그 데이터 (Parquet 형식)
       │       ├── terms.parquet                   <- 단어 데이터 (Parquet 형식)
       │       ├── items.csv                       <- 아이템 데이터
       │       ├── ratings.csv                     <- 평점 데이터
       │       ├── tags.csv                        <- 태그 데이터
       │       └── terms.csv                       <- 단어 데이터
       │
       └── ⋯ -일부 생략 -  
```

## 예제 실행하기(Executing Examples)

Windows OS에서의 전체 예제 실행은 [`getting-started-recommender-examples-on-windows.ps1`](/recommender-examples/getting-started-recommender-examples-on-windows.ps1) 를 참고하세요.

```powershell
##############################################################################
# 평점 정규화(Normalize rating)
##############################################################################

# 평균 중심화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples

# Z점수화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples

# 최소-최대화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples

# 소수 스케일링화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples

# 이진 임계화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.BinaryThresholdingTest.binaryThresholdingExamples

##############################################################################
# 유사도 계산(Calculate similarity)
##############################################################################

# 코사인 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.CosineSimilarityTest.cosineSimilarityExamples

# 피어슨 상관계수와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.PearsonSimilarityTest.pearsonSimilarityExamples

# 유클리드 거리와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples

# 이진 속성과 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples

##############################################################################
# 평점 예측과 아이템 추천(Recommend top-N items with highest rating prediction)
##############################################################################

# 이웃 기반 협업 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch05.KNearestNeighborsTest.kNearestNeighborsExamples

# 특잇값 분해 기반 협업 필터링 추천 
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples

# TF-IDF 기반 콘텐츠 기반 필터링 추천 
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples

# 연관규칙 기반 추천 
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch08.AssociationRuleMiningTest.associationRuleMiningExamples
```
### 평점 정규화


https://github.com/r4tings/r4tings-recommender/assets/123946859/ceab3963-c27e-4617-a3b3-d9a0dcd71891


### 유사도 계산


https://github.com/r4tings/r4tings-recommender/assets/123946859/03a122d3-c094-44d7-8b6d-4fb7280ae616


### 평점 예측과 아이템 추천


https://github.com/r4tings/r4tings-recommender/assets/123946859/71763280-ad25-4517-b2e6-3f0fc4008e18



## 워크북(Workbook)

- [1. 추천과 추천 기법](https://r4tings.com/recommender/docs/workbook/latest/ch-01)
    - [1.1 주요 용어와 개념](https://r4tings.com/recommender/docs/workbook/latest/ch-01-sec-01)
    - [1.2 협업 필터링과 콘텐츠 기반 필터링](https://r4tings.com/recommender/docs/workbook/latest/ch-01-sec-02)
    - [1.3 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-01-sec-03)
- [2. 데이터셋 살펴보기](https://r4tings.com/recommender/docs/workbook/latest/ch-02)
    - [2.1 북크로싱 데이터셋](https://r4tings.com/recommender/docs/workbook/latest/ch-02-sec-01)
    - [2.2 무비렌즈 데이터셋](https://r4tings.com/recommender/docs/workbook/latest/ch-02-sec-02)
    - [2.3 예제 데이터셋](https://r4tings.com/recommender/docs/workbook/latest/ch-02-sec-03)
    - [2.4 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-02-sec-04)
- [3. 평점 정규화](https://r4tings.com/recommender/docs/workbook/latest/ch-03)
    - [3.1 평점 정규화와 이진화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-01)
    - [3.2 평균 중심화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-02)
    - [3.3 Z점수화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-03)
    - [3.4 최소-최대화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-04)
    - [3.5 소수 스케일링화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-05)
    - [3.6 이진 임계화](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-06)
    - [3.7 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-03-sec-07)
- [4. 유사도](https://r4tings.com/recommender/docs/workbook/latest/ch-04)
    - [4.1 유사도와 거리](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-01)
    - [4.2 코사인 유사도](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-02)
    - [4.3 피어슨 상관계수와 유사도](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-03)
    - [4.4 유클리드 거리와 유사도](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-04)
    - [4.5 이진 속성과 유사도](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-05)
    - [4.6 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-04-sec-06)
- [5. 이웃 기반 협업 필터링 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-05)
    - [5.1 메모리 기반 협업 필터링](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-01)
    - [5.2 가중 평균 유사도 평점 예측](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-02)
    - [5.3 평균 중심 가중 평균 유사도 평점 예측](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-03)
    - [5.4 Z점수 가중 평균 유사도 평점 예측](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-04)
    - [5.5 예제 코드 실행해보기](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-05)
    - [5.6 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-05-sec-06)
- [6. 특잇값 분해 기반 협업 필터링 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-06)
    - [6.1 모델 기반 협업 필터링](https://r4tings.com/recommender/docs/workbook/latest/ch-06-sec-01)
    - [6.2 특잇값 분해 평점 예측](https://r4tings.com/recommender/docs/workbook/latest/ch-06-sec-02)
    - [6.3 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-06-sec-03)
- [7. TF-IDF 콘텐츠 기반 필터링 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-07)
    - [7.1 TF-IDF와 콘텐츠 기반 필터링](https://r4tings.com/recommender/docs/workbook/latest/ch-07-sec-01)
    - [7.2 TF-IDF와 코사인 유사도 기반 아이템 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-07-sec-02)
    - [7.3 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-07-sec-03)
- [8. 연관규칙 기반 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-08)
    - [8.1 연관규칙](https://r4tings.com/recommender/docs/workbook/latest/ch-08-sec-01)
    - [8.2 연관규칙 기반 아이템 추천](https://r4tings.com/recommender/docs/workbook/latest/ch-08-sec-02)
    - [8.3 요약(Summary)](https://r4tings.com/recommender/docs/workbook/latest/ch-08-sec-03)
- References
    - [Recommender systems handbook](https://link.springer.com/book/10.1007/978-0-387-85820-3). Francesco Ricci, Lior Rokach, Bracha Shapira, Paul B. Kantor. (2011).
    - [Recommender Systems  - The Textbook](https://link.springer.com/book/10.1007/978-3-319-29659-3). Charu C. Aggarwal. (2016).
    - [recommenderlab: An R framework for developing and testing recommendation algorithms](https://doi.org/10.48550/arXiv.2205.12371). Michael Hahsler. (2022).
    - [Recommender Systems Specialization](https://www.coursera.org/specializations/recommender-systems). Coursera.
    - [Apache Spark](https://spark.apache.org). The Apache Software Foundation.

<br/>

<div align="right">
   <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a>
</div>
