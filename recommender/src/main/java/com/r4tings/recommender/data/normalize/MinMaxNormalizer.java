/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class MinMaxNormalizer extends RatingNormalizer<MinMaxNormalizer> {

  public MinMaxNormalizer() {
    super(MinMaxNormalizer.class.getSimpleName());

    setDefault(lower(), 0d);
    setDefault(upper(), 1d);
  }

  @Override
  protected MinMaxNormalizer self() {
    return this;
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS) {
    Row row = ratingDS.agg(min(getRatingCol()).as(COL.MIN), max(getRatingCol()).as(COL.MAX)).head();

    double min = row.getAs(COL.MIN);
    double max = row.getAs(COL.MAX);

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("min: {} max: {}", min, max);
    }

    return ratingDS.withColumn(
        getOutputCol(),
        NormalizeMethod.MIN_MAX
            .invoke(getVerbose())
            .apply(col(getRatingCol()), lit(min), lit(max), lit(getLower()), lit(getUpper())));
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol) {
    Dataset<Row> groupStatsDS =
        ratingDS
            .groupBy(col(groupCol))
            .agg(min(getRatingCol()).as(COL.MIN), max(getRatingCol()).as(COL.MAX));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("groupStatsDS: {}", groupStatsDS.count());
      groupStatsDS.show();
    }

    return scale(
        ratingDS,
        groupCol,
        groupStatsDS,
        NormalizeMethod.MIN_MAX
            .invoke(getVerbose())
            .apply(
                col(getRatingCol()), col(COL.MIN), col(COL.MAX), lit(getLower()), lit(getUpper())));
  }

  /*
   * Param Definitions
   */
  public DoubleParam lower() {
    return new DoubleParam(this, "lower", "lower bound");
  }

  public DoubleParam upper() {
    return new DoubleParam(this, "upper", "upper bound");
  }

  /*
   * Param Getters
   */
  public Double getLower() {
    return (Double) getOrDefault(lower());
  }

  public Double getUpper() {
    return (Double) getOrDefault(upper());
  }

  /*
   *  Param Setters
   */

  public MinMaxNormalizer setLower(Double value) {
    if (Objects.nonNull(value)) {
      set(lower(), value);
    }

    return this;
  }

  public MinMaxNormalizer setUpper(Double value) {
    if (Objects.nonNull(value)) {
      set(upper(), value);
    }

    return this;
  }
}
