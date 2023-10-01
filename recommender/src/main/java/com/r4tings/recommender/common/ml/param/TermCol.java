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
