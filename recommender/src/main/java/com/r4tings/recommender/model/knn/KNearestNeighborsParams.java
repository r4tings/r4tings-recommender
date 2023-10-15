/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.knn;

import com.r4tings.recommender.common.ml.CommonParams;
import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.ml.param.GroupCol;
import com.r4tings.recommender.data.normalize.RatingNormalizer;
import com.r4tings.recommender.model.measures.similarity.RatingSimilarityMeasurer;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamValidators;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public class KNearestNeighborsParams extends CommonParams<KNearestNeighborsParams>
    implements GroupCol {

  public KNearestNeighborsParams() {
    super(KNearestNeighborsParams.class.getSimpleName());

    setDefault(outputCol(), COL.SCORE);
    setDefault(group(), Group.USER);

    setDefault(weightedAverage(), WeightedAverage.SIMPLE);
    setDefault(minimumNeighbors(), 2);
    setDefault(k(), 20);
  }

  /*
   * Param Definitions
   */

  @Override
  public Param<Group> group() {
    return new Param<>(this, "group", "group:" + Group.descriptions());
  }

  public Param<WeightedAverage> weightedAverage() {
    return new Param<>(this, "weightedAverage", "TODO");
  }

  public Param<RatingNormalizer> normalizer() {
    return new Param<>(this, "ratingNormalizer", "TODO");
  }

  public Param<RatingSimilarityMeasurer> similarityMeasurer() {
    return new Param<>(
        this,
        "RatingSimilarityMeasurer",
        "The default value of the similarityMeasurer is the value returned when getSimilarityMeasurer() is called."
            + "[i.e. return new CosineSimilarityMeasurer().setGroup(getGroup())]");
  }

  public IntParam minimumNeighbors() {
    return new IntParam(
        this,
        "minimumNeighbors",
        "The number of minimum nearest neighbors.",
        ParamValidators.gtEq(0d));
  }

  public IntParam k() {
    return new IntParam(
        this,
        "k",
        "The number of nearest neighbors to use for prediction.",
        ParamValidators.gt(1d));
  }

  /*
   * Param Getters
   */

  @Override
  public Group getGroup() {
    // return getOrDefault(criterion());
    return get(group()).isEmpty() ? null : get(group()).get();
  }

  @Override
  public String getGroupCol() {
    return Objects.equals(getGroup(), Group.USER) ? getUserCol() : getItemCol();
  }

  public WeightedAverage getWeightedAverage() {
    return getOrDefault(weightedAverage());
  }

  public RatingNormalizer getNormalizer() {
    return get(normalizer()).isEmpty() ? null : get(normalizer()).get();

    //    return getOrDefault(ratingScaler()).get();
  }

  public RatingSimilarityMeasurer getSimilarityMeasurer() {
    //    return get(similarityMeasurer()).isEmpty()
    //        ? new CosineSimilarityMeasurer().setCriterion(getCriterion())
    //        : get(similarityMeasurer()).get();

    return get(similarityMeasurer()).isEmpty() ? null : get(similarityMeasurer()).get();
  }

  public Integer getMinimumNeighbors() {
    return (Integer) getOrDefault(minimumNeighbors());
  }

  public Integer getK() {
    return (Integer) getOrDefault(k());
  }

  /*
   *  Param Setters
   */

  @Override
  public KNearestNeighborsParams setGroup(Group value) {
    if (Objects.nonNull(value)) {
      set(group(), value);
    }

    return this;
  }

  @Override
  public KNearestNeighborsParams setGroupCol(String value) {
    if (Objects.nonNull(value)) {
      set(group(), Group.get(value));
    }

    return this;
  }

  public KNearestNeighborsParams setWeightedAverage(WeightedAverage value) {
    if (Objects.nonNull(value)) {
      set(weightedAverage(), value);
    }
    return this;
  }

  public KNearestNeighborsParams setNormalizer(RatingNormalizer value) {
    if (Objects.nonNull(value)) {
      set(normalizer(), value);
    }
    return this;
  }

  public KNearestNeighborsParams setSimilarityMeasurer(RatingSimilarityMeasurer value) {
    if (Objects.nonNull(value)) {
      set(similarityMeasurer(), value);
    }
    return this;
  }

  public KNearestNeighborsParams setMinimumNeighbors(Integer value) {
    if (Objects.nonNull(value)) {
      set(minimumNeighbors(), value);
    }
    return this;
  }

  public KNearestNeighborsParams setK(Integer value) {
    if (Objects.nonNull(value)) {
      set(k(), value);
    }
    return this;
  }

  @Override
  protected KNearestNeighborsParams self() {
    return this;
  }
}
