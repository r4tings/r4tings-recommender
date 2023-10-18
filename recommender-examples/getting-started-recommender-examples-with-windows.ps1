
Write-Host -Foregroundcolor black -backgroundcolor white "`n Get started r4tings recommender examples with windows"
pause
Write-Host -Foregroundcolor black -backgroundcolor white "`n Deleting a exist project folder"
pause
Remove-Item -path /r4tings -recurse -confirm
Write-Host -Foregroundcolor black -backgroundcolor white "`n Setting up R4tings Recommender project"
pause

#########################
# 프로젝트 구성
#########################

cd /
mkdir r4tings
cd r4tings
Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip
Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath .
Rename-Item -Path r4tings-recommender-main -NewName r4tings-recommender
cd r4tings-recommender
ls
pwd

pause
Write-Host -Foregroundcolor black -backgroundcolor white "`n R4tings Recommender project requires java 11 to run"
java -version
pause
Write-Host -Foregroundcolor black -backgroundcolor white "`n Build gradle project"
pause
./gradlew clean build -x test
pause
Write-Host -Foregroundcolor black -backgroundcolor white "`n Prepare Datasets"
pause

#########################
# 데이터셋 준비
#########################

# ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.downloadPublicDatasets
# ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.bookCrossingDataset
# ./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.movieLensDataset
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.r4tingsDataset

#########################
# 평점 정규화
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize rating"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Mean Centering"

pause

# 평균 중심 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Z-Score"

pause

# Z점수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Min-Max"

pause

# 최소-최대 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Decimal Scaling"

pause

# 소수 자릿수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Binarize by Binary Thresholding"

pause

# 이진 임계 이진화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.BinaryThresholdingTest.binaryThresholdingExamples

#########################
# 유사도 계산
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate similarity"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Cosine Similarity"

pause

# 코사인 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.CosineSimilarityTest.cosineSimilarityExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Pearson Similarity"

pause

# 피어슨 상관계수와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.PearsonSimilarityTest.pearsonSimilarityExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Euclidean Similarity"

pause

# 유클리드 거리와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Extended Jaccard Similarity"

pause

# 이진 속성과 유사도 (확장 자카드 계수)
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples

#########################
# 평점 예측과 아이템 추천
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Recommend top-N items with highest rating prediction"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Collaborative Filtering (K-Nearest-Neighbors)"

pause

# 이웃 기반 협업 필터링
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch05.KNearestNeighborsTest.kNearestNeighborsExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Collaborative Filtering (SVD)"

pause

# 특잇값 분해 기반 협업 필터링
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Content-based Filtering (TF-IDF)"

pause

# TF-IDF 콘텐츠 기반 필터링
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Association Rule Mining"

pause

# 연관규칙 기반
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch08.AssociationRuleMiningTest.associationRuleMiningExamples

pause