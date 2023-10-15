/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.workbook.ch03;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.DecimalScalingNormalizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DecimalScalingTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',     , true, '                                              u4, i1,  0.3'",
    "'dataset/r4tings, ratings.parquet', USER, true, 'The requested operation is not supported. - user,   ,     '",
    "'dataset/r4tings, ratings.parquet', ITEM, true, 'The requested operation is not supported. - item,   ,     '",
  })
  void decimalScalingExamples(
      @ConvertPathString String path,
      Group group,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    DecimalScalingNormalizer normalizer =
        new DecimalScalingNormalizer().setGroup(group).setVerbose(verbose);

    if (Objects.isNull(group)) {
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
    } else {
      UnsupportedOperationException exception =
          assertThrows(UnsupportedOperationException.class, () -> normalizer.transform(ratingDS));

      assertEquals(expectations[0], exception.getMessage());
    }
  }
}
