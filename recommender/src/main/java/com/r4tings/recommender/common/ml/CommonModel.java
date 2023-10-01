package com.r4tings.recommender.common.ml;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

public abstract class CommonModel<T extends CommonModel<T>> extends AbstractModel {

  public CommonModel(String prefix) {
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
