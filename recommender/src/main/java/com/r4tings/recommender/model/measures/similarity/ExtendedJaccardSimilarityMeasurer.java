/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;

public class ExtendedJaccardSimilarityMeasurer
    extends RatingSimilarityMeasurer<ExtendedJaccardSimilarityMeasurer> {

  public ExtendedJaccardSimilarityMeasurer() {
    super(ExtendedJaccardSimilarityMeasurer.class.getSimpleName());
    // setDefault(imputeZero(), Boolean.TRUE);
  }

  @Override
  protected ExtendedJaccardSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {
    //    if (Objects.nonNull(getImputeZero()) && getImputeZero().equals(Boolean.FALSE)) {
    //      throw new UnsupportedOperationException("The requested operation is not supported.");
    //    }

    return compute(
        ratingDS,
        SimilarityMeasure.BINARY_EXTENDED_JACCARD
            .invoke(getImputeZero(), getVerbose())
            .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
  }
}
