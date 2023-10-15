/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.baseline;

import com.r4tings.recommender.common.ml.CommonModel;
import com.r4tings.recommender.common.util.SparkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.annotation.Experimental;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

@Slf4j
public class MeanRatingBaselineModel extends CommonModel<MeanRatingBaselineModel> {

  public MeanRatingBaselineModel() {
    super(MeanRatingBaselineModel.class.getSimpleName());
    setDefault(residualCol(), COL.RESIDUAL);
  }

  @Override
  public Dataset<Row> transform(Dataset<?> ratings) {

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(ratings)
            .select(
                SparkUtils.validateInputColumns(
                    ratings.columns(), getUserCol(), getItemCol(), getRatingCol()));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", explainParams());
      log.info("ratingDS\n{}", ratingDS.showString(SPARK.DEFAULT_NUM_ROWS, 0, false));
    }

    return ratingDS
        .join(getUserBiasDS(), getUserCol())
        .join(getItemBiasDS(), getItemCol())
        .withColumn(
            getOutputCol(),
            (lit(getOverallMeanRating()).plus(col(COL.USER_BIAS).plus(col(COL.ITEM_BIAS)))))
        .withColumn(getResidualCol(), col(getRatingCol()).minus(col(COL.BIAS)));
  }

  /*
   * Param Definitions
   */

  public Param<String> residualCol() {
    return new Param<>(this, "residualCol", "residual column name");
  }

  public DoubleParam overallMeanRating() {
    return new DoubleParam(this, "overallMeanRating", "TODO");
  }

  public Param<Dataset<Row>> userBiasDS() {
    return new Param<>(this, "userBias", "TODO");
  }

  public Param<Dataset<Row>> itemBiasDS() {
    return new Param<>(this, "itemBias", "TODO");
  }

  /*
   * Param Getters
   */

  public String getResidualCol() {
    return getOrDefault(residualCol());
  }

  public Double getOverallMeanRating() {
    return get(overallMeanRating()).isEmpty() ? null : (Double) get(overallMeanRating()).get();
  }

  public Dataset<Row> getUserBiasDS() {
    return get(userBiasDS()).isEmpty() ? null : get(userBiasDS()).get();
  }

  public Dataset<Row> getItemBiasDS() {
    return get(itemBiasDS()).isEmpty() ? null : get(itemBiasDS()).get();
  }

  /*
   *  Param Setters
   */

  public MeanRatingBaselineModel setResidualCol(String value) {
    if (Objects.nonNull(value)) {
      set(residualCol(), value);
    }

    return self();
  }

  public MeanRatingBaselineModel setOverallMeanRating(Double value) {
    if (Objects.nonNull(value)) {
      set(overallMeanRating(), value);
    }

    return this;
  }

  public MeanRatingBaselineModel setUserBiasDS(Dataset<Row> value) {
    if (Objects.nonNull(value)) {
      set(userBiasDS(), value);
    }

    return this;
  }

  public MeanRatingBaselineModel setItemBiasDS(Dataset<Row> value) {
    if (Objects.nonNull(value)) {
      set(itemBiasDS(), value);
    }

    return this;
  }

  @Override
  protected MeanRatingBaselineModel self() {
    return this;
  }

  @Experimental
  public Dataset<Row> transformAll() {
    return getUserBiasDS()
        .crossJoin(getItemBiasDS())
        .withColumn(
            getOutputCol(),
            (lit(getOverallMeanRating()).plus(col(COL.USER_BIAS).plus(col(COL.ITEM_BIAS)))));
  }
}
