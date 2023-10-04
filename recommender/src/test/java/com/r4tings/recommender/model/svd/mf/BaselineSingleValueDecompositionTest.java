package com.r4tings.recommender.model.svd.mf;

import com.r4tings.recommender.common.util.VerboseUtils;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class BaselineSingleValueDecompositionTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "#{index} run with [{arguments}]")
  @CsvSource({
    "dataset/r4tings/ratings.csv, , , , true, 6, 'u4, i1, 3d       '",
    "dataset/r4tings/ratings.csv, , , , true, 5, 'u4, i1, 3d       '",
    "dataset/r4tings/ratings.csv, , , , true, 4, 'u4, i1, 3.0984428 '",
    "dataset/r4tings/ratings.csv, , , , true, 3, 'u4, i1, 1.3714937'",
    "dataset/r4tings/ratings.csv, , , , true, 2, 'u4, i1, 1.3422441'",
    "dataset/r4tings/ratings.csv, , , , true, 1, 'u4, i1, 1.4989139'",
  })
  @interface SingleValueDecompositionCsvSource {}

  @SingleValueDecompositionCsvSource
  @DisplayName("SingleValueDecomposition")
  void testWithExample(
      @ConvertDataset Dataset<Row> ratingDS,
      String userCol,
      String itemCol,
      String ratingCol,
      Boolean verbose,
      Integer dimension,
      @ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    SingleValueDecomposition svd =
        new SingleValueDecomposition()
            .setVerbose(verbose)
            .setK(dimension)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    SingleValueDecompositionModel baselineModel = svd.fit(ratingDS);

    Dataset<Row> reconstructedRatingDS = baselineModel.transform(ratingDS);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS", ratingDS.schema().simpleString());
      VerboseUtils.showPivot(ratingDS, COL.USER, COL.ITEM, COL.RATING, 7);

      testReporter.publishEntry(
          "reconstructedRatingDS", reconstructedRatingDS.schema().simpleString());
      VerboseUtils.showPivot(reconstructedRatingDS, COL.USER, COL.ITEM, COL.USV, 7);

      VerboseUtils.showPivot(reconstructedRatingDS, COL.USER, COL.ITEM, COL.RESIDUAL, 7);

      // TODO -> assert를 할까?
      log.warn(
          "{}",
          String.format(
              "%,.7f",
              reconstructedRatingDS.agg(round(avg(col(COL.RESIDUAL)), 7)).head().getDouble(0)));

      reconstructedRatingDS.show(true);
    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    double actual =
        reconstructedRatingDS
            .where(
                col(svd.getUserCol())
                    .equalTo(expects[0])
                    .and(col(svd.getItemCol()).equalTo(expects[1])))
            .select(svd.getOutputCol())
            .head()
            .getDouble(0);

    testReporter.publishEntry("actual", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[2]), actual, 1.0e-7);
  }
}
