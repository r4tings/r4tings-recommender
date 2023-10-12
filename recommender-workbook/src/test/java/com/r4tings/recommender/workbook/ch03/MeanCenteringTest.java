package com.r4tings.recommender.workbook.ch03;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.MeanCenteringNormalizer;
import com.r4tings.recommender.test.AbstractSparkTests;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeanCenteringTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet',     , true, 'u4, i1,  0d       '",
    "'dataset/r4tings, ratings.parquet', USER, true, 'u4, i1, -0.3333333'",
    "'dataset/r4tings, ratings.parquet', ITEM, true, 'u4, i1,  0.8333333'",
  })
  void meanCenteringExamples(
      @ConvertPathString String path,
      Group group,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    MeanCenteringNormalizer normalizer =
        new MeanCenteringNormalizer().setGroup(group).setVerbose(verbose);

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
