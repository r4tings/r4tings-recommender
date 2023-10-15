/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd.mf;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.common.util.SparkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.ParamValidators;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static com.r4tings.recommender.common.util.SparkUtils.zipWithIndex;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.length;

@Slf4j
public class SingleValueDecomposition extends CommonEstimator<SingleValueDecomposition> {

  public SingleValueDecomposition() {
    super(SingleValueDecomposition.class.getSimpleName());

    setDefault(outputCol(), COL.USV);
  }

  @Override
  protected SingleValueDecomposition self() {
    return this;
  }

  @Override
  public SingleValueDecompositionModel fit(Dataset ratings) {

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
     * Build a rating (residual) matrix from the rating data
     * buildCoordinateMatrix TODO 왜 인덱스드가 아니고 코디네이트 매트릭스로 만들었을끼?  JavaRDD<Vector> 변환하기 싫어서?
     */

    /*
     * TODO 참고
     * https://www.mikulskibartosz.name/row-number-in-apache-spark-window-row-number-rank-and-dense-rank/
     * https://jaceklaskowski.gitbooks.io/mastering-spark-sql/spark-sql-functions-windows.html
     * https://jjeong.tistory.com/1019
     */

    /*
     * Step 1: prepare a matrix containing the rating data.
     */

    Dataset<Row> userIndexDS =
        zipWithIndex(
            ratingDS
                .select(getUserCol())
                .distinct()
                // TODO 계산하기 검증 후 이후 일괄 삭제
                .orderBy(length(col(getUserCol())), col(getUserCol())),
            COL.USER_INDEX,
            0);

    Dataset<Row> itemIndexDS =
        zipWithIndex(
            ratingDS
                .select(getItemCol())
                .distinct()
                // TODO 계산하기 검증 후 이후 일괄 삭제
                .orderBy(length(col(getItemCol())), col(getItemCol())),
            COL.ITEM_INDEX,
            0);

    RDD<MatrixEntry> matrixEntryRDD =
        ratingDS
            .join(userIndexDS.hint(SPARK.HINT_BROADCAST), getUserCol())
            .join(itemIndexDS.hint(SPARK.HINT_BROADCAST), getItemCol())
            .select(COL.USER_INDEX, COL.ITEM_INDEX, getRatingCol())
            .toJavaRDD()
            .map(
                row ->
                    MatrixEntry.apply(
                        row.getAs(COL.USER_INDEX),
                        row.getAs(COL.ITEM_INDEX),
                        row.getAs(getRatingCol())))
            .rdd();

    /*
    Create a RowMatrix from JavaRDD<Vector>.
    CoordinateMatrix coordinateMatrix = new CoordinateMatrix(matrixEntryJavaRDD.rdd(), 5, 10);
    https://medium.com/balabit-unsupervised/scalable-sparse-matrix-multiplication-in-apache-spark-c79e9ffc0703
    */
    CoordinateMatrix coordinateMatrix = new CoordinateMatrix(matrixEntryRDD);

    /*
     * Step 2: compute its factorization.
     */

    // Compute 20 largest singular values and corresponding singular vectors
    // Compute the top 5 singular values and corresponding singular vectors.

    if (coordinateMatrix.numCols() < getK()) {
      // TODO 왜 k가 cols인데 출력결과는 cols-1개일까
      log.warn("k: {} cols: {}", getK(), coordinateMatrix.numCols());
      setK((int) coordinateMatrix.numCols());
    }

    SingularValueDecomposition<IndexedRowMatrix, Matrix> svd =
        coordinateMatrix.toIndexedRowMatrix().computeSVD(getK(), true, 1.0E-9d);

    SparkSession spark = ratingDS.sparkSession();

    /*
     * The U factor is a RowMatrix.
     */
    IndexedRowMatrix matrixU = svd.U();

    Dataset<Row> userFeatureDS =
        spark
            .createDataFrame(
                matrixU
                    .rows()
                    .toJavaRDD()
                    .map(row -> RowFactory.create(row.index(), row.vector().toArray())),
                DataTypes.createStructType(
                    Arrays.asList(
                        DataTypes.createStructField(
                            COL.USER_INDEX, DataTypes.LongType, false, Metadata.empty()),
                        DataTypes.createStructField(
                            COL.USER_FEATURES,
                            DataTypes.createArrayType(DataTypes.DoubleType),
                            false,
                            Metadata.empty()))))
            .join(userIndexDS.hint(SPARK.HINT_BROADCAST), COL.USER_INDEX);

    log.info("userFeatureDS\n{}", userFeatureDS.showString(10, 0, false));

    /*
     * The singular values are stored in a local dense vector.
     */

    Vector matrixS = svd.s();

    double[] features = matrixS.toArray();

    log.info("features: {}", Arrays.toString(features));

    /*
     * The V factor is a local dense matrix.
     */
    Matrix matrixV = svd.V();

    Dataset<Row> itemFeatureDS =
        spark
            .createDataFrame(
                JavaConverters.seqAsJavaList(
                    matrixV
                        .rowIter()
                        .zipWithIndex()
                        .map(tuple -> RowFactory.create(tuple._2(), tuple._1().toArray()))
                        .toSeq()),
                DataTypes.createStructType(
                    Arrays.asList(
                        DataTypes.createStructField(
                            COL.ITEM_INDEX, DataTypes.IntegerType, false, Metadata.empty()),
                        DataTypes.createStructField(
                            COL.ITEM_FEATURES,
                            DataTypes.createArrayType(DataTypes.DoubleType),
                            false,
                            Metadata.empty()))))
            .join(itemIndexDS.hint(SPARK.HINT_BROADCAST), COL.ITEM_INDEX);

    log.info("itemFeatureDS\n{}", itemFeatureDS.showString(10, 0, false));

    return new SingleValueDecompositionModel()
        .setUserFeatureDS(userFeatureDS.select(getUserCol(), COL.USER_FEATURES))
        .setFeatureWeights(features)
        .setItemFeatureDS(itemFeatureDS.select(getItemCol(), COL.ITEM_FEATURES))
        .setUserCol(getUserCol())
        .setItemCol(getItemCol())
        .setRatingCol(getRatingCol())
        .setOutputCol(getOutputCol()); // TODO Model에 일괄 적용
  }

  /*
   * Param Definitions
   */

  // TODO DIMENSION 은 상수이긴하나 COL이 아님
  public IntParam k() {
    return new IntParam(this, COL.DIMENSION, "TODO", ParamValidators.gt(0d));
  }

  /*
   * Param Getters
   */

  public int getK() {
    return (Integer) getOrDefault(k());
  }

  /*
   *  Param Setters
   */

  public SingleValueDecomposition setK(Integer value) {
    if (Objects.nonNull(value)) {
      set(k(), value);
    }

    return this;
  }
}
