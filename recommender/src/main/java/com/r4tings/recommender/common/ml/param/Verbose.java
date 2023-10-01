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
