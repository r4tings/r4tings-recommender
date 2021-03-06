/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import org.apache.spark.ml.param.IntParam;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

public class ManhattanSimilarityMeasurer
    extends RatingSimilarityMeasurer<ManhattanSimilarityMeasurer> {

  public ManhattanSimilarityMeasurer() {
    super(ManhattanSimilarityMeasurer.class.getSimpleName());
    setDefault(actualScale(), null);
  }

  @Override
  protected ManhattanSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {
    if (Objects.isNull(getActualScale())) {
      return compute(
          ratingDS,
          SimilarityMeasure.MANHATTAN
              .invoke(getImputeZero(), getVerbose())
              .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
    } else {
      return compute(
          ratingDS,
          SimilarityMeasure.WEIGHTED_MANHATTAN
              .invoke(getImputeZero(), getVerbose())
              .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS), lit(getActualScale())));
    }
  }

  /*
   * Param Definitions
   */
  public IntParam actualScale() {
    return new IntParam(this, "lower", "lower bound");
  }

  /*
   * Param Getters
   */
  public Integer getActualScale() {
    return (Integer) getOrDefault(actualScale());
  }

  /*
   *  Param Setters
   */

  public ManhattanSimilarityMeasurer setActualScale(Integer value) {
    if (Objects.nonNull(value)) {
      set(actualScale(), value);
    }

    return this;
  }
}
