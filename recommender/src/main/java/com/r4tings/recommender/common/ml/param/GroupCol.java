package com.r4tings.recommender.common.ml.param;

import org.apache.spark.ml.param.Param;

public interface GroupCol {

  /*
   * Param Definitions
   */
  Param<Group> group();

  /*
   * Param Getters
   */
  Group getGroup();

  String getGroupCol();

  /*
   *  Param Setters
   */
  GroupCol setGroup(Group value);

  GroupCol setGroupCol(String values);
}
