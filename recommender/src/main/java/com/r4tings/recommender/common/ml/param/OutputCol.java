/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml.param;

import org.apache.spark.ml.param.Param;

public interface OutputCol {

  /*
   * Param Definitions
   */
  Param<String> outputCol();

  /*
   * Param Getters
   */
  String getOutputCol();

  /*
   *  Param Setters
   */
  OutputCol setOutputCol(String values);
}
