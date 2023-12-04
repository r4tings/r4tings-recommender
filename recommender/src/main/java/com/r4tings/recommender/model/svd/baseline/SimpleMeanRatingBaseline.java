/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.baseline;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.lit;

@Slf4j
public class SimpleMeanRatingBaseline extends CommonEstimator<SimpleMeanRatingBaseline> {

  public SimpleMeanRatingBaseline() {
    super(SimpleMeanRatingBaseline.class.getSimpleName());
  }

  @Override
  protected SimpleMeanRatingBaseline self() {
    return this;
  }

  @Override
  public MeanRatingBaselineModel fit(Dataset ratings) {

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(ratings)
            .select(
                SparkUtils.validateInputColumns(
                    ratings.columns(), getUserCol(), getItemCol(), getRatingCol()));

    double overallMeanRating = ratingDS.agg(avg(getRatingCol())).head().getDouble(0);

    Dataset<Row> userBiasDS =
        ratingDS
            .groupBy(getUserCol())
            .agg(avg(getRatingCol()).minus(lit(overallMeanRating)).as(COL.USER_BIAS));

    Dataset<Row> itemBiasDS =
        ratingDS
            .groupBy(getItemCol())
            .agg(avg(getRatingCol()).minus(lit(overallMeanRating)).as(COL.ITEM_BIAS));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("overallMeanRating: {}", String.format("%.7f", overallMeanRating));
      log.info("itemBiasDS\n{}", itemBiasDS.showString(10, 0, false));
      log.info("userBiasDS\n{}", userBiasDS.showString(10, 0, false));

      VerboseUtils.show(itemBiasDS, getItemCol(), COL.ITEM_BIAS, 7);
      VerboseUtils.show(userBiasDS, getUserCol(), COL.USER_BIAS, 7);
    }

    return new MeanRatingBaselineModel()
        .setOverallMeanRating(overallMeanRating)
        .setUserBiasDS(userBiasDS)
        .setItemBiasDS(itemBiasDS)
        .setVerbose(getVerbose())
        .setUserCol(getUserCol())
        .setItemCol(getItemCol())
        .setRatingCol(getRatingCol())
        .setOutputCol(getOutputCol());
  }
}
