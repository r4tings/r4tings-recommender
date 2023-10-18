
../getting-started-r4tings-recommender-on-windows.ps1

Write-Host -Foregroundcolor black -backgroundcolor white "`n Get started recommender on windows"

#########################
# 데이터셋 준비
#########################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.downloadPublicDatasets
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.bookCrossingDataset
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.movieLensDataset

#########################
# 평점 정규화
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize rating"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Mean Centering"

pause

# 평균 중심 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.MeanCenteringNormalizerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Z-Score"

pause

# Z점수 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.ZScoreNormalizerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Min-Max"

pause

# 최소-최대 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.MinMaxNormalizerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Normalize by Decimal Scaling"

pause

# 소수 자릿수 정규화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.DecimalScalingNormalizerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Binarize by Binary Thresholding"

pause

# 이진 임계 이진화
./gradlew :recommender:test --tests com.r4tings.recommender.data.normalize.ThresholdBinarizerTest.testWithExample

#########################
# 유사도 계산
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate similarity"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Cosine Similarity"

pause

# 코사인 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.CosineSimilarityMeasurerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Pearson Similarity"

pause

# 피어슨 상관계수와 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.PearsonSimilarityMeasurerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Distance & Similarity"

pause

# 거리와 유사도
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.ManhattanSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.EuclideanSimilarityMeasurerTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Calculate Extended Jaccard Similarity"

pause

# 이진 속성과 유사도 (확장 자카드 계수)
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.SimpleMatchingSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.JaccardSimilarityMeasurerTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.measures.similarity.binary.ExtendedJaccardSimilarityMeasurerTest.testWithExample

#########################
# 평점 예측과 아이템 추천
#########################

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Recommend top-N items with highest rating prediction"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Collaborative Filtering (K-Nearest-Neighbors)"

pause

# 이웃 기반 협업 필터링
./gradlew :recommender:test --tests com.r4tings.recommender.model.knn.KNearestNeighborsTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Collaborative Filtering (SVD)"

pause

# 특잇값 분해 기반 협업 필터링
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.baseline.SimpleMeanRatingBaselineTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.baseline.GeneralMeanRatingBaselineTest.testWithExample
./gradlew :recommender:test --tests com.r4tings.recommender.model.svd.mf.BaselineSingleValueDecompositionTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Content-based Filtering (TF-IDF)"

pause

# TF-IDF 콘텐츠 기반 필터링
./gradlew :recommender:test --tests com.r4tings.recommender.model.tfidf.TermFrequencyInverseDocumentFrequencyTest.testWithExample

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Association Rule Mining"

pause

# 연관규칙 기반
./gradlew :recommender:test --tests com.r4tings.recommender.model.arm.AssociationRuleMiningTest.testWithExample

pause