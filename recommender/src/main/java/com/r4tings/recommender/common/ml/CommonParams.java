package com.r4tings.recommender.common.ml;

import com.r4tings.recommender.common.ml.param.OutputCol;
import com.r4tings.recommender.common.ml.param.RatingCol;
import com.r4tings.recommender.common.ml.param.Verbose;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.param.BooleanParam;
import org.apache.spark.ml.param.JavaParams;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.util.Identifiable$;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

@Slf4j
public abstract class CommonParams<T extends CommonParams<T>> extends JavaParams
    implements Verbose, OutputCol, RatingCol {

  private final String uid;

  public CommonParams(String prefix) {
    this.uid = Identifiable$.MODULE$.randomUID(prefix);

    setDefault(verbose(), Boolean.FALSE);
    setDefault(outputCol(), COL.OUTPUT);
    setDefault(userCol(), COL.USER);
    setDefault(itemCol(), COL.ITEM);
    setDefault(ratingCol(), COL.RATING);
  }

  @Override
  public Estimator copy(ParamMap extra) {
    return defaultCopy(extra);
  }

  @Override
  public String uid() {
    return this.uid;
  }

  /*
   * Param Definitions
   */

  @Override
  public BooleanParam verbose() {
    return new BooleanParam(this, "verbose", "Enable verbose logging");
  }

  @Override
  public Param<String> outputCol() {
    return new Param<>(this, "outputCol", "output column name");
  }

  @Override
  public Param<String> userCol() {
    return new Param<>(this, "userCol", "user column name ");
  }

  @Override
  public Param<String> itemCol() {
    return new Param<>(this, "itemCol", "item column name ");
  }

  @Override
  public Param<String> ratingCol() {
    return new Param<>(this, "ratingCol", "rating column name");
  }

  /*
   * Param Getters
   */

  @Override
  public Boolean getVerbose() {
    return (Boolean) getOrDefault(verbose());
  }

  @Override
  public String getOutputCol() {
    return getOrDefault(outputCol());
  }

  @Override
  public String getUserCol() {
    return getOrDefault(userCol());
  }

  @Override
  public String getItemCol() {
    return getOrDefault(itemCol());
  }

  @Override
  public String getRatingCol() {
    return getOrDefault(ratingCol());
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
