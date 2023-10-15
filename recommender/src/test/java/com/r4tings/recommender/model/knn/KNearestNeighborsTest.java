/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.knn;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.measures.similarity.RatingSimilarityMeasurer;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class KNearestNeighborsTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvSource({
    "dataset/r4tings/ratings.csv,                               , 'USER, COSINE, ,     ', USER,         SIMPLE, 3, 5, true, 'u4, 3, i3, 2.8251491'",
    "dataset/r4tings/ratings.csv, 'USER, MEAN_CENTERING, , , , ', 'USER, COSINE, ,     ', USER, MEAN_CENTERING, 3, 5, true, 'u4, 3, i3, 1.9600040'",
    "dataset/r4tings/ratings.csv, 'USER, MEAN_CENTERING, , , , ', 'USER, COSINE, , true', USER, MEAN_CENTERING, 3, 5, true, 'u4, 3, i3, 1.9600040'",
    "dataset/r4tings/ratings.csv, 'USER,        Z_SCORE, , , , ', 'USER, COSINE, ,     ', USER,        Z_SCORE, 3, 5, true, 'u4, 3, i3, 2.1836194'",
    "dataset/r4tings/ratings.csv,                               , 'ITEM, COSINE, ,     ', ITEM,         SIMPLE, 3, 5, true, 'u4, 3, i3, 3.6744673'",
    "dataset/r4tings/ratings.csv, 'ITEM, MEAN_CENTERING, , , , ', 'ITEM, COSINE, ,     ', ITEM, MEAN_CENTERING, 3, 5, true, 'u4, 1, i3, 3.1968382'",
    "dataset/r4tings/ratings.csv, 'ITEM, MEAN_CENTERING, , , , ', 'ITEM, COSINE, , true', ITEM, MEAN_CENTERING, 3, 5, true, 'u4, 1, i3, 3.1968382'",
    "dataset/r4tings/ratings.csv, 'ITEM,        Z_SCORE, , , , ', 'ITEM, COSINE, ,     ', ITEM,        Z_SCORE, 3, 5, true, 'u4, 1, i3, 3.3887394'",
  })
  @interface ExampleCsvSource {}

  @ExampleCsvSource
  @DisplayName("KNearestNeighbors")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratings,
      @ConvertRatingNormalizer RatingNormalizer normalizer,
      @ConvertRatingSimilarityMeasurer RatingSimilarityMeasurer measurer,
      Group group,
      WeightedAverage weightedAverage,
      Integer k,
      Integer topN,
      Boolean verbose,
      @AbstractSparkTests.ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    KNearestNeighborsParams params =
        new KNearestNeighborsParams()
            .setNormalizer(normalizer)
            .setSimilarityMeasurer(measurer)
            .setGroup(group)
            .setWeightedAverage(weightedAverage)
            .setK(k)
            .setMinimumNeighbors(2)
            .setVerbose(verbose);

    testReporter.publishEntry("expects", Arrays.toString(expects));

    testReporter.publishEntry("target user", expects[0]);

    Dataset<Row> recommendItems =
        new KNearestNeighbors(params).recommend(ratings, topN, expects[0]);

    log.info("recommendItems\n{}", recommendItems.showString(10, 0, false));

    testReporter.publishEntry("expected", expects[3]);

    double actual =
        recommendItems
            .where(
                col(COL.RANK).equalTo(expects[1]).and(col(params.getItemCol()).equalTo(expects[2])))
            .head()
            .getAs(params.getOutputCol());

    testReporter.publishEntry("actual", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[3]), actual, 1.0e-7);
  }
}
