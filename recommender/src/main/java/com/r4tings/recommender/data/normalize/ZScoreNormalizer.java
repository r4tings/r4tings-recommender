package com.r4tings.recommender.data.normalize;

import com.r4tings.recommender.common.ml.param.Group;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class ZScoreNormalizer extends RatingNormalizer<ZScoreNormalizer> {

  public ZScoreNormalizer() {
    super(ZScoreNormalizer.class.getSimpleName());
  }

  @Override
  protected ZScoreNormalizer self() {
    return this;
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS) {
    Row row =
        ratingDS
            .agg(mean(getRatingCol()).as(COL.MEAN), stddev(getRatingCol()).as(COL.STDDEV))
            .head();

    double mean = row.getAs(COL.MEAN);
    double stddev = row.getAs(COL.STDDEV);

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("mean: {} stddev: {}", mean, stddev);
    }

    return ratingDS.withColumn(
        getOutputCol(),
        NormalizeMethod.Z_SCORE
            .invoke(getVerbose())
            .apply(col(getRatingCol()), lit(mean), lit(stddev)));
  }

  @Override
  protected Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol) {

    Dataset<Row> groupStatsDS =
        ratingDS
            .groupBy(col(groupCol))
            .agg(mean(getRatingCol()).as(COL.MEAN), stddev(getRatingCol()).as(COL.STDDEV));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("groupStatsDS: {}", groupStatsDS.count());
      groupStatsDS.show();
    }

    return scale(
        ratingDS,
        groupCol,
        groupStatsDS,
        NormalizeMethod.Z_SCORE
            .invoke(getVerbose())
            .apply(col(getRatingCol()), col(COL.MEAN), col(COL.STDDEV)));
  }

  /*
   *  Param Setters
   */

  @Override
  public ZScoreNormalizer setGroup(Group value) {
    if (Objects.nonNull(value)) {
      set(group(), value);
    }

    return this;
  }

  @Override
  public ZScoreNormalizer setGroupCol(String value) {
    if (Objects.nonNull(value)) {
      set(group(), Group.get(value));
    }

    return this;
  }

  @Override
  public ZScoreNormalizer setVerbose(Boolean value) {
    if (Objects.nonNull(value)) {
      set(verbose(), value);
    }

    return this;
  }
}
