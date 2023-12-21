##############################################################################
# 데이터셋 살펴보기(Explore datasets)
##############################################################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.downloadPublicDatasets > docs/workbook/ch-02-sec-01a.txt

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.bookCrossingDataset > docs/workbook/ch-02-sec-01b.txt

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.movieLensDataset > docs/workbook/ch-02-sec-02.txt

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.r4tingsDataset > docs/workbook/ch-02-sec-03.txt

Rscript recommender-examples/src/test/R/ch02/DatasetPrepareTest.R > docs/workbook/ch-02.R.txt

##############################################################################
# 평점 정규화(Normalize rating)
##############################################################################

# 평균 중심 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples > docs/workbook/ch-03-sec-02.txt

Rscript recommender-examples/src/test/R/ch03/MeanCenteringTest.R > docs/workbook/ch-03-sec-02.R.txt

# Z점수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples > docs/workbook/ch-03-sec-03.txt

Rscript recommender-examples/src/test/R/ch03/ZScoreTest.R > docs/workbook/ch-03-sec-03.R.txt

# 최소-최대 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples > docs/workbook/ch-03-sec-04.txt

Rscript recommender-examples/src/test/R/ch03/MinMaxTest.R > docs/workbook/ch-03-sec-04.R.txt

# 소수 스케일링 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples > docs/workbook/ch-03-sec-05.txt

Rscript recommender-examples/src/test/R/ch03/DecimalScalingTest.R > docs/workbook/ch-03-sec-05.R.txt

# 이진 임계 이진화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.BinaryThresholdingTest.binaryThresholdingExamples > docs/workbook/ch-03-sec-06.txt

Rscript recommender-examples/src/test/R/ch03/BinaryThresholdingTest.R > docs/workbook/ch-03-sec-06.R.txt

##############################################################################
# 유사도 계산(Calculate similarity)
##############################################################################

# 코사인 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.CosineSimilarityTest.cosineSimilarityExamples > docs/workbook/ch-04-sec-02.txt

Rscript recommender-examples/src/test/R/ch04/CosineSimilarity.R > docs/workbook/ch-04-sec-02.R.txt

# 피어슨 상관계수와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.PearsonSimilarityTest.pearsonSimilarityExamples > docs/workbook/ch-04-sec-03.txt

Rscript recommender-examples/src/test/R/ch04/PearsonSimilarity.R > docs/workbook/ch-04-sec-03.R.txt

# 유클리드 거리와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples > docs/workbook/ch-04-sec-04.txt

Rscript recommender-examples/src/test/R/ch04/EuclideanSimilarity.R > docs/workbook/ch-04-sec-04.R.txt

# 이진 속성과 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples > docs/workbook/ch-04-sec-05.txt

Rscript recommender-examples/src/test/R/ch04/ExtendedJaccardSimilarity.R > docs/workbook/ch-04-sec-05.R.txt

##############################################################################
# 평점 예측과 아이템 추천(Recommend top-N items with highest rating prediction)
##############################################################################

# 이웃 기반 협업 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch05.KNearestNeighborsTest.kNearestNeighborsExamples > docs/workbook/ch-05.txt

Rscript recommender-examples/src/test/R/ch05/KNearestNeighbors.R > docs/workbook/ch-05.R.txt

# 특잇값 분해 기반 협업 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples > docs/workbook/ch-06.txt

# TF-IDF 기반 콘텐츠 기반 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples > docs/workbook/ch-07.txt

# 연관규칙 기반 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch08.AssociationRuleMiningTest.associationRuleMiningExamples > docs/workbook/ch-08.txt