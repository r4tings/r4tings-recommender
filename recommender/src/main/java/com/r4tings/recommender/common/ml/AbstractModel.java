/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml;

import com.r4tings.recommender.common.ml.param.OutputCol;
import com.r4tings.recommender.common.ml.param.RatingCol;
import com.r4tings.recommender.common.ml.param.Verbose;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.param.BooleanParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.util.Identifiable$;
import org.apache.spark.sql.types.StructType;

import static com.r4tings.recommender.common.Constants.COL;

public abstract class AbstractModel extends Model<AbstractModel>
    implements Verbose, OutputCol, RatingCol {

  private final String uid;

  public AbstractModel(String prefix) {
    this.uid = Identifiable$.MODULE$.randomUID(prefix);

    setDefault(verbose(), Boolean.FALSE);
    setDefault(outputCol(), COL.OUTPUT);
    setDefault(userCol(), COL.USER);
    setDefault(itemCol(), COL.ITEM);
    setDefault(ratingCol(), COL.RATING);
  }

  @Override
  public StructType transformSchema(StructType schema) {
    return schema;
  }

  @Override
  public AbstractModel copy(ParamMap extra) {
    return defaultCopy(extra);
  }

  @Override
  public String uid() {
    return uid;
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
}
