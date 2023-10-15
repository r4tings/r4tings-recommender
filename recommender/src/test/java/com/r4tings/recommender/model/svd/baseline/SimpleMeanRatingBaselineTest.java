/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.baseline;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.common.util.VerboseUtils;
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
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class SimpleMeanRatingBaselineTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({
    "dataset/r4tings/ratings.csv, , , , true, 'u4, i1, 0.3333333, -0.8333333, 2.5, 0.5'",
  })
  @interface SimpleMeanRatingBaselineCsvSource {}

  @SimpleMeanRatingBaselineCsvSource
  @DisplayName("SimpleMeanRatingBaseline")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
      String userCol,
      String itemCol,
      String ratingCol,
      Boolean verbose,
      @ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    CommonEstimator baseline =
        new SimpleMeanRatingBaseline()
            .setVerbose(verbose)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    MeanRatingBaselineModel baselineModel = (MeanRatingBaselineModel) baseline.fit(ratingDS);

    Dataset<Row> baselineRatingDS = baselineModel.transform(ratingDS);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
      testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
      VerboseUtils.showPivot(
          ratingDS, baseline.getUserCol(), baseline.getItemCol(), baseline.getRatingCol(), 1);

      testReporter.publishEntry("baselineRatingDS.count", String.valueOf(ratingDS.count()));
      testReporter.publishEntry("baselineRatingDS.schema", ratingDS.schema().simpleString());
      VerboseUtils.showPivot(
          baselineRatingDS, baseline.getUserCol(), baseline.getItemCol(), COL.RESIDUAL, 1);
    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    Row result =
        baselineRatingDS
            .where(
                col(baseline.getUserCol())
                    .equalTo(expects[0])
                    .and(col(baseline.getItemCol()).equalTo(expects[1])))
            .select(COL.USER_BIAS, COL.ITEM_BIAS, baseline.getOutputCol(), COL.RATING, COL.RESIDUAL)
            .head();

    double actual = result.getAs(COL.RESIDUAL);

    testReporter.publishEntry(
        COL.RESIDUAL,
        String.format("%,.7f [%s]\n%s", actual, actual, result /* result.prettyJson()*/));

    assertAll(
        //  "TODO: heading",
        () -> assertEquals(Double.parseDouble(expects[2]), result.getAs(COL.USER_BIAS), 1.0e-7),
        () -> assertEquals(Double.parseDouble(expects[3]), result.getAs(COL.ITEM_BIAS), 1.0e-7),
        () ->
            assertEquals(
                Double.parseDouble(expects[4]), result.getAs(baseline.getOutputCol()), 1.0e-7),
        () -> assertEquals(Double.parseDouble(expects[5]), actual, 1.0e-7));
  }
}
