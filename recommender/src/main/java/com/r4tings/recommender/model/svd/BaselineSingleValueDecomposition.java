/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.svd;

import com.r4tings.recommender.common.ml.AbstractRecommender;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import com.r4tings.recommender.model.svd.baseline.MeanRatingBaselineModel;
import com.r4tings.recommender.model.svd.mf.SingleValueDecompositionModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.mutable.WrappedArray;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class BaselineSingleValueDecomposition extends AbstractRecommender {

  private final BaselineSingleValueDecompositionParams params;

  public BaselineSingleValueDecomposition(BaselineSingleValueDecompositionParams params) {
    super(params);
    this.params = params;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS, Object[] ids) {

    /*
     * Step 1:
     */

    /*    CommonEstimator baselinePredictor = params.getBaselinePredictor();
    MeanRatingBaselineModel baselineModel =
        (MeanRatingBaselineModel) Objects.requireNonNull(baselinePredictor).fit(ratingDS);

    Dataset<Row> baselineResidualRatingDS = baselineModel.transform(ratingDS);
    */

    MeanRatingBaselineModel baselineModel = (MeanRatingBaselineModel) params.getBaselineModel();
    Dataset<Row> baselineResidualRatingDS = baselineModel.transform(ratingDS);

    /*

    * Step 2:
    */

    /*
        // TODO 이것도 param으로 baseline 모델 포함해서 복합관계 해소 필요
        SingleValueDecomposition singleValueDecomposition =
            new SingleValueDecomposition()
                .setVerbose(params.getVerbose())
                .setK(params.getK())
                .setUserCol(params.getUserCol())
                .setItemCol(params.getItemCol())
                .setRatingCol(COL.RESIDUAL);

        // truncated SVD
        SingleValueDecompositionModel svdModel = singleValueDecomposition.fit(baselineResidualRatingDS);
    */

    SingleValueDecompositionModel svdModel =
        (SingleValueDecompositionModel) params.getSingleValueDecompositionModel();

    // SingleValueDecompositionModel svdModel =
    // singleValueDecomposition.fit(baselineModel.transform(ratingDS)); // TODO 또는 파이프라인

    /*
     * Step 3:
     */

    Dataset<Row> userBiasDS = baselineModel.getUserBiasDS();
    Dataset<Row> userFeatureDS = svdModel.getUserFeatureDS();

    Dataset<Row> lhsDS =
        userBiasDS
            .where(col(params.getUserCol()).isin(ids))
            .join(userFeatureDS, params.getUserCol());

    Dataset<Row> itemBiasDS = baselineModel.getItemBiasDS();
    Dataset<Row> itemFeatureDS = svdModel.getItemFeatureDS();

    Dataset<Row> rhsDS =
        SparkUtils.leftAntiJoin(
                itemBiasDS,
                ratingDS
                    .where(col(params.getUserCol()).isin(ids))
                    .select(col(params.getItemCol()).as(params.getItemCol()))
                    .hint(SPARK.HINT_BROADCAST),
                params.getItemCol())
            .join(itemFeatureDS, params.getItemCol());

    UDF3<WrappedArray<Double>, WrappedArray<Double>, WrappedArray<Double>, Double>
        udfConvertReconstructRating = SingleValueDecompositionModel::reconstructRating;

    Dataset<Row> reconstructedRatingScoreDS =
        lhsDS
            .hint(SPARK.HINT_BROADCAST)
            .crossJoin(rhsDS)
            .withColumn(
                COL.BIAS,
                (lit(baselineModel.getOverallMeanRating())
                    .plus(col(COL.USER_BIAS).plus(col(COL.ITEM_BIAS)))))
            .withColumn(
                COL.USV, // TODO singleValueDecomposition.getOutputCol();
                udf(udfConvertReconstructRating, DataTypes.DoubleType)
                    .apply(
                        col(COL.USER_FEATURES),
                        lit(svdModel.getFeatureWeights()),
                        col(COL.ITEM_FEATURES)))
            .withColumn(params.getOutputCol(), col(COL.BIAS).plus(col(COL.USV)));

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("baselineResidualRatingDS\n{}", baselineResidualRatingDS.showString(10, 0, false));
      VerboseUtils.showPivot(baselineResidualRatingDS, COL.USER, COL.ITEM, COL.BIAS, 7);
      VerboseUtils.showPivot(baselineResidualRatingDS, COL.USER, COL.ITEM, COL.RESIDUAL, 7);
      log.info("userBiasDS\n{}", userBiasDS.showString(10, 0, false));
      log.info("userFeatureDS\n{}", userFeatureDS.showString(10, 0, false));
      log.info("lhsDS\n{}", lhsDS.showString(10, 0, false));
      log.info("itemBiasDS\n{}", itemBiasDS.showString(10, 0, false));
      log.info("itemFeatureDS\n{}", itemFeatureDS.showString(10, 0, false));
      log.info("rhsDS\n{}", rhsDS.showString(10, 0, false));

      log.info(
          "reconstructedRatingScoreDS\n{}", reconstructedRatingScoreDS.showString(10, 0, false));
    }

    return reconstructedRatingScoreDS.select(params.getItemCol(), params.getOutputCol());
  }
}
