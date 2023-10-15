/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml.param;

import org.apache.spark.ml.param.Param;

public interface TermCol {

  /*
   * Param Definitions
   */
  Param<String> termCol();

  /*
   * Param Getters
   */
  String getTermCol();

  /*
   *  Param Setters
   */
  TermCol setTermCol(String values);
}
