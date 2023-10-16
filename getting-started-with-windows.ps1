
# Get Started with Windows

cd /
Remove-Item -path /r4tings -recurse -confirm
mkdir r4tings
cd r4tings
Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip
Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath .
Rename-Item -Path r4tings-recommender-main -NewName r4tings-recommender
cd r4tings-recommender
ls
./gradlew clean build -x test

######################################
# ���� �����ͼ� ���� (�����غ�)          # 
######################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.downloadPublicDatasets
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.bookCrossingDataset
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.movieLensDataset
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch02.DatasetPrepareTest.r4tingsDataset

######################################
# ���� ����ȭ                          # 
######################################

# ��� �߽� ����ȭ
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MeanCenteringTest.meanCenteringExamples

# Z���� ����ȭ
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.ZScoreTest.zScoreExamples

# �ּ�-�ִ� ����ȭ
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.MinMaxTest.minMaxExamples

# �Ҽ� �ڸ��� ����ȭ
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.DecimalScalingTest.decimalScalingExamples

# ���� �Ӱ� ����ȭ
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch03.BinaryThresholdingTest.binaryThresholdingExamples

######################################
# ���絵 ���                          # 
######################################

# �ڻ��� ���絵
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.CosineSimilarityTest.cosineSimilarityExamples

# �Ǿ �������� ���絵
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.PearsonSimilarityTest.pearsonSimilarityExamples

# ��Ŭ���� �Ÿ��� ���絵
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.EuclideanSimilarityTest.euclideanSimilarityExamples

# ���� �Ӽ��� ���絵
./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch04.binary.ExtendedJaccardSimilarityTest.extendedJaccardSimilarityExamples

######################################
# �̿� ��� ���� ���͸� ��õ             # 
######################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch05.KNearestNeighborsTest.kNearestNeighborsExamples

######################################
# Ư�հ� ���� ��� ���� ���͸� ��õ       # 
######################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch06.BaselineSingleValueDecompositionTest.baselineSingleValueDecompositionExamples

######################################
# TF-IDF ��� ������ ��� ���͸� ��õ    # 
######################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch07.TermFrequencyInverseDocumentFrequencyTest.termFrequencyInverseDocumentFrequencyExamples

######################################
# ������Ģ ��� ��õ                    # 
######################################

./gradlew :recommender-examples:test --tests com.r4tings.recommender.examples.ch08.AssociationRuleMiningTest.associationRuleMiningExamples