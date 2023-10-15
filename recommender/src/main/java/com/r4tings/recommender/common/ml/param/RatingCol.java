/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
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
