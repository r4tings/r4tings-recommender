package com.r4tings.recommender.data.normalize;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

@Slf4j
public class ThresholdBinarizer extends RatingNormalizer<ThresholdBinarizer> {

  public ThresholdBinarizer() {
    super(ThresholdBinarizer.class.getSimpleName());
  }

  @Override
  protected ThresholdBinarizer self() {
    return this;
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS) {

    return ratingDS.withColumn(
        getOutputCol(),
        NormalizeMethod.BINARY_THRESHOLDING
            .invoke(getVerbose())
            .apply(col(getRatingCol()), lit(getThreshold())));
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol) {
    throw new UnsupportedOperationException(
        "The requested operation is not supported. - " + getGroup().getDescription());
  }

  /*
   * Param Definitions
   */
  public DoubleParam threshold() {
    return new DoubleParam(this, "threshold", "threshold");
  }

  /*
   * Param Getters
   */
  public Double getThreshold() {
    return (Double) getOrDefault(threshold());
  }

  /*
   *  Param Setters
   */

  public ThresholdBinarizer setThreshold(Double value) {
    if (Objects.nonNull(value)) {
      set(threshold(), value);
    }

    return this;
  }
}
