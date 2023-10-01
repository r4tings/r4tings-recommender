package com.r4tings.recommender.examples.ch07;

import com.r4tings.recommender.model.tfidf.TermFrequencyInverseDocumentFrequency;
import com.r4tings.recommender.model.tfidf.TermFrequencyInverseDocumentFrequencyParams;
import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class TermFrequencyInverseDocumentFrequencyTest extends AbstractSparkTests {

  @ParameterizedTest
  @CsvSource({
    "'dataset/r4tings, ratings.parquet', 'dataset/r4tings, tags.parquet', 'COSINE, true, true', 3.5, 5, true, 'u4, 1, i3, 0.1604979'",
  })
  void termFrequencyInverseDocumentFrequencyExamples(
      @ConvertPathString String path1,
      @ConvertPathString String path2,
      @ConvertSimilarityMeasure UserDefinedFunction similarityMeasure,
      Double threshold,
      Integer topN,
      Boolean verbose,
      @ConvertStringArray String[] expectations,
      ArgumentsAccessor arguments) {

    Dataset<Row> ratingDS = spark.read().load(path1);
    Dataset<Row> termDS = spark.read().load(path2);

    TermFrequencyInverseDocumentFrequencyParams params =
        new TermFrequencyInverseDocumentFrequencyParams()
            .setSimilarityMeasure(similarityMeasure)
            .setThreshold(threshold)
            .setTermCol("tag")
            .setVerbose(verbose);

    TermFrequencyInverseDocumentFrequency recommender =
        new TermFrequencyInverseDocumentFrequency(params);

    Dataset<Row> recommendedItemDS = recommender.recommend(ratingDS, termDS, topN, expectations[0]);

    recommendedItemDS.show();

    double actual =
        recommendedItemDS
            .where(
                col(COL.RANK)
                    .equalTo(expectations[1])
                    .and(col(params.getItemCol()).equalTo(expectations[2])))
            .head()
            .getAs(params.getOutputCol());

    log.info("actual {}", String.format("%,.7f [%s]", actual, actual));

    assertEquals(Double.parseDouble(expectations[3]), actual, 1.0e-7);
  }
}
