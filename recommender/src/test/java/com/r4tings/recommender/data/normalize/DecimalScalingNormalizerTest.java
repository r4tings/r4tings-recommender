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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class DecimalScalingNormalizerTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({"dataset/r4tings/ratings.csv, , true, , , , 'u4, i1, 0.3'"})
  @interface DecimalScalingCsvSource {}

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvFileSource(
      resources = "/com/r4tings/recommender/data/normalize/decimal_scaling.csv",
      numLinesToSkip = 1)
  @interface DecimalScalingCsvFileSource {}

  //  @DecimalScalingCsvSource
  @DecimalScalingCsvFileSource
  @Tag("Normalization")
  @DisplayName("Decimal Scaling")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
      Group group,
      Boolean verbose,
      String userCol,
      String itemCol,
      String ratingCol,
      @ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    DecimalScalingNormalizer normalizer =
        new DecimalScalingNormalizer()
            .setGroup(group)
            .setVerbose(verbose)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(normalizer.explainParams());

    if (Objects.isNull(group)) {
      Dataset<Row> normalizedDS = normalizer.transform(ratingDS);

      if (Objects.equals(verbose, Boolean.TRUE)) {
        testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
        testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
        showPivot(
            ratingDS,
            normalizer.getUserCol(),
            normalizer.getItemCol(),
            normalizer.getRatingCol(),
            7);

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

      testReporter.publishEntry("actual", String.format("%.7f [%s]", actual, actual));

      assertEquals(Double.parseDouble(expects[2]), actual, 1.0e-7);
    } else {
      UnsupportedOperationException exception =
          assertThrows(UnsupportedOperationException.class, () -> normalizer.transform(ratingDS));

      testReporter.publishEntry("exception", exception.getMessage());

      assertEquals(expects[0], exception.getMessage());
    }
  }
}
