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

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class AssociationRuleMiningTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet', LIFT, 0.5, 0.5, 10, true, 'i3, 1, i7, 0.6, 0.75, 1.25, 1.6, 0.12'",
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

    Row row =
        recommendedItemDS
            .where(
                col(COL.RANK)
                    .equalTo(expectations[1])
                    .and(col(params.getItemCol()).equalTo(expectations[2])))
            .head();

    List<String> measureList =
        Arrays.asList(COL.SUPPORT, COL.CONFIDENCE, COL.LIFT, COL.CONVICTION, COL.LEVERAGE);

    IntStream.range(0, measureList.size())
        .forEach(
            idx -> {
              String measure = measureList.get(idx);
              double actual = (double) row.getAs(measure);
              log.info("{} {}", measure, String.format("%.7f [%s]", actual, actual));
              assertEquals(Double.parseDouble(expectations[idx + 3]), actual, 1.0e-4);
            });
  }
}
