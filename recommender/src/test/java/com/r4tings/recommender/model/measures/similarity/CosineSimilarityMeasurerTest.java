/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.util.VerboseUtils;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.util.VerboseUtils.showPivot;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class CosineSimilarityMeasurerTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({
    "dataset/r4tings/ratings.csv,                               ,  USER, true,     , u4  , , , , 'u4, u5,  0.996473 '",
    "dataset/r4tings/ratings.csv,                               ,  ITEM, true,     , 'i1, i2, i4, i6, i8, i9'  , , , , 'i3, i1,  0.9936053'",
    "dataset/r4tings/ratings.csv,                               ,  USER, true,     , , , , ,'u4, u5,  0.996473 '",
    "dataset/r4tings/ratings.csv, 'USER, MEAN_CENTERING, , , , ',  USER, true,     , , , , ,'u4, u5, -0.4834227'",
    "dataset/r4tings/ratings.csv, 'USER, MEAN_CENTERING, , , , ',  USER, true, true, , , , ,'u4, u5, -0.1689656'",
    "dataset/r4tings/ratings.csv, 'USER,        Z_SCORE, , , , ',  USER, true,     , , , , ,'u4, u5, -0.4834227'",
    "dataset/r4tings/ratings.csv,                               ,  ITEM, true,     , , , , ,'i3, i1,  0.9936053'",
    "dataset/r4tings/ratings.csv, 'ITEM, MEAN_CENTERING, , , , ',  ITEM, true,     , , , , ,'i3, i1,  0.975441 '",
    "dataset/r4tings/ratings.csv, 'ITEM, MEAN_CENTERING, , , , ',  ITEM, true, true, , , , ,'i3, i1,  0.5705628'",
    "dataset/r4tings/ratings.csv, 'ITEM,        Z_SCORE, , , , ',  ITEM, true,     , , , , ,'i3, i1,  0.975441 '",
  })
  @interface CosineSimilarityCsvSource {}

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvFileSource(
      resources = "/com/r4tings/recommender/model/measures/similarity/cosine_similarity.csv",
      numLinesToSkip = 1)
  @interface CosineSimilarityCsvFileSource {}

  //  @CosineSimilarityCsvSource
  @CosineSimilarityCsvFileSource
  @Tag("Similarity")
  @DisplayName("Cosine")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
      @ConvertRatingNormalizer RatingNormalizer normalizer,
      Group group,
      Boolean verbose,
      Boolean imputeZero,
      @ConvertStringArray String[] ids,
      String userCol,
      String itemCol,
      String ratingCol,
      @AbstractSparkTests.ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    if (Objects.nonNull(normalizer)) {

      normalizer.setUserCol(userCol).setItemCol(itemCol).setRatingCol(ratingCol);

      log.info(normalizer.explainParams());

      if (Objects.equals(verbose, Boolean.TRUE)) {
        testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
        testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
        showPivot(
            ratingDS,
            normalizer.getUserCol(),
            normalizer.getItemCol(),
            normalizer.getRatingCol(),
            7);
      }

      ratingDS = normalizer.transform(ratingDS);

      if (Objects.equals(verbose, Boolean.TRUE)) {
        testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
        testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
        showPivot(
            ratingDS,
            normalizer.getUserCol(),
            normalizer.getItemCol(),
            normalizer.getOutputCol(),
            7);
      }
    }

    CosineSimilarityMeasurer measurer =
        new CosineSimilarityMeasurer()
            .setGroup(group)
            .setImputeZero(imputeZero)
            .setIds(ids)
            .setVerbose(verbose)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(measurer.explainParams());

    Dataset<Row> similarityDS = measurer.transform(ratingDS);

    similarityDS.explain();

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("similarityDS.count", String.valueOf(similarityDS.count()));
      testReporter.publishEntry("similarityDS.schema", similarityDS.schema().simpleString());
      // VerboseUtils.showPivot(similarityDS, COL.RHS, COL.LHS, measurer.getOutputCol(), 7);
      VerboseUtils.showPivot(similarityDS, COL.LHS, COL.RHS, measurer.getOutputCol(), 7);
    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    double actual =
        similarityDS
            .where(
                (col(COL.LHS).equalTo(expects[0]).and(col(COL.RHS).equalTo(expects[1])))
                    .or(col(COL.LHS).equalTo(expects[1]).and(col(COL.RHS).equalTo(expects[0]))))
            .select(measurer.getOutputCol())
            .head()
            .getDouble(0);

    testReporter.publishEntry("actual", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[2]), actual, 1.0e-7);
  }
}
