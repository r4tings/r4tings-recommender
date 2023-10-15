/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.workbook.ch05;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.knn.KNearestNeighbors;
import com.r4tings.recommender.model.knn.KNearestNeighborsParams;
import com.r4tings.recommender.model.knn.WeightedAverage;
import com.r4tings.recommender.model.measures.similarity.RatingSimilarityMeasurer;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class KNearestNeighborsTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',                              , 'USER, COSINE,  false, false', USER,         SIMPLE, 3, 2, 5, true, 'u4, i3, 3, 2.825149 '",
    "'dataset/r4tings, ratings.parquet', 'USER, MEAN_CENTERING, false', 'USER, COSINE,  false, false', USER, MEAN_CENTERING, 3, 2, 5, true, 'u4, i3, 3, 1.960004 '",
    "'dataset/r4tings, ratings.parquet', 'USER,        Z_SCORE, false', 'USER, COSINE,  false, false', USER,        Z_SCORE, 3, 2, 5, true, 'u4, i3, 3, 2.1836194'",
    "'dataset/r4tings, ratings.parquet',                              , 'ITEM, COSINE,  false, false', ITEM,         SIMPLE, 3, 2, 5, true, 'u4, i3, 3, 3.6744673'",
    "'dataset/r4tings, ratings.parquet', 'ITEM, MEAN_CENTERING, false', 'ITEM, COSINE,  false, false', ITEM, MEAN_CENTERING, 3, 2, 5, true, 'u4, i3, 1, 3.1968382'",
    "'dataset/r4tings, ratings.parquet', 'ITEM,        Z_SCORE, false', 'ITEM, COSINE,  false, false', ITEM,        Z_SCORE, 3, 2, 5, true, 'u4, i3, 1, 3.3887394'",
  })
  void kNearestNeighborsExamples(
      @ConvertPathString String path,
      @ConvertRatingNormalizer RatingNormalizer normalizer,
      @ConvertRatingSimilarityMeasurer RatingSimilarityMeasurer measurer,
      Group group,
      WeightedAverage weightedAverage,
      Integer k,
      Integer minimumNeighbors,
      Integer topN,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    KNearestNeighborsParams params =
        new KNearestNeighborsParams()
            .setGroup(group)
            .setWeightedAverage(weightedAverage)
            .setK(k)
            .setMinimumNeighbors(minimumNeighbors)
            .setSimilarityMeasurer(measurer)
            .setNormalizer(normalizer)
            .setVerbose(verbose);

    KNearestNeighbors recommender = new KNearestNeighbors(params);

    Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, topN, expectations[0]);

    recommendedItemDS.show();

    double actual =
        recommendedItemDS
            .where(
                col(params.getItemCol())
                    .equalTo(expectations[1])
                    .and(col(COL.RANK).equalTo(expectations[2])))
            .head()
            .getAs(params.getOutputCol());

    log.info("actual {}", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expectations[3]), actual, 1.0e-7);
  }
}
