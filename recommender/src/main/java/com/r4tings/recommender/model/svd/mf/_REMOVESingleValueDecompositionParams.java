package com.r4tings.recommender.model.svd.mf;

import com.r4tings.recommender.common.ml.CommonParams;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.ParamValidators;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public class _REMOVESingleValueDecompositionParams
    extends CommonParams<_REMOVESingleValueDecompositionParams> {

  public _REMOVESingleValueDecompositionParams() {
    super(_REMOVESingleValueDecompositionParams.class.getSimpleName());

    setDefault(outputCol(), COL.SCORE);

    setDefault(k(), 25);
  }

  @Override
  protected _REMOVESingleValueDecompositionParams self() {
    return this;
  }

  /*
   * Param Definitions
   */

  // TODO DIMENSION 은 상수이긴하나 COL이 아님
  public IntParam k() {
    return new IntParam(this, COL.DIMENSION, "TODO", ParamValidators.gt(0d));
  }

  /*
   * Param Getters
   */

  public int getK() {
    return (Integer) getOrDefault(k());
  }

  /*
   *  Param Setters
   */

  @Override
  public _REMOVESingleValueDecompositionParams setVerbose(Boolean value) {
    if (Objects.nonNull(value)) {
      set(verbose(), value);
    }

    return this;
  }

  @Override
  public _REMOVESingleValueDecompositionParams setUserCol(String value) {
    if (Objects.nonNull(value)) {
      set(userCol(), value);
    }

    return this;
  }

  @Override
  public _REMOVESingleValueDecompositionParams setItemCol(String value) {
    if (Objects.nonNull(value)) {
      set(itemCol(), value);
    }

    return this;
  }

  @Override
  public _REMOVESingleValueDecompositionParams setRatingCol(String value) {
    if (Objects.nonNull(value)) {
      set(ratingCol(), value);
    }

    return this;
  }

  @Override
  public _REMOVESingleValueDecompositionParams setOutputCol(String value) {
    if (Objects.nonNull(value)) {
      set(outputCol(), value);
    }

    return this;
  }

  public _REMOVESingleValueDecompositionParams setK(Integer value) {
    if (Objects.nonNull(value)) {
      set(k(), value);
    }

    return this;
  }
}
