/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch08;

import com.r4tings.recommender.model.arm.AssociationRuleMining;
import com.r4tings.recommender.model.arm.AssociationRuleMiningParams;
import com.r4tings.recommender.model.arm.InterestMeasure;
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
public class AssociationRuleMiningTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',    SUPPORT, 0.5, 0.5, 10, true, 'i3, 1, i2, 0.8     '",
    "'dataset/r4tings, ratings.parquet', CONFIDENCE, 0.5, 0.5, 10, true, 'i3, 1, i2, 1       '",
    "'dataset/r4tings, ratings.parquet',       LIFT, 0.5, 0.5, 10, true, 'i3, 2, i2, 1       '",
    "'dataset/r4tings, ratings.parquet',   LEVERAGE, 0.5, 0.5, 10, true, 'i3, 2, i2, 0       '",
    "'dataset/r4tings, ratings.parquet', CONVICTION, 0.5, 0.5, 10, true, 'i3, 1, i2, Infinity'",
  })
  void associationRuleMiningExamples(
      @ConvertPathString String path,
      InterestMeasure interestMeasure,
      Double minimumSupportThreshold,
      Double minimumConfidenceThreshold,
      Integer topN,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    AssociationRuleMiningParams params =
        new AssociationRuleMiningParams()
            .setMinSupport(minimumSupportThreshold)
            .setMinConfidence(minimumConfidenceThreshold)
            .setVerbose(verbose)
            .setOutputCol(interestMeasure.getDescription());

    AssociationRuleMining recommender = new AssociationRuleMining(params);

    Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, topN, expectations[0]);

    recommendedItemDS.show();

    double actual =
            (double) recommendedItemDS
                .where(
                    col(COL.RANK)
                        .equalTo(expectations[1])
                        .and(col(params.getItemCol()).equalTo(expectations[2])))
                .head()
                .getAs(params.getOutputCol());

    log.info("actual {}", String.format("%.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expectations[3]), actual, 1.0e-7);
  }
}
