
# Get Started with Windows

cd /
# Remove-Item -path /r4tings -recurse -confirm
mkdir r4tings
cd r4tings
Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip
Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath .
Rename-Item -Path r4tings-recommender-main -NewName r4tings-recommender
cd r4tings-recommender
ls
./gradlew clean build -x test

######################################
# 예제 데이터셋 구성 (사전준비)          # 
######################################

./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch02.DatasetPrepareTest.downloadPublicDatasets
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch02.DatasetPrepareTest.bookCrossingDataset
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch02.DatasetPrepareTest.movieLensDataset
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch02.DatasetPrepareTest.r4tingsDataset

######################################
# 평점 정규화                          # 
######################################

# 평균 중심 정규화
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch03.MeanCenteringTest.meanCenteringExamples

# Z점수 정규화
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch03.ZScoreTest.zScoreExamples

# 최소-최대 정규화
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch03.MinMaxTest.minMaxExamples

# 소수 자릿수 정규화
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch03.DecimalScalingTest.decimalScalingExamples

# 이진 임계 이진화
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch03.BinaryThresholdingTest.binaryThresholdingExamples

######################################
# 유사도 계산                          # 
######################################

# 코사인 유사도
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch04.CosineSimilarityTest.cosineSimilarityExamples

# 피어슨 상관계수와 유사도
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch04.PearsonSimilarityTest.pearsonSimilarityExamples

# 유클리드 거리와 유사도
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples

# 이진 속성과 유사도
./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples

######################################
# 이웃 기반 협업 필터링 추천             # 
######################################

./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch05.KNearestNeighborsTest.kNearestNeighborsExamples

######################################
# 특잇값 분해 기반 협업 필터링 추천       # 
######################################

./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples

######################################
# TF-IDF 기반 콘텐츠 기반 필터링 추천    # 
######################################

./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples

######################################
# 연관규칙 기반 추천                    # 
######################################

./gradlew :recommender-workbook:test --tests com.r4tings.recommender.workbook.ch08.AssociationRuleMiningTest.associationRuleMiningExamples