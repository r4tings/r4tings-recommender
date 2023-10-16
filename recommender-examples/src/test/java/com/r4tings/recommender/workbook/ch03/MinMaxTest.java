/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch03;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.MinMaxNormalizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinMaxTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',     , 1d, 5d, true, 'u4, i1, 3.2222222'",
    "'dataset/r4tings, ratings.parquet', USER, 1d, 5d, true, 'u4, i1, 3.2222222'",
    "'dataset/r4tings, ratings.parquet', ITEM, 1d, 5d, true, 'u4, i1, 5d       '",
  })
  void minMaxExamples(
      @ConvertPathString String path,
      Group group,
      Double lower,
      Double upper,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    MinMaxNormalizer normalizer =
        new MinMaxNormalizer().setGroup(group).setLower(lower).setUpper(upper).setVerbose(verbose);

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
