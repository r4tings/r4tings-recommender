/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.arm;

import com.r4tings.recommender.common.util.VerboseUtils;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class AssociationRuleMiningTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvSource({
    "'dataset/r4tings                  , ratings.csv,  items.csv',    SUPPORT, 0.2, 0.5,  true, 10,       ,        , , label, '  i3, 4,   i1, 0.4      '",
    "'dataset/r4tings                  , ratings.csv,  items.csv', CONFIDENCE, 0.2, 0.5,  true, 10,       ,        , , label, '  i3, 4,   i1, 0.5      '",
    "'dataset/r4tings                  , ratings.csv,  items.csv',       LIFT, 0.2, 0.5,  true, 10,       ,        , , label, '  i3, 6,   i1, 0.8333333'",
  })
  @interface AssociationRuleMiningCsvSource {}

  @AssociationRuleMiningCsvSource
  @Tag("Recommendation")
  @DisplayName("AssociationRuleMining")
  void testWithExample(
      @ConvertDatasetArray Dataset<Row>[] datasets,
      InterestMeasure interestMeasure,
      Double minimumSupportThreshold,
      Double minimumConfidenceThreshold,
      Boolean verbose,
      Integer topN,
      String userCol,
      String itemCol,
      String ratingCol,
      String labelCol,
      @AbstractSparkTests.ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    Dataset<Row> ratings = datasets[0];
    Dataset<Row> items = datasets[1];

    AssociationRuleMiningParams params =
        new AssociationRuleMiningParams()
            //        .setMinSupportCount(minSupportCount) // Optional clear
            .setMinSupport(minimumSupportThreshold) //  Optional clear
            .setMinConfidence(minimumConfidenceThreshold) //  Optional clear
            .setVerbose(verbose)
            .setOutputCol(interestMeasure.getDescription())
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(params.explainParams());

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS.count", String.valueOf(ratings.count()));
      testReporter.publishEntry("ratingDS.schema", ratings.schema().simpleString());
      VerboseUtils.showPivot(
          ratings, params.getUserCol(), params.getItemCol(), params.getRatingCol(), 7);
    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    testReporter.publishEntry("ids", expects[0]);

    Dataset<Row> recommendDS =
        new AssociationRuleMining(params).recommend(ratings, topN, expects[0]);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("recommendDS.count", String.valueOf(recommendDS.count()));
      testReporter.publishEntry("recommendDS.schema", recommendDS.schema().simpleString());
    }

    recommendDS
        .join(items.select(params.getItemCol(), labelCol), params.getItemCol())
        .orderBy(col(COL.RANK))
        .show(false);

    double actual =
        recommendDS
            .where(
                col(COL.RANK).equalTo(expects[1]).and(col(params.getItemCol()).equalTo(expects[2])))
            .head()
            .getAs(params.getOutputCol());

    testReporter.publishEntry("actual", String.format("%.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[3]), actual, 1.0e-7);
  }
}
