package com.r4tings.recommender.workbook.ch04;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.measures.similarity.EuclideanSimilarityMeasurer;
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
public class EuclideanSimilarityTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet', , USER,   , , true, 'u4, u5, 0.309017 '",
    "'dataset/r4tings, ratings.parquet', , USER, 10, , true, 'u4, u5, 0.1666667'",
    "'dataset/r4tings, ratings.parquet', , ITEM,   , , true, 'i3, i1, 0.5857864'",
    "'dataset/r4tings, ratings.parquet', , ITEM,  5, , true, 'i3, i1, 0.4721359'",
  })
  void euclideanSimilarityExamples(
      @ConvertPathString String path,
      @ConvertRatingNormalizer RatingNormalizer normalizer,
      Group group,
      Integer weight,
      Boolean imputeZero,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    if (Objects.nonNull(normalizer)) {
      ratingDS = normalizer.transform(ratingDS);
    }

    EuclideanSimilarityMeasurer measurer =
        new EuclideanSimilarityMeasurer()
            .setGroup(group)
            .setImputeZero(imputeZero)
            .setVerbose(verbose);

    if (Objects.nonNull(weight)) {
      measurer.setWeight(weight);
    }

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
