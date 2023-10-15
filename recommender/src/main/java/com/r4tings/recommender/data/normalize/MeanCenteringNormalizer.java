/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class MeanCenteringNormalizer extends RatingNormalizer<MeanCenteringNormalizer> {

  public MeanCenteringNormalizer() {
    super(MeanCenteringNormalizer.class.getSimpleName());
  }

  @Override
  protected MeanCenteringNormalizer self() {
    return this;
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS) {

    double overallMean = ratingDS.agg(mean(getRatingCol())).head().getDouble(0);

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("overall mean: {}", overallMean);
    }

    return ratingDS.withColumn(
        getOutputCol(),
        NormalizeMethod.MEAN_CENTERING
            .invoke(getVerbose())
            .apply(col(getRatingCol()), lit(overallMean)));
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol) {
    Dataset<Row> groupStatsDS =
        ratingDS.groupBy(col(groupCol)).agg(avg(getRatingCol()).as(COL.MEAN));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("groupStatsDS: {}", groupStatsDS.count());
      groupStatsDS.orderBy(length(col(groupCol)), col(groupCol)).show();
    }

    return scale(
        ratingDS,
        groupCol,
        groupStatsDS,
        NormalizeMethod.MEAN_CENTERING
            .invoke(getVerbose())
            .apply(col(getRatingCol()), col(COL.MEAN)));
  }
}
