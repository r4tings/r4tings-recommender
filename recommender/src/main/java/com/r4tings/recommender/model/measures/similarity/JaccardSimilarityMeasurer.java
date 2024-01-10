/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;

public class JaccardSimilarityMeasurer extends RatingSimilarityMeasurer<JaccardSimilarityMeasurer> {

  public JaccardSimilarityMeasurer() {
    super(JaccardSimilarityMeasurer.class.getSimpleName());
  }

  @Override
  protected JaccardSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {

    return compute(
        ratingDS,
        SimilarityMeasure.BINARY_JACCARD
            .invoke(getImputeZero(), getVerbose())
            .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
  }
}
