/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml.param;

import org.apache.spark.ml.param.BooleanParam;

public interface Verbose {

  /*
   * Param Definitions
   */
  BooleanParam verbose();

  /*
   * Param Getters
   */
  Boolean getVerbose();

  /*
   *  Param Setters
   */
  Verbose setVerbose(Boolean value);
}
