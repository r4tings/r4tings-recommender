/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch06;

import com.r4tings.recommender.model.svd.BaselineSingleValueDecomposition;
import com.r4tings.recommender.model.svd.BaselineSingleValueDecompositionParams;
import com.r4tings.recommender.model.svd.baseline.GeneralMeanRatingBaseline;
import com.r4tings.recommender.model.svd.baseline.MeanRatingBaselineModel;
import com.r4tings.recommender.model.svd.baseline.SimpleMeanRatingBaseline;
import com.r4tings.recommender.model.svd.mf.SingleValueDecomposition;
import com.r4tings.recommender.model.svd.mf.SingleValueDecompositionModel;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.Estimator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class BaselineSingleValueDecompositionTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',  SIMPLE,   ,   , 3, 5, true, 'u4, i1,       2.5,        0.5,  0.5374888, u4, i3, 3, 2.6591122'",
    "'dataset/r4tings, ratings.parquet', GENERAL,  0,  0, 3, 5, true, 'u4, i1, 2.4666667,  0.5333333,  0.5590040, u4, i3, 3, 2.5964331'",
    "'dataset/r4tings, ratings.parquet', GENERAL, 25, 10, 3, 5, true, 'u4, i1, 3.0348793, -0.0348793, -0.6658965, u4, i3, 2, 3.1906588'",
    // Full SVD
    // "'dataset/r4tings, ratings.parquet',  SIMPLE,   ,   , 5, 5, true, 'u4, i1,       2.5,
    // 0.5,        0.5, u4, i3, 3, 2.7083333'",
    // "'dataset/r4tings, ratings.parquet', GENERAL,  0,  0, 5, 5, true, 'u4, i1, 2.4666667,
    // 0.5333333,  0.5333333, u4, i3, 3, 2.675    '",
    // "'dataset/r4tings, ratings.parquet', GENERAL, 25, 10, 5, 5, true, 'u4, i1, 3.0348793,
    // -0.0348793, -0.0348792, u4, i3, 3, 3.0379581'",
  })
  void baselineSingleValueDecompositionExamples(
      @ConvertPathString String path,
      String baselineType,
      Integer lambda2,
      Integer lambda3,
      Integer k,
      Integer topN,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    Estimator baseline;
    if ("SIMPLE".equalsIgnoreCase(baselineType)) {
      baseline = new SimpleMeanRatingBaseline().setVerbose(verbose);
    } else {
      baseline =
          new GeneralMeanRatingBaseline()
              .setLambda2(lambda2)
              .setLambda3(lambda3)
              .setVerbose(verbose);
    }

    MeanRatingBaselineModel baselineModel = (MeanRatingBaselineModel) baseline.fit(ratingDS);

    Dataset<Row> baselineRatingDS = baselineModel.transform(ratingDS);

    double actual1 =
        baselineRatingDS
            .where(
                col(baselineModel.getUserCol())
                    .equalTo(expectations[0])
                    .and(col(baselineModel.getItemCol()).equalTo(expectations[1])))
            .head()
            .getAs(baselineModel.getOutputCol());

    assertEquals(Double.parseDouble(expectations[2]), actual1, 1.0e-7);

    double actual2 =
        baselineRatingDS
            .where(
                col(baselineModel.getUserCol())
                    .equalTo(expectations[0])
                    .and(col(baselineModel.getItemCol()).equalTo(expectations[1])))
            .head()
            .getAs(baselineModel.getResidualCol());

    assertEquals(Double.parseDouble(expectations[3]), actual2, 1.0e-7);

    SingleValueDecomposition svd =
        new SingleValueDecomposition().setK(k).setVerbose(verbose).setRatingCol(COL.RESIDUAL);

    SingleValueDecompositionModel svdModel = svd.fit(baselineRatingDS);

    Dataset<Row> reconstructedRatingDS = svdModel.transform(baselineRatingDS);

    double actual3 =
        reconstructedRatingDS
            .where(
                col(baselineModel.getUserCol())
                    .equalTo(expectations[0])
                    .and(col(baselineModel.getItemCol()).equalTo(expectations[1])))
            .head()
            .getAs(svdModel.getOutputCol());

    assertEquals(Double.parseDouble(expectations[4]), actual3, 1.0e-7);

    BaselineSingleValueDecompositionParams params =
        new BaselineSingleValueDecompositionParams()
            .setBaselineModel(baselineModel)
            .setSingleValueDecompositionModel(svdModel)
            .setVerbose(verbose);

    BaselineSingleValueDecomposition recommender = new BaselineSingleValueDecomposition(params);

    Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, topN, expectations[5]);

    recommendedItemDS.show();

    double actual4 =
        recommendedItemDS
            .where(
                col(params.getItemCol())
                    .equalTo(expectations[6])
                    .and(col(COL.RANK).equalTo(expectations[7])))
            .head()
            .getAs(params.getOutputCol());

    log.info("actual3 {}", String.format("%,.7f [%s]", actual4, actual4));

    assertEquals(Double.parseDouble(expectations[8]), actual4, 1.0e-7);
  }
}
