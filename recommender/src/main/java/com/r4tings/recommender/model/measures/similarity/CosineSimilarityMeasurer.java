package com.r4tings.recommender.model.measures.similarity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.col;

public class CosineSimilarityMeasurer extends RatingSimilarityMeasurer<CosineSimilarityMeasurer> {

  public CosineSimilarityMeasurer() {
    super(CosineSimilarityMeasurer.class.getSimpleName());
  }

  @Override
  protected CosineSimilarityMeasurer self() {
    return this;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS) {

    return compute(
        ratingDS,
        SimilarityMeasure.COSINE
            .invoke(getImputeZero(), getVerbose())
            .apply(col(COL.LHS_RATINGS), col(COL.RHS_RATINGS)));
  }
}
