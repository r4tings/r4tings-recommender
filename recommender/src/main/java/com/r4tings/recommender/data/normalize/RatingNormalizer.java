/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import com.r4tings.recommender.common.ml.AbstractRatingTransformer;
import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.ml.param.GroupCol;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.Param;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.SPARK;

@Slf4j
public abstract class RatingNormalizer<T extends RatingNormalizer<T>>
    extends AbstractRatingTransformer implements GroupCol {

  public RatingNormalizer(String prefix) {
    super(prefix);

    //      setDefault(outputCol(), COL.RATING);
  }

  /*
   *  Transformer
   */
  @Override
  public Dataset<Row> transform(Dataset<?> dataset) {

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", explainParams());
    }

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(dataset)
            .select(
                SparkUtils.validateInputColumns(
                    dataset.columns(), getUserCol(), getItemCol(), getRatingCol()));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("ratingDS\n{}", ratingDS.showString(SPARK.DEFAULT_NUM_ROWS, 0, false));
      VerboseUtils.showPivot(ratingDS, getUserCol(), getItemCol(), getRatingCol(), 7);
    }

    Dataset<Row> normalizedRatingDS;
    if (Objects.isNull(getGroup())) { // Overall
      normalizedRatingDS = compute(ratingDS);
    } else { // Group
      normalizedRatingDS = compute(ratingDS, getGroupCol());
    }

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info(
          "normalizedRatingDS\n{}",
          normalizedRatingDS.showString(SPARK.DEFAULT_NUM_ROWS, 0, false));
      VerboseUtils.showPivot(normalizedRatingDS, getUserCol(), getItemCol(), getOutputCol(), 7);
    }

    return normalizedRatingDS.select(getUserCol(), getItemCol(), getOutputCol());
  }

  /*
   * Param Definitions
   */

  @Override
  public Param<Group> group() {
    return new Param<>(this, "group", "group column name:" + Group.descriptions());
  }

  /*
   * Param Getters
   */

  @Override
  public Group getGroup() {

    return get(group()).isEmpty() ? null : get(group()).get();
  }

  @Override
  public String getGroupCol() {
    return Objects.equals(getGroup(), Group.USER) ? getUserCol() : getItemCol();
  }

  @Override
  public String getOutputCol() {

    return get(outputCol()).isEmpty() ? getRatingCol() : get(outputCol()).get();
  }

  /*
   *  Param Setters
   */

  @Override
  public T setVerbose(Boolean value) {
    if (Objects.nonNull(value)) {
      set(verbose(), value);
    }

    return self();
  }

  @Override
  public T setOutputCol(String value) {
    if (Objects.nonNull(value)) {
      set(outputCol(), value);
    }

    return self();
  }

  @Override
  public T setUserCol(String value) {
    if (Objects.nonNull(value)) {
      set(userCol(), value);
    }

    return self();
  }

  @Override
  public T setItemCol(String value) {
    if (Objects.nonNull(value)) {
      set(itemCol(), value);
    }

    return self();
  }

  @Override
  public T setRatingCol(String value) {
    if (Objects.nonNull(value)) {
      set(ratingCol(), value);
    }

    return self();
  }

  @Override
  public T setGroup(Group value) {
    if (Objects.nonNull(value)) {
      set(group(), value);
    }

    return self();
  }

  @Override
  public T setGroupCol(String value) {
    if (Objects.nonNull(value)) {
      set(group(), Group.get(value));
    }

    return self();
  }

  protected abstract T self();

  protected abstract Dataset<Row> compute(Dataset<Row> ratingDS);

  protected abstract Dataset<Row> compute(Dataset<Row> ratingDS, String groupCol);

  protected Dataset<Row> scale(
      Dataset<Row> ratingDS, String groupCol, Dataset<Row> groupStatsDS, Column outputColumn) {

    return ratingDS
        .join(groupStatsDS.hint(SPARK.HINT_BROADCAST), groupCol)
        .withColumn(getOutputCol(), outputColumn);
  }
}
