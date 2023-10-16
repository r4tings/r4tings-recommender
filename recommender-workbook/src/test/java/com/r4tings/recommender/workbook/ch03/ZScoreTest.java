/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch03;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.ZScoreNormalizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZScoreTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',     , true, 'u4, i1,  0d       '",
    "'dataset/r4tings, ratings.parquet', USER, true, 'u4, i1, -0.2122382'",
    "'dataset/r4tings, ratings.parquet', ITEM, true, 'u4, i1,  0.5773503'",
  })
  void zScoreExamples(
      @ConvertPathString String path,
      Group group,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    ZScoreNormalizer normalizer = new ZScoreNormalizer().setGroup(group).setVerbose(verbose);

    Dataset<Row> normalizedRatingDS = normalizer.transform(ratingDS);

    double actual =
        normalizedRatingDS
            .where(
                col(normalizer.getUserCol())
                    .equalTo(expectations[0])
                    .and(col(normalizer.getItemCol()).equalTo(expectations[1])))
            .select(normalizer.getOutputCol())
            .head()
            .getDouble(0);

    assertEquals(Double.parseDouble(expectations[2]), actual, 1.0e-7);
  }
}
