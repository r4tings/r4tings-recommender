/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml;

import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.length;

@Slf4j
public abstract class AbstractRecommender {

  private final CommonParams<? extends CommonParams> params;

  public AbstractRecommender(CommonParams<? extends CommonParams> params) {
    this.params = params;
  }

  protected abstract Dataset<Row> execute(Dataset<Row> ratingDS, Object[] ids);

  public Dataset<Row> recommend(Dataset<Row> ratings, int topN, Object... userId) {

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", params.explainParams());
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

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("ratingDS: {}", ratingDS.count());
      VerboseUtils.showPivot(
          ratingDS, params.getUserCol(), params.getItemCol(), params.getRatingCol(), 7);
    }

    Dataset<Row> itemScoreDS = execute(ratingDS, userId);

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("itemScoreDS: {}", itemScoreDS.count());
      VerboseUtils.show(itemScoreDS, params.getOutputCol(), params.getOutputCol(), 7);
    }

    /*
     * Step 4: Top-N item recommendation
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
}
