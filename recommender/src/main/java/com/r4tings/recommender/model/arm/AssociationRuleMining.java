/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.arm;

import com.r4tings.recommender.common.ml.AbstractRecommender;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@Slf4j
public class AssociationRuleMining extends AbstractRecommender {

  private final AssociationRuleMiningParams params;

  public AssociationRuleMining(AssociationRuleMiningParams params) {
    super(params);
    this.params = params;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS, Object[] ids) {

    /*
     * Step 1: Prepare LHS & RHS Itemsets
     */

    Long totalTransactions = ratingDS.select(params.getUserCol()).distinct().count();

    Dataset<Row> lhsDS =
        ratingDS
            .where(col(params.getItemCol()).isin(ids))
            .select(col(params.getItemCol()), col(params.getUserCol()))
            .groupBy(col(params.getItemCol()).as(COL.LHS))
            .agg(collect_list(params.getUserCol()).as(COL.LHS_TRANSACTIONS))
            .withColumn(COL.LHS_SUPPORT, size(col(COL.LHS_TRANSACTIONS)).divide(totalTransactions));

    Dataset<Row> rhsDS =
        ratingDS
            .where(not(col(params.getItemCol()).isin(ids)))
            .select(col(params.getItemCol()), col(params.getUserCol()))
            .groupBy(col(params.getItemCol()).as(COL.RHS))
            .agg(collect_list(params.getUserCol()).as(COL.RHS_TRANSACTIONS))
            .withColumn(COL.RHS_SUPPORT, size(col(COL.RHS_TRANSACTIONS)).divide(totalTransactions))
            .where(col(COL.RHS_SUPPORT).$greater$eq(params.getMinSupport()));

    /*
     * Step 2: Build Strong rules
     */

    Dataset<Row> candidateRuleDS =
        lhsDS
            .hint(SPARK.HINT_BROADCAST)
            .crossJoin(rhsDS)
            .withColumn(
                COL.SUPPORT,
                InterestMeasure.SUPPORT
                    .invoke(params.getVerbose())
                    .apply(
                        size(array_intersect(col(COL.LHS_TRANSACTIONS), col(COL.RHS_TRANSACTIONS))),
                        lit(totalTransactions)))
            .withColumn(
                COL.CONFIDENCE,
                InterestMeasure.CONFIDENCE
                    .invoke(params.getVerbose())
                    .apply(col(COL.SUPPORT), col(COL.LHS_SUPPORT)));

    Dataset<Row> strongRuleDS =
        candidateRuleDS.where(
            col(COL.SUPPORT)
                .$greater$eq(params.getMinSupport())
                .and(col(COL.CONFIDENCE).$greater$eq(params.getMinConfidence())));

    /*
     * Step 3: Compute additional Interest Measures
     */

    Dataset<Row> associationRuleDS =
        strongRuleDS
            .withColumn(
                COL.LIFT,
                InterestMeasure.LIFT
                    .invoke(params.getVerbose())
                    .apply(col(COL.CONFIDENCE), col(COL.RHS_SUPPORT)))
            // divide by zero 대응 향상도가 1일때 발생 support가 0인 경우는 최소 지지도로 제거됨
            .withColumn(
                COL.CONVICTION,
                //  For instance, in the case of a perfect confidence score, the denominator becomes
                // 0 (due to 1 - 1) for which the conviction score is defined as 'inf'.
                // Conviction is infinite for logical implications (confidence 1), and is 1 if
                // X and Y are independent
                when(col(COL.CONFIDENCE).equalTo(1d), Double.POSITIVE_INFINITY)
                    .otherwise(
                        InterestMeasure.CONVICTION
                            .invoke(params.getVerbose())
                            .apply(col(COL.RHS_SUPPORT), col(COL.CONFIDENCE))))
            .withColumn(
                COL.LEVERAGE,
                InterestMeasure.LEVERAGE
                    .invoke(params.getVerbose())
                    .apply(col(COL.SUPPORT), col(COL.LHS_SUPPORT), col(COL.RHS_SUPPORT)));

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("totalTransactions: {}", totalTransactions);
      log.info("lhsDS\n{}", lhsDS.showString(10, 0, false));
      log.info(
          "rhsDS\n{}", rhsDS.orderBy(length(col(COL.RHS)), col(COL.RHS)).showString(10, 0, false));
      log.info(
          "candidateRuleDS\n{}",
          candidateRuleDS.orderBy(length(col(COL.RHS)), col(COL.RHS)).showString(10, 0, false));
      log.info(
          "strongRuleDS\n{}",
          strongRuleDS.orderBy(length(col(COL.RHS)), col(COL.RHS)).showString(10, 0, false));
      log.info("associationRuleDS\n{}", associationRuleDS.showString(10, 0, false));
    }

    return associationRuleDS.select(
        col(COL.RHS).as(params.getItemCol()),
        col(COL.SUPPORT),
        col(COL.CONFIDENCE),
        col(COL.LIFT),
        col(COL.CONVICTION),
        col(COL.LEVERAGE));
  }
}
