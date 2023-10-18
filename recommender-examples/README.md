# R4tings Recommender Examples

https://github.com/r4tings/r4tings-recommender-examples/assets/31362557/6be8f7fb-6a81-468f-b5b3-39fe5943f64d

## 프로젝트 구성하기(Set up the project)

프로젝트 구성하기는 [링크](/Readme.md#프로젝트-구성하기set-up-the-project) 를 참고하세요

## 데이터셋 준비하기(Prepare Dataset)

예제 테스트 클래스인 DatasetPrepareTest 클래스의 테스트 메서드인 r4tingsDataset 실행 결과를 살펴봅니다.

* [r4tingsDatasetExamples](./recommender-examples/src/test/java/com/r4tings/recommender/examples/ch02/DatasetPrepareTest.java#L43)

다음과 같이 명령줄 인터페이스(CLI, Command line interface)에서 빌드 도구인 Gradle Wrapper로 DatasetPrepareTest 클래스의 테스트 메서드인 r4tingsDataset 실행해 봅니다.

```
PS C:\r4tings\r4tings-recommender> ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.r4tingsDataset
```

https://github.com/r4tings/r4tings-recommender-examples/assets/123946859/fce48a50-2503-4e76-ad09-619319fe829a

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

Windows OS에서의 전체 예제 실행은 [`getting-started-recommender-examples-with-windows.ps1`](/recommender-examples/getting-started-recommender-examples-with-windows.ps1) 를 참고하세요.

```powershell
##############################################################################
# 평점 정규화(Normalize rating)
##############################################################################

# 평균 중심 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples

# Z점수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples

# 최소-최대 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples

# 소수 자릿수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples

# 이진 임계 이진화
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


