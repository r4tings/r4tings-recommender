package com.r4tings.recommender.model.tfidf;

import com.r4tings.recommender.common.util.VerboseUtils;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class TermFrequencyInverseDocumentFrequencyTest extends AbstractSparkTests {

  @Retention(RetentionPolicy.RUNTIME)
  @ParameterizedTest(name = "[{arguments}] #{index}")
  @CsvSource({
    "'dataset/r4tings                  , ratings.csv,    terms.csv,  items.csv', 'COSINE, true, false', 3.5,  5, true,       ,        , ,        , label, '   u4, 1,    i3,  0.1604979'",
    "'dataset/r4tings                  , ratings.csv,    terms.csv,  items.csv', 'COSINE,     , false', 3.5,  5, true,       ,        , ,        , label, '   u4, 1,   i10,  0.8319038'",
    "'dataset/r4tings                  , ratings.csv,    terms.csv,  items.csv', 'COSINE, true, false',    ,  5, true,       ,        , ,        , label, '   u4, 3,    i3, -0.0942485'",
    "'dataset/r4tings                  , ratings.csv,    terms.csv,  items.csv', 'COSINE,     , false',    ,  5, true,       ,        , ,        , label, '   u4, 4,    i3, -0.7517576'",
  })
  @interface TermFrequencyInverseDocumentFrequencyCsvSource {}

  @TermFrequencyInverseDocumentFrequencyCsvSource
  @Tag("Recommendation")
  @DisplayName("TermFrequencyInverseDocumentFrequency")
  void testWithExample(
      @ConvertDatasetArray Dataset<Row>[] datasets,
      @ConvertRatingSimilarityMeasure UserDefinedFunction similarityMeasure,
      Double threshold,
      Integer topN,
      Boolean verbose,
      String userCol,
      String itemCol,
      String ratingCol,
      String termCol,
      String labelCol,
      @AbstractSparkTests.ConvertStringArray String[] expects,
      ArgumentsAccessor arguments) {

    testReporter.publishEntry("arguments", Arrays.toString(arguments.toArray()));

    Dataset<Row> ratingDS = datasets[0];
    Dataset<Row> termDS = datasets[1];
    Dataset<Row> itemDS = datasets[2];

    TermFrequencyInverseDocumentFrequencyParams params =
        new TermFrequencyInverseDocumentFrequencyParams()
            .setVerbose(verbose)
            .setSimilarityMeasure(similarityMeasure)
            .setThreshold(threshold)
            .setTermCol(termCol)
            .setUserCol(userCol)
            .setItemCol(itemCol)
            .setRatingCol(ratingCol);

    log.info(params.explainParams());

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("ratingDS.count", String.valueOf(ratingDS.count()));
      testReporter.publishEntry("ratingDS.schema", ratingDS.schema().simpleString());
      VerboseUtils.showPivot(
          ratingDS, params.getUserCol(), params.getItemCol(), params.getRatingCol(), 1);

      testReporter.publishEntry("termDS.count", String.valueOf(termDS.count()));
      testReporter.publishEntry("termDS.schema", termDS.schema().simpleString());
      VerboseUtils.showPivot2(
          termDS, params.getUserCol(), params.getItemCol(), params.getTermCol(), false);

      /*
      Dataset<Row> dd =
          documentDS
              .groupBy(params.getItemCol(), params.getUserCol())
              .agg(collect_list(params.getTermCol()).as("terms"));
      dd.show(false);
       */

    }

    testReporter.publishEntry("expects", Arrays.toString(expects));

    testReporter.publishEntry("ids", expects[0]);

    Dataset<Row> recommendDS =
        new TermFrequencyInverseDocumentFrequency(params)
            .recommend(ratingDS, termDS, topN, expects[0]);

    if (Objects.equals(verbose, Boolean.TRUE)) {
      testReporter.publishEntry("recommendDS.count", String.valueOf(recommendDS.count()));
      testReporter.publishEntry("recommendDS.schema", recommendDS.schema().simpleString());
    }

    recommendDS
        .join(itemDS.select(params.getItemCol(), labelCol), params.getItemCol())
        .orderBy(col(COL.RANK))
        .show(false);

    double actual =
        recommendDS
            .where(
                col(COL.RANK).equalTo(expects[1]).and(col(params.getItemCol()).equalTo(expects[2])))
            .head()
            .getAs(params.getOutputCol());

    testReporter.publishEntry("actual", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expects[3]), actual, 1.0e-7);
  }
}
