/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import com.r4tings.recommender.common.ml.param.Group;
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

import static com.r4tings.recommender.common.util.VerboseUtils.showPivot;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class MinMaxNormalizerTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({
    "dataset/r4tings/ratings.csv,     , 1d, 5d, true, , , , 'u4, i1, 3.2222222'",
    "dataset/r4tings/ratings.csv, USER, 1d, 5d, true, , , , 'u4, i1, 3.2222222'",
    "dataset/r4tings/ratings.csv, ITEM, 1d, 5d, true, , , , 'u4, i1,         5'",
  })
  @interface MinMaxCsvSource {}

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvFileSource(
      resources = "/com/r4tings/recommender/data/normalize/min_max.csv",
      numLinesToSkip = 1)
  @interface MinMaxCsvFileSource {}

  // @MinMaxCsvSource
  @MinMaxCsvFileSource
  @Tag("Normalization")
  @DisplayName("Min-Max")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
      Group group,
      Double lower,
      Double upper,
      Boolean verbose,
      String userCol,
      String itemCol,
      String ratingCol,
      @ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    MinMaxNormalizer normalizer =
        new MinMaxNormalizer()
            .setGroup(group)
            .setLower(lower)
            .setUpper(upper)
            .setVerbose(verbose)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(normalizer.explainParams());

    Dataset<Row> normalizedDS = normalizer.transform(ratingDS);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
      testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
      showPivot(
          ratingDS, normalizer.getUserCol(), normalizer.getItemCol(), normalizer.getRatingCol(), 7);

      testReporter.publishEntry("normalizedDS.count", String.valueOf(normalizedDS.count()));
      testReporter.publishEntry("normalizedDS.schema", normalizedDS.schema().simpleString());
      showPivot(
          normalizedDS,
          normalizer.getUserCol(),
          normalizer.getItemCol(),
          normalizer.getOutputCol(),
          7);
    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    double actual =
        normalizedDS
            .where(
                col(normalizer.getUserCol())
                    .equalTo(expects[0])
                    .and(col(normalizer.getItemCol()).equalTo(expects[1])))
            .select(normalizer.getOutputCol())
            .head()
            .getDouble(0);

    testReporter.publishEntry("actual", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[2]), actual, 1.0e-7);
  }
}
