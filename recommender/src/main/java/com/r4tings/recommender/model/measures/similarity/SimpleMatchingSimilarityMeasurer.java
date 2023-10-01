package com.r4tings.recommender.model.measures.similarity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;

public class SimpleMatchingSimilarityMeasurer
    extends RatingSimilarityMeasurer<SimpleMatchingSimilarityMeasurer> {

  public SimpleMatchingSimilarityMeasurer() {
    super(SimpleMatchingSimilarityMeasurer.class.getSimpleName());

    setDefault(imputeZero(), Boolean.TRUE);
  }

  @Override
  protected SimpleMatchingSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {

    if (Objects.nonNull(getImputeZero()) && getImputeZero().equals(Boolean.FALSE)) {
      throw new UnsupportedOperationException("The requested operation is not supported.");
    }

    return compute(
        ratingDS,
        SimilarityMeasure.BINARY_SMC
            .invoke(getImputeZero(), getVerbose())
            .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
  }
}
