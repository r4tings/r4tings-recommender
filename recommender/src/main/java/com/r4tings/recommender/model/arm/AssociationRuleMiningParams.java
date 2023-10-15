/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.arm;

import com.r4tings.recommender.common.ml.CommonParams;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.ParamValidators;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public class AssociationRuleMiningParams extends CommonParams<AssociationRuleMiningParams> {

  public AssociationRuleMiningParams() {
    super(AssociationRuleMiningParams.class.getSimpleName());

    setDefault(outputCol(), COL.SUPPORT);
  }

  /*
   * Param Definitions
   */

  public DoubleParam minSupport() {
    return new DoubleParam(
        this,
        "minSupport",
        "the minimal support level of a frequent pattern",
        ParamValidators.inRange(0d, 1d));
  }

  public DoubleParam minConfidence() {
    return new DoubleParam(
        this,
        "minConfidence",
        "minimal confidence for generating Association Rule",
        ParamValidators.inRange(0d, 1d));
  }

  /*
   * Param Getters
   */

  public Double getMinSupport() {
    return (Double) getOrDefault(minSupport());
  }

  public Double getMinConfidence() {
    return (Double) getOrDefault(minConfidence());
  }

  /*
   *  Param Setters
   */

  public AssociationRuleMiningParams setMinSupport(Double value) {
    if (Objects.nonNull(value)) {
      set(minSupport(), value);
    }

    return this;
  }

  public AssociationRuleMiningParams setMinConfidence(Double value) {
    if (Objects.nonNull(value)) {
      set(minConfidence(), value);
    }

    return this;
  }

  @Override
  protected AssociationRuleMiningParams self() {
    return this;
  }
}
