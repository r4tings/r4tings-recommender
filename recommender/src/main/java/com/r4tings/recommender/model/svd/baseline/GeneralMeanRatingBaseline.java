/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.baseline;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.ParamValidators;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.collection.JavaConverters;

import java.util.Collections;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class GeneralMeanRatingBaseline extends CommonEstimator<GeneralMeanRatingBaseline> {

  public GeneralMeanRatingBaseline() {
    super(GeneralMeanRatingBaseline.class.getSimpleName());

    setDefault(lambda2(), 0);
    setDefault(lambda3(), 0);
  }

  @Override
  protected GeneralMeanRatingBaseline self() {
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

    Dataset<Row> itemBiasDS =
        ratingDS
            .groupBy(getItemCol())
            .agg(
                sum(col(getRatingCol()).minus(lit(overallMeanRating)))
                    .divide(count(getRatingCol()).plus(lit(getLambda2())))
                    .as(COL.ITEM_BIAS));

    Dataset<Row> userBiasDS =
        ratingDS
            .join(
                itemBiasDS.hint(SPARK.HINT_BROADCAST),
                JavaConverters.asScalaBuffer(Collections.singletonList(getItemCol())),
                SPARK.JOINTYPE_LEFT_OUTER)
            .groupBy(getUserCol())
            .agg(
                sum(col(getRatingCol()).minus(lit(overallMeanRating)).minus(col(COL.ITEM_BIAS)))
                    .divide(count(getRatingCol()).plus(lit(getLambda3())))
                    .as(COL.USER_BIAS));

    ratingDS
        .join(
            itemBiasDS.hint(SPARK.HINT_BROADCAST),
            JavaConverters.asScalaBuffer(Collections.singletonList(getItemCol())),
            SPARK.JOINTYPE_LEFT_OUTER)
        .groupBy(getUserCol())
        .agg(
            collect_list(col(getRatingCol())),
            lit(overallMeanRating),
            collect_list(col(COL.ITEM_BIAS)),
            count(getRatingCol()))
            .show(false);

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("overallMeanRating: {}", String.format("%,.7f", overallMeanRating));
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

  /*
   * Param Definitions
   */

  public IntParam lambda2() {
    return new IntParam(this, "lamda2", "lamda2", ParamValidators.gtEq(0d));
  }

  public IntParam lambda3() {
    return new IntParam(this, "lamda3", "lamda3", ParamValidators.gtEq(0d));
  }

  /*
   *  Param Getters
   */

  public Integer getLambda2() {
    return (Integer) getOrDefault(lambda2());
  }

  public Integer getLambda3() {
    return (Integer) getOrDefault(lambda3());
  }

  /*
   *  Param Setters
   */

  public GeneralMeanRatingBaseline setLambda2(Integer value) {
    if (Objects.nonNull(value)) {
      set(lambda2(), value);
    }
    return this;
  }

  public GeneralMeanRatingBaseline setLambda3(Integer value) {
    if (Objects.nonNull(value)) {
      set(lambda3(), value);
    }
    return this;
  }
}
