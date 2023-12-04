/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.util.VerboseUtils;
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
class ManhattanSimilarityMeasurerTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({
    "dataset/r4tings/ratings.csv, USER, true, , , , , , 'u4, u5, 0.25'",
    "dataset/r4tings/ratings.csv, ITEM, true, , , , , , 'i3, i1, 0.5 '",
  })
  @interface ManhattanSimilarityCsvSource {}

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvFileSource(
      resources = "/com/r4tings/recommender/model/measures/similarity/manhattan_similarity.csv",
      numLinesToSkip = 1)
  @interface ManhattanSimilarityCsvFileSource {}

  // @ManhattanSimilarityCsvSource
  @ManhattanSimilarityCsvFileSource
  @Tag("Similarity")
  @DisplayName("Manhattan Similarity")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
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

    ManhattanSimilarityMeasurer measurer =
        new ManhattanSimilarityMeasurer()
            .setGroup(group)
            .setVerbose(verbose)
            .setImputeZero(imputeZero)
            .setIds(ids)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(measurer.explainParams());

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
      testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
      showPivot(ratingDS, measurer.getUserCol(), measurer.getItemCol(), measurer.getRatingCol(), 7);
    }

    Dataset<Row> similarityDS = measurer.transform(ratingDS);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("similarityDS.count", String.valueOf(similarityDS.count()));
      testReporter.publishEntry("similarityDS.schema", similarityDS.schema().simpleString());
      VerboseUtils.showPivot(similarityDS, COL.RHS, COL.LHS, measurer.getOutputCol(), 7);
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

    testReporter.publishEntry("actual", String.format("%.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[2]), actual, 1.0e-7);
  }
}
