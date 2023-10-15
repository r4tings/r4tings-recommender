/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.workbook.ch04;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.measures.similarity.PearsonSimilarityMeasurer;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class PearsonSimilarityTest extends AbstractSparkTests {

  /*
  상관분석은 집합이라 의미 없는듯 정규화
   */
  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet', , USER, , true, 'u4, u5, 1d'",
    "'dataset/r4tings, ratings.parquet', , ITEM, , true, 'i3, i1, 1d'",
  })
  void pearsonSimilarityExamples(
      @ConvertPathString String path,
      @ConvertRatingNormalizer RatingNormalizer normalizer,
      Group group,
      Boolean imputeZero,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    if (Objects.nonNull(normalizer)) {
      ratingDS = normalizer.transform(ratingDS);
    }

    PearsonSimilarityMeasurer measurer =
        new PearsonSimilarityMeasurer()
            .setGroup(group)
            .setImputeZero(imputeZero)
            .setVerbose(verbose);

    Dataset<Row> similarityDS = measurer.transform(ratingDS);

    double actual =
        similarityDS
            .where(
                (col(COL.LHS).equalTo(expectations[0]).and(col(COL.RHS).equalTo(expectations[1])))
                    .or(
                        col(COL.LHS)
                            .equalTo(expectations[1])
                            .and(col(COL.RHS).equalTo(expectations[0]))))
            .select(measurer.getOutputCol())
            .head()
            .getDouble(0);

    log.info("actual {}", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expectations[2]), actual, 1.0e-7);
  }
}
