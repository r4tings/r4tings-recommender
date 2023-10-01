package com.r4tings.recommender.model.svd;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.common.ml.CommonParams;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamValidators;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public class BaselineSingleValueDecompositionParams
    extends CommonParams<BaselineSingleValueDecompositionParams> {

  public BaselineSingleValueDecompositionParams() {
    super(BaselineSingleValueDecompositionParams.class.getSimpleName());
    setDefault(outputCol(), COL.SCORE);
  }

  /*
   * Param Definitions
   */

  public Param<CommonEstimator> baselinePredictor() {
    return new Param<>(this, "baselinePredictor", "TODO");
  }

  public Param<CommonEstimator> singleValueDecomposition() {
    return new Param<>(this, "singleValueDecomposition", "TODO");
  }

  public Param<Model> baselineModel() {
    return new Param<>(this, "baselineModel", "TODO");
  }

  public Param<Model> singleValueDecompositionModel() {
    return new Param<>(this, "singleValueDecompositionModel", "TODO");
  }

  // TODO DIMENSION 은 상수이긴하나 COL이 아님
  public IntParam k() {
    return new IntParam(this, COL.DIMENSION, "TODO", ParamValidators.gt(0d));
  }

  /*
   * Param Getters
   */

  public CommonEstimator getBaselinePredictor() {
    return get(baselinePredictor()).isEmpty() ? null : get(baselinePredictor()).get();
  }

  public CommonEstimator getSingleValueDecomposition() {
    return get(singleValueDecomposition()).isEmpty() ? null : get(singleValueDecomposition()).get();
  }

  public Integer getK() {
    return (Integer) getOrDefault(k());
  }

  public Model getBaselineModel() {
    return get(baselineModel()).isEmpty() ? null : get(baselineModel()).get();
  }

  public Model getSingleValueDecompositionModel() {
    return get(singleValueDecompositionModel()).isEmpty()
        ? null
        : get(singleValueDecompositionModel()).get();
  }

  /*
   *  Param Setters
   */

  public BaselineSingleValueDecompositionParams setBaselinePredictor(CommonEstimator value) {
    if (Objects.nonNull(value)) {
      set(baselinePredictor(), value);
    }
    return this;
  }

  public BaselineSingleValueDecompositionParams setSingleValueDecomposition(CommonEstimator value) {
    if (Objects.nonNull(value)) {
      set(singleValueDecomposition(), value);
    }
    return this;
  }

  public BaselineSingleValueDecompositionParams setK(Integer value) {
    if (Objects.nonNull(value)) {
      set(k(), value);
    }

    return this;
  }

  public BaselineSingleValueDecompositionParams setBaselineModel(Model value) {
    if (Objects.nonNull(value)) {
      set(baselineModel(), value);
    }
    return this;
  }

  public BaselineSingleValueDecompositionParams setSingleValueDecompositionModel(Model value) {
    if (Objects.nonNull(value)) {
      set(singleValueDecompositionModel(), value);
    }
    return this;
  }

  @Override
  protected BaselineSingleValueDecompositionParams self() {
    return this;
  }
}
