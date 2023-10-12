package com.r4tings.recommender.workbook.ch04.binary;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.measures.similarity.SimpleMatchingSimilarityMeasurer;
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
public class SimpleMatchingSimilarityTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet', ' , BINARY_THRESHOLDING, , , , 3d', USER, true, 'u4, u5, 0.25'",
    "'dataset/r4tings, ratings.parquet', ' , BINARY_THRESHOLDING, , , , 3d', ITEM, true, 'i3, i1, 0.6 '",
  })
  void simpleMatchingSimilarityExamples(
      @ConvertPathString String path,
      @ConvertRatingNormalizer RatingNormalizer binarizer,
      Group group,
      Boolean verbose,
      @ConvertStringArray String[] expectations) {

    Dataset<Row> ratingDS = spark.read().load(path);

    Dataset<Row> binarizedRatingDS = Objects.requireNonNull(binarizer).transform(ratingDS);

    SimpleMatchingSimilarityMeasurer measurer =
        new SimpleMatchingSimilarityMeasurer().setGroup(group).setVerbose(verbose);

    Dataset<Row> similarityDS = measurer.transform(binarizedRatingDS);

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
