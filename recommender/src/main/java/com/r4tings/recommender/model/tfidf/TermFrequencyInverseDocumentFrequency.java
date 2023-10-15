/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.tfidf;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.data.normalize.MeanCenteringNormalizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class TermFrequencyInverseDocumentFrequency {

  private final TermFrequencyInverseDocumentFrequencyParams params;

  public TermFrequencyInverseDocumentFrequency(TermFrequencyInverseDocumentFrequencyParams params) {
    this.params = params;
  }

  public Dataset<Row> recommend(
      Dataset<Row> ratings, Dataset<Row> terms, int topN, Object... userId) {

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", params.explainParams());
      log.info("ratings\n{}", ratings.showString(10, 0, false));
      log.info("terms\n{}", terms.showString(10, 0, false));
    }

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(ratings)
            .select(
                SparkUtils.validateInputColumns(
                    ratings.columns(),
                    params.getUserCol(),
                    params.getItemCol(),
                    params.getRatingCol()));

    Dataset<Row> termDS =
        Objects.requireNonNull(terms)
            .select(
                SparkUtils.validateInputColumns(
                    terms.columns(),
                    params.getUserCol(),
                    params.getItemCol(),
                    params.getTermCol()));

    Dataset<Row> itemScoreDS = execute(ratingDS, termDS, userId);

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("itemScoreDS\n{}", itemScoreDS.showString(10, 0, false));
    }

    /*
     * Step 4: Recommend Top-N items
     */

    return SparkUtils.zipWithIndex(
        itemScoreDS
            .na()
            .drop()
            .orderBy(
                col(params.getOutputCol()).desc(),
                length(col(params.getItemCol())),
                col(params.getItemCol()))
            .limit(topN),
        COL.RANK,
        1);
  }

  protected Dataset<Row> execute(Dataset<Row> ratingDS, Dataset<Row> termDS, Object[] ids) {

    /*
     * Step 1: Compute the unit-normalized TF-IDF weight for each item in the dataset
     */

    Dataset<Row> tfDS =
        termDS
            .groupBy(col(params.getItemCol()), col(params.getTermCol()))
            .agg(count(params.getTermCol()).as(COL.TF));

    long totalDocument = ratingDS.agg(countDistinct(params.getItemCol())).head().getAs(0);

    Dataset<Row> idfDS =
        tfDS.groupBy(params.getTermCol())
            .agg(log10(lit(totalDocument).divide(count(params.getTermCol()))).as(COL.IDF));

    Dataset<Row> tfIdfDS =
        tfDS.join(idfDS.hint(SPARK.HINT_BROADCAST), params.getTermCol())
            .select(
                col(params.getItemCol()),
                col(params.getTermCol()),
                col(COL.TF).multiply(col(COL.IDF)).as(COL.TFIDF));

    Dataset<Row> euclideanNormDS =
        tfIdfDS
            .groupBy(params.getItemCol())
            .agg(sqrt(sum(pow(col(COL.TFIDF), 2))).as(COL.EUCLIDEAN_NORM));

    Dataset<Row> lengthNormalizedTfIdfDS =
        tfIdfDS
            .join(euclideanNormDS.hint(SPARK.HINT_BROADCAST), params.getItemCol())
            .select(
                col(params.getItemCol()).as(params.getItemCol()),
                col(params.getTermCol()),
                col(COL.TFIDF).divide(col(COL.EUCLIDEAN_NORM)).as(COL.LENGTH_NORMALIZED_TFIDF));

    /*
     *  Step 2. Build weighted or unweighted user profile for target user
     */

    UserDefinedFunction seqMapsMergeSummingValueUDF =
        udf(
            (Seq<scala.collection.Map<?, Double>> termScalaMapSeq) ->
                JavaConverters.seqAsJavaList(termScalaMapSeq).stream()
                    .flatMap(m -> JavaConverters.mapAsJavaMap(m).entrySet().stream())
                    .collect(
                        groupingBy(
                            java.util.Map.Entry::getKey,
                            summingDouble(java.util.Map.Entry::getValue))),
            DataTypes.createMapType(DataTypes.StringType, DataTypes.DoubleType, false));

    Dataset<Row> userProfileDS;
    if (Objects.nonNull(params.getThreshold())) {
      userProfileDS =
          ratingDS
              .where(
                  col(params.getUserCol())
                      .isin(ids)
                      .and(col(params.getRatingCol()).$greater$eq(lit(params.getThreshold()))))
              .hint(SPARK.HINT_BROADCAST)
              .join(lengthNormalizedTfIdfDS, params.getItemCol())
              .groupBy(params.getItemCol(), params.getUserCol())
              .agg(
                  map_from_arrays(
                          collect_list(params.getTermCol()),
                          collect_list(COL.LENGTH_NORMALIZED_TFIDF))
                      .as(COL.DOCUMENT_TFIDF))
              .groupBy(params.getUserCol())
              .agg(
                  udf(
                          (Seq<scala.collection.Map<?, Double>> termScalaMapSeq) ->
                              JavaConverters.seqAsJavaList(termScalaMapSeq).stream()
                                  .flatMap(m -> JavaConverters.mapAsJavaMap(m).entrySet().stream())
                                  .collect(
                                      groupingBy(
                                          java.util.Map.Entry::getKey,
                                          summingDouble(java.util.Map.Entry::getValue))),
                          DataTypes.createMapType(
                              DataTypes.StringType, DataTypes.DoubleType, false))
                      .apply(collect_list(col(COL.DOCUMENT_TFIDF)))
                      .as(COL.USER_TFIDF));

    } else {
      MeanCenteringNormalizer normalizer = new MeanCenteringNormalizer().setGroup(Group.USER);

      Dataset<Row> normalizedRatingDS =
          Objects.requireNonNull(normalizer)
              .setUserCol(params.getUserCol())
              .setItemCol(params.getItemCol())
              .setRatingCol(params.getRatingCol())
              .transform(ratingDS);

      userProfileDS =
          normalizedRatingDS
              .where(col(params.getUserCol()).isin(ids))
              .hint(SPARK.HINT_BROADCAST)
              .join(lengthNormalizedTfIdfDS, params.getItemCol())
              .groupBy(params.getItemCol(), params.getUserCol())
              .agg(
                  map_from_arrays(
                          collect_list(params.getTermCol()),
                          collect_list(
                              col(COL.LENGTH_NORMALIZED_TFIDF)
                                  .multiply(col(params.getRatingCol()))))
                      .as(COL.DOCUMENT_TFIDF))
              .groupBy(params.getUserCol())
              .agg(
                  seqMapsMergeSummingValueUDF
                      .apply(collect_list(col(COL.DOCUMENT_TFIDF)))
                      .as(COL.USER_TFIDF));
    }

    Dataset<Row> itemProfileDS =
        SparkUtils.leftAntiJoin(
                lengthNormalizedTfIdfDS,
                ratingDS
                    .where(col(params.getUserCol()).isin(ids))
                    .select(col(params.getItemCol()).as(params.getItemCol()))
                    .hint(SPARK.HINT_BROADCAST),
                params.getItemCol())
            .groupBy(params.getItemCol())
            .agg(
                map_from_arrays(
                        collect_list(params.getTermCol()),
                        collect_list(COL.LENGTH_NORMALIZED_TFIDF))
                    .as(COL.DOCUMENT_TFIDF));

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("tfDS\n{}", tfDS.showString(10, 0, false));
      log.info("idfDS\n{}", idfDS.showString(10, 0, false));
      log.info("tfIdfDS\n{}", tfIdfDS.showString(10, 0, false));

      log.info("euclideanNormDS\n{}", euclideanNormDS.showString(10, 0, false));
      log.info("lengthNormalizedTfIdfDS\n{}", lengthNormalizedTfIdfDS.showString(10, 0, false));

      log.info("userProfileDS\n{}", userProfileDS.showString(10, 0, false));
      log.info("itemProfileDS\n{}", itemProfileDS.showString(10, 0, false));
    }

    /*
     *  Step 3. Compute cosine similarity between that target user's vector and each item's vector
     */

    return userProfileDS
        .hint(SPARK.HINT_BROADCAST)
        .crossJoin(itemProfileDS)
        .withColumn(
            params.getOutputCol(),
            params.getSimilarityMeasure().apply(col(COL.DOCUMENT_TFIDF), col(COL.USER_TFIDF)))
        .select(col(params.getItemCol()).as(params.getItemCol()), col(params.getOutputCol()));
  }
}
