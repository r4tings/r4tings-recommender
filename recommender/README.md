# Get Started

## 전제조건(Prerequisites)

### 필수 소프트웨어(Required software)
|소프트웨어|버전|설명|
|------|---|---|
|JDK|11| <p>OpenJDK 또는 Oracle JDK <p>* OpenJDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/) 를 참고하세요 <p>* Oracle JDK를 내려받고 구성하는 방법은 [링크](https://docs.oracle.com/en/java/javase/11/)를 참고하세요|

### 선택 소프트웨어(Optional software)
|소프트웨어|버전|설명|
|------|---|---|
|Git|Latest| Git을 내려받고 구성하는 방법은 [링크](https://git-scm.com/downloads)를 참고하세요|
|Git Client|Latest| <p>GitHub Desktop 또는 Sourcetree <p>* GitHub Desktop을 내려받고 구성하는 방법은 [링크](https://docs.github.com/en/desktop/)를 참고하세요 <p>* Sourcetree를 내려받고 구성하는 방법은 [링크](https://www.sourcetreeapp.com/)를 참고하세요|
|Gradle|Latest|Build Tool을 내려받고 구성하는 방법은 [링크](https://docs.gradle.org/current/userguide/what_is_gradle.html/)를 참고하세요|
|IntelliJ|Latest|IntelliJ를 내려받고 구성하는 방법은 [링크](https://www.jetbrains.com/idea/)를 참고하세요|

### 프로젝트 구성하기(Set up the project)

프로젝트 구성하기는 [링크](https://github.com/r4tings/r4tings-recommender#프로젝트-구성하기set-up-the-project) 를 참고하세요

## 데이터셋 준비하기(Prepare Dataset)

### 공개 데이터셋 내려받기(Download Public Datasets)

예제 테스트 클래스인 [**DatasetPrepareTest**](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java) 클래스의 테스트 메서드인 downloadPublicDatasets를 실행하여 외부 데이터셋을 내려받고 압축을 해제합니다.

* [downloadPublicDatasets](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java#L78)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 downloadPublicDatasets를 실행하고 실행 결과를 살펴봅니다.

```powershell
PS C:\r4tings\r4tings-recommender> ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.downloadPublicDatasets
```

https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e


Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 downloadPublicDatasets 실행 후, R4tings Recommender 오픈소스 추천엔진의 dataset 디렉토리 구조는 다음과 같습니다

```
C:\r4tings
   └── r4tings-recommender
       ├── dataset                                 <- 예제 데이터셋 
       │   │
       │   ├── Book-Crossing                       <- Book-Crossing 데이터셋
       │   │   ├── BX-Book-Ratings.csv             <- 도서-평점 데이터
       │   │   ├── BX-Books.csv                    <- 도서 데이터
       │   │   ├── BX-Users.csv                    <- 사용자 데이터
       │   │   └── BX-CSV-Dump.zip                 <- Book-Crossing 데이터셋 압축 파일
       │   │
       │   ├── MovieLens                           <- MovieLens 데이터셋
       │   │   ├── ml-coursera                     <- MovieLens Coursera 예제 데이터셋 
       │   │   ├── ml-latest                       <- MovieLens Latest 데이터셋 
       │   │   ├── ml-latest-samll                 <- MovieLens Latest(Small) 데이터셋   
       │   │   ├── ml-latest.zip                   <- MovieLens Latest 데이터셋 압축 파일
       │   │   └── ml-latest-samll.zip             <- MovieLens Latest(Small) 데이터셋 압축 파일
       │   │
       │   └── r4tings                             <- r4tings 데이터셋
       │
       └── ⋯ -일부 생략 -  
```

### Book-Crossing 데이터셋 Parquet 유형으로 변환하기

CSV 파일 형식의 Book-Crossing 데이터셋을 로드하여 Parquet 형식으로 저장합니다.

> Apache Spark는 CSV와 Parquet 파일 형식 모두 지원하지만, 여기에서는 Raw 데이터를 전처리하기 위해 CSV 형식의 데이터셋 파일을 읽어들여 Parquet 형식으로 변환합니다.

예제 테스트 클래스인 [**DatasetPrepareTest**](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java) 클래스의 테스트 메서드인 bookCrossingDataset 실행 결과를 살펴봅니다.

* [bookCrossingDataset](/recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java#L102)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 bookCrossingDataset 실행해 봅니다.

```powershell
PS C:\r4tings\r4tings-recommender> ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.bookCrossingDataset
```

https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/b0079e57-6d14-48e8-8d95-ecd2064c462e

Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 bookCrossingDatasetExamples 실행 후, R4tings Recommender 오픈소스 추천엔진의 dataset 디렉토리 구조는 다음과 같습니다.

```
C:\r4tings
   └── r4tings-recommender
       ├── dataset                                 <- 예제 데이터셋 
       │   │
       │   ├── Book-Crossing                       <- Book-Crossing 데이터셋
       │   │   ├── BX-Book-Ratings.parquet         <- 도서-평점 데이터 (Parquet 형식)
       │   │   ├── BX-Books.parquet                <- 도서 데이터 (Parquet 형식)
       │   │   ├── BX-Book-Ratings.csv             <- 도서-평점 데이터
       │   │   ├── BX-Books.csv                    <- 도서 데이터
       │   │   ├── BX-Users.csv                    <- 사용자 데이터
       │   │   └── BX-CSV-Dump.zip                 <- Book-Crossing 데이터셋 압축 파일
       │   │
       │   └── ⋯ -일부 생략 -       
       │
       └── ⋯ -일부 생략 -  
```

## 테스트 실행하기(Executing Tests)

Windows OS에서의 전체 예제 실행은 [`getting-started-recommender-on-windows.ps1`](/recommender/getting-started-recommender-on-windows.ps1) 를 참고하세요.

```powershell
##############################################################################
# 평점 정규화(Normalize rating)
##############################################################################

# 평균 중심 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.MeanCenteringNormalizerTest.testWithExample

# Z점수 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.ZScoreNormalizerTest.testWithExample

# 최소-최대 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.MinMaxNormalizerTest.testWithExample

# 소수 자릿수 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.DecimalScalingNormalizerTest.testWithExample

# 이진 임계 이진화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.ThresholdBinarizerTest.testWithExample

##############################################################################
# 유사도 계산(Calculate similarity)
##############################################################################

# 코사인 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.CosineSimilarityMeasurerTest.testWithExample

# 피어슨 상관계수와 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.PearsonSimilarityMeasurerTest.testWithExample

# 거리와 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.ManhattanSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.EuclideanSimilarityMeasurerTest.testWithExample

# 이진 속성과 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.SimpleMatchingSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.JaccardSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.ExtendedJaccardSimilarityMeasurerTest.testWithExample

##############################################################################
# 평점 예측과 아이템 추천(Recommend top-N items with highest rating prediction)
##############################################################################

# 이웃 기반 협업 필터링 추천
./gradlew :recommender:test --tests com.r4tings.recommender.model.knn.KNearestNeighborsTest.testWithExample

# 특잇값 분해 기반 협업 필터링 추천 
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.baseline.SimpleMeanRatingBaselineTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.baseline.GeneralMeanRatingBaselineTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.mf.BaselineSingleValueDecompositionTest.testWithExample

# TF-IDF 기반 콘텐츠 기반 필터링 추천 
./gradlew :recommender:test --tests com.r4tings.recommender.model.tfidf.TermFrequencyInverseDocumentFrequencyTest.testWithExample

# 연관규칙 기반 추천 
./gradlew :recommender:test --tests com.r4tings.recommender.model.arm.AssociationRuleMiningTest.testWithExample
```

<br/>

<div align="right">
   <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a>
</div>
