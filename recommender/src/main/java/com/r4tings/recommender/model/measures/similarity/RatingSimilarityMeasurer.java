/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import com.r4tings.recommender.common.ml.AbstractRatingTransformer;
import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.ml.param.GroupCol;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.BooleanParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@Slf4j
public abstract class RatingSimilarityMeasurer<T extends RatingSimilarityMeasurer<T>>
    extends AbstractRatingTransformer implements GroupCol {

  public RatingSimilarityMeasurer(String prefix) {
    super(prefix);

    setDefault(outputCol(), COL.SIMILARITY);
    setDefault(imputeZero(), false);
  }

  // TEMPLATE METHOD
  @Override
  public final Dataset<Row> transform(Dataset<?> ratings) {

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", explainParams());
    }

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(ratings)
            .select(
                SparkUtils.validateInputColumns(
                    ratings.columns(), getUserCol(), getItemCol(), getRatingCol()));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("ratingDS: {}", ratingDS.count());
      VerboseUtils.showPivot(ratingDS, getUserCol(), getItemCol(), getRatingCol(), 7);
    }

    Dataset<Row> similarityDS = execute(ratingDS);

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.info("similarityDS: {}", similarityDS.count());
      VerboseUtils.showPivot(similarityDS, COL.LHS, COL.RHS, getOutputCol(), 7);
    }

    return similarityDS;
  }

  /*
   * Param Definitions
   */

  @Override
  public Param<Group> group() {
    return new Param<>(this, "group", "group column name:" + Group.descriptions());
  }

  public BooleanParam imputeZero() {
    return new BooleanParam(this, "imputeZero", "imputeZero is ,,,,");
  }

  public Param<Object[]> ids() {
    return new Param<>(this, "ids", "ids");
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

  public Boolean getImputeZero() {
    return (Boolean) getOrDefault(imputeZero());
  }

  public Object[] getIds() {
    //    return getOrDefault(criterion());
    return get(ids()).isEmpty() ? null : get(ids()).get();
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

  public T setImputeZero(Boolean value) {
    if (Objects.nonNull(value)) {
      set(imputeZero(), value);
    }

    return self();
  }

  public T setIds(Object... value) {
    if (Objects.nonNull(value)) {
      set(ids(), value);
    }

    return self();
  }

  protected Dataset<Row> compute(Dataset<Row> ratingDS, Column outputColumn) {

    Dataset<Row> crossJoinedDS = null;

    if (Objects.isNull(getIds())) {
      Dataset<Row> indexDS =
          SparkUtils.zipWithIndex(
              ratingDS
                  .select(col(getGroupCol()).as(COL.LHS))
                  .distinct()
                  .orderBy(length(col(getGroupCol())), col(getGroupCol())),
              COL.INDEX);

      Dataset<Row> lhsDS =
          ratingDS
              .groupBy(col(getGroupCol()).as(COL.LHS))
              .agg(
                  map_from_arrays(
                          collect_list(
                              col(
                                  Objects.equals(getGroup(), Group.USER)
                                      ? getItemCol()
                                      : getUserCol())),
                          collect_list(col(getRatingCol())))
                      .as(COL.LHS_RATINGS))
              .join(indexDS.hint(SPARK.HINT_BROADCAST), COL.LHS)
              .withColumnRenamed(COL.INDEX, COL.LHS_INDEX);

      Dataset<Row> rhsDS =
          lhsDS
              .withColumnRenamed(COL.LHS, COL.RHS)
              .withColumnRenamed(COL.LHS_RATINGS, COL.RHS_RATINGS)
              .withColumnRenamed(COL.LHS_INDEX, COL.RHS_INDEX);

      // TODO 검증을 위한 단순 출력용  이후 삭제
      // crossJoinedDS =
      // lhsDS.crossJoin(rhsDS).where(col(COL.LHS_INDEX).notEqual(col(COL.RHS_INDEX)));
      crossJoinedDS = lhsDS.crossJoin(rhsDS).where(col(COL.LHS_INDEX).$greater(col(COL.RHS_INDEX)));
    } else if (Objects.nonNull(getIds())) {

      Dataset<Row> lhsDS =
          ratingDS
              .where(col(getGroupCol()).isin(getIds()))
              .groupBy(col(getGroupCol()).as(COL.LHS))
              .agg(
                  map_from_arrays(
                          collect_list(
                              col(
                                  Objects.equals(getGroup(), Group.USER)
                                      ? getItemCol()
                                      : getUserCol())),
                          collect_list(col(getRatingCol())))
                      .as(COL.LHS_RATINGS));

      Dataset<Row> rhsDS =
          SparkUtils.leftAntiJoin(
                  ratingDS,
                  lhsDS.select(col(COL.LHS).as(getGroupCol())).hint(SPARK.HINT_BROADCAST),
                  getGroupCol())
              .groupBy(col(getGroupCol()).as(COL.RHS))
              .agg(
                  map_from_arrays(
                          collect_list(
                              col(
                                  Objects.equals(getGroup(), Group.USER)
                                      ? getItemCol()
                                      : getUserCol())),
                          collect_list(col(getRatingCol())))
                      .as(COL.RHS_RATINGS));

      crossJoinedDS = lhsDS.hint(SPARK.HINT_BROADCAST).crossJoin(rhsDS);
    }

    if (log.isDebugEnabled()) {
      log.debug("crossJoinedDS: {}", crossJoinedDS.count());
      crossJoinedDS.select(COL.LHS, COL.LHS_RATINGS, COL.RHS, COL.RHS_RATINGS).show(false);
    }

    return Objects.requireNonNull(crossJoinedDS)
        .select(col(COL.LHS), col(COL.RHS), outputColumn.as(getOutputCol()));
  }

  protected abstract T self();

  // HOOK METHOD
  protected abstract Dataset<Row> execute(Dataset<Row> ratingDS);
}
