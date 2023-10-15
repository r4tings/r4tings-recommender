/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static org.apache.spark.sql.functions.*;

@Slf4j
public class DecimalScalingNormalizer extends RatingNormalizer<DecimalScalingNormalizer> {

  public DecimalScalingNormalizer() {
    super(DecimalScalingNormalizer.class.getSimpleName());
  }

  @Override
  protected DecimalScalingNormalizer self() {
    return this;
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS) {

    double max = ratingDS.agg(max(col(getRatingCol()))).head().getDouble(0);
    int exponent = (int) Math.ceil(Math.log10(Math.abs(max)));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("max: {} exponent: {}", max, exponent);
    }

    return ratingDS.withColumn(
        getOutputCol(),
        NormalizeMethod.DECIMAL_SCALING
            .invoke(getVerbose())
            .apply(col(getRatingCol()), lit(exponent)));
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol) {
    throw new UnsupportedOperationException(
        "The requested operation is not supported. - " + getGroup().getDescription());
  }
}
