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
