##############################################################################
# 평점 정규화(Normalize rating)
##############################################################################

# 평균 중심 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples > docs/results/MeanCenteringTest.txt

# Z점수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples > docs/results/ZScoreTest.txt

# 최소-최대 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples > docs/results/MinMaxTest.txt

# 소수 자릿수 정규화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples > docs/results/DecimalScalingTest.txt

# 이진 임계 이진화
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.BinaryThresholdingTest.binaryThresholdingExamples > docs/results/BinaryThresholdingTest.txt

##############################################################################
# 유사도 계산(Calculate similarity)
##############################################################################

# 코사인 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.CosineSimilarityTest.cosineSimilarityExamples > docs/results/CosineSimilarityTest.txt

# 피어슨 상관계수와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.PearsonSimilarityTest.pearsonSimilarityExamples > docs/results/PearsonSimilarityTest.txt

# 유클리드 거리와 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples > docs/results/EuclideanSimilarityTest.txt

# 이진 속성과 유사도
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples > docs/results/ExtendedJaccardSimilarityTest.txt

##############################################################################
# 평점 예측과 아이템 추천(Recommend top-N items with highest rating prediction)
##############################################################################

# 이웃 기반 협업 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch05.KNearestNeighborsTest.kNearestNeighborsExamples > docs/results/KNearestNeighborsTest.txt

# 특잇값 분해 기반 협업 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples > docs/results/BaselineSingleValueDecompositionTest.txt

# TF-IDF 기반 콘텐츠 기반 필터링 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples > docs/results/TermFrequencyInverseDocumentFrequencyTest.txt

# 연관규칙 기반 추천
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch08.AssociationRuleMiningTest.associationRuleMiningExamples > docs/results/AssociationRuleMiningTest.txt