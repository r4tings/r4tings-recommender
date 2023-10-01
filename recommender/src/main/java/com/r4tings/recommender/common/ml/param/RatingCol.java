package com.r4tings.recommender.common.ml.param;

import org.apache.spark.ml.param.Param;

public interface RatingCol {

  /*
   * Param Definitions
   */
  Param<String> userCol();

  Param<String> itemCol();

  Param<String> ratingCol();

  /*
   * Param Getters
   */
  String getUserCol();

  String getItemCol();

  String getRatingCol();

  /*
   *  Param Setters
   */
  RatingCol setUserCol(String value);

  RatingCol setItemCol(String value);

  RatingCol setRatingCol(String value);
}
