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

public class EuclideanSimilarityMeasurer
    extends RatingSimilarityMeasurer<EuclideanSimilarityMeasurer> {

  public EuclideanSimilarityMeasurer() {
    super(EuclideanSimilarityMeasurer.class.getSimpleName());

    setDefault(weight(), null);
  }

  @Override
  protected EuclideanSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {

    if (Objects.isNull(getWeight())) {
      return compute(
          ratingDS,
          SimilarityMeasure.EUCLIDEAN
              .invoke(getImputeZero(), getVerbose())
              .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
    } else {
      return compute(
          ratingDS,
          SimilarityMeasure.WEIGHTED_EUCLIDEAN
              .invoke(getImputeZero(), getVerbose())
              .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS), lit(getWeight())));
    }
  }

  /*
   * Param Definitions
   */
  public IntParam weight() {
    return new IntParam(this, "lower", "lower bound");
  }

  /*
   * Param Getters
   */
  public Integer getWeight() {
    return (Integer) getOrDefault(weight());
  }

  /*
   *  Param Setters
   */

  public EuclideanSimilarityMeasurer setWeight(Integer value) {
    if (Objects.nonNull(value)) {
      set(weight(), value);
    }

    return this;
  }
}
