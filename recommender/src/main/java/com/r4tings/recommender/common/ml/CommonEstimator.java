/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public abstract class CommonEstimator<T extends CommonEstimator<T>> extends AbstractEstimator {

  public CommonEstimator(String prefix) {
    super(prefix);

    setDefault(outputCol(), COL.BIAS);
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

  protected abstract T self();
}
