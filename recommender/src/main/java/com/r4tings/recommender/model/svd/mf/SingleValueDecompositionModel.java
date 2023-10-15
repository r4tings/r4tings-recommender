/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.mf;

import com.r4tings.recommender.common.ml.CommonModel;
import com.r4tings.recommender.common.util.SparkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.spark.ml.param.Param;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.JavaConverters;
import scala.collection.mutable.WrappedArray;

import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class SingleValueDecompositionModel extends CommonModel<SingleValueDecompositionModel> {

  public SingleValueDecompositionModel() {
    super(SingleValueDecompositionModel.class.getSimpleName());
  }

  @Override
  protected SingleValueDecompositionModel self() {
    return this;
  }

  @Override
  public Dataset<Row> transform(Dataset<?> ratings) {

    // Create a copy, unknown error happen when the operation is applied on origin dataset
    Dataset<Row> ratingDS =
        Objects.requireNonNull(ratings)
            .select(
                SparkUtils.validateInputColumns(
                    ratings.columns(), getUserCol(), getItemCol(), getRatingCol()));

    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
      log.debug("params:\n{}", explainParams());
      log.info("ratingDS\n{}", ratingDS.showString(SPARK.DEFAULT_NUM_ROWS, 0, false));
    }

    /*
    if (Objects.equals(getVerbose(), Boolean.TRUE)) {
           log.info("U: {}", matrixU);
      log.info("S: {}", matrixS);
      log.info("V:\n{}", matrixV);*/

    // log.info("matrixU rows:{}", matrixU.toRowMatrix().rows().collect());
    // log.info("matrixS rows:{}", matrixS.toArray());
    // log.info("matrixV rows:{}", matrixV.toString());

    /*
       // 스칼라 삼중곱 TODO 함수명 변경
       UserDefinedFunction hadamardDotProduct = udf(new HadamardDotProduct(), DataTypes.DoubleType);

       // 실제로 필요한건 unrated에 대한 것 TODO -> 인덱스를 ID로 변환해서 내보내야함..
       Dataset<Row> evalDS =
           userFeatureDS
               .crossJoin(itemFeatureDS)
               .withColumn(
                   COL.USV,
                   hadamardDotProduct.apply(
                       col(COL.ROW_FEATURES), lit(features), col(COL.COL_FEATURES)));

       showPivot(log, "evalDS", evalDS, COL.ROW_INDEX, COL.COL_INDEX, COL.USV, 7);


     }
    */

    UDF3<WrappedArray<Double>, WrappedArray<Double>, WrappedArray<Double>, Double>
        udfConvertReconstructRating = SingleValueDecompositionModel::reconstructRating;

    return ratingDS
        .join(getUserFeatureDS(), getUserCol())
        .join(getItemFeatureDS(), getItemCol())
        //   .drop(getRatingCol(), COL.ROW_INDEX, COL.COL_INDEX)
        // .selectExpr(COL.USER, COL.ITEM, COL.BIAS, COL.ROW_FEATURES, COL.COL_FEATURES)
        .withColumn(
            getOutputCol(),
            udf(udfConvertReconstructRating, DataTypes.DoubleType)
                .apply(col(COL.USER_FEATURES), lit(getFeatureWeights()), col(COL.ITEM_FEATURES)))
        .withColumn(COL.RESIDUAL, col(getRatingCol()).minus(col(getOutputCol())));
  }

  public static Double reconstructRating(
      WrappedArray<Double> u, WrappedArray<Double> s, WrappedArray<Double> v) {

    return new ArrayRealVector(JavaConverters.seqAsJavaList(u).toArray(new Double[0]))
        // Element-by-element multiplication
        .ebeMultiply(
            new ArrayRealVector(JavaConverters.seqAsJavaList(s).toArray(new Double[0]))) // vector
        .dotProduct(
            new ArrayRealVector(JavaConverters.seqAsJavaList(v).toArray(new Double[0]))); // scalar
  }

  /*
   * Param Definitions
   */

  public Param<Dataset<Row>> userFeatureDS() {
    return new Param<>(this, "userFeatureDS", "TODO");
  }

  public Param<Double[]> featureWeights() {
    return new Param<>(this, "features", "TODO");
  }

  public Param<Dataset<Row>> itemFeatureDS() {
    return new Param<>(this, "itemFeatureDS", "TODO");
  }

  /*
   * Param Getters
   */

  public Dataset<Row> getUserFeatureDS() {
    return get(userFeatureDS()).isEmpty() ? null : get(userFeatureDS()).get();
  }

  public Double[] getFeatureWeights() {
    return get(featureWeights()).isEmpty() ? null : get(featureWeights()).get();
  }

  public Dataset<Row> getItemFeatureDS() {
    return get(itemFeatureDS()).isEmpty() ? null : get(itemFeatureDS()).get();
  }

  public SingleValueDecompositionModel setUserFeatureDS(Dataset<Row> value) {
    if (Objects.nonNull(value)) {
      set(userFeatureDS(), value);
    }

    return this;
  }

  public SingleValueDecompositionModel setItemFeatureDS(Dataset<Row> value) {
    if (Objects.nonNull(value)) {
      set(itemFeatureDS(), value);
    }

    return this;
  }

  public SingleValueDecompositionModel setFeatureWeights(double[] value) {
    if (Objects.nonNull(value)) {
      set(featureWeights(), Arrays.stream(value).boxed().toArray(Double[]::new));
    }

    return this;
  }
}
