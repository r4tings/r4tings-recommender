/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch03;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.ThresholdBinarizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BinaryThresholdingTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',     , 3d, true, '                                              u4, i1,  1d'",
    "'dataset/r4tings, ratings.parquet', USER, 3d, true, 'The requested operation is not supported. - user,   ,    '",
    "'dataset/r4tings, ratings.parquet', ITEM, 3d, true, 'The requested operation is not supported. - item,   ,    '",
  })
  void binaryThresholdingExamples(
      @ConvertPathString String path,
      Group group,
      Double threshold,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    ThresholdBinarizer binarizer =
        new ThresholdBinarizer().setGroup(group).setThreshold(threshold).setVerbose(verbose);

    if (Objects.isNull(group)) {
      Dataset<Row> binarizedRatingDS = binarizer.transform(ratingDS);

      double actual =
          binarizedRatingDS
              .where(
                  col(binarizer.getUserCol())
                      .equalTo(expectations[0])
                      .and(col(binarizer.getItemCol()).equalTo(expectations[1])))
              .select(binarizer.getOutputCol())
              .head()
              .getDouble(0);

      assertEquals(Double.parseDouble(expectations[2]), actual, 1.0e-4);
    } else {
      UnsupportedOperationException exception =
          assertThrows(UnsupportedOperationException.class, () -> binarizer.transform(ratingDS));

      assertEquals(expectations[0], exception.getMessage());
    }
  }
}
