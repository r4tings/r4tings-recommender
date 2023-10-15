/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.knn;

import com.r4tings.recommender.common.ml.AbstractRecommender;
import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.common.util.SparkUtils;
import com.r4tings.recommender.common.util.VerboseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;
import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;
import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

@SuppressWarnings("ALL")
@Slf4j
public class KNearestNeighbors extends AbstractRecommender {

  private final KNearestNeighborsParams params;

  public KNearestNeighbors(KNearestNeighborsParams params) {
    super(params);
    this.params = params;
  }

  @Override
  protected Dataset<Row> execute(Dataset<Row> ratingDS, Object[] ids) {

    /*
     * Step 1: Neighborhood selection
     */

    Dataset<Row> userRatedItemDS =
        ratingDS.where(col(params.getUserCol()).isin(ids)).select(params.getItemCol());

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      log.info("userRatedItems.count: {}", userRatedItemDS.count());
    }

    Dataset<Row> neighborsRatingDS = getNeighborsRating(ratingDS, ids, userRatedItemDS);

    /*
     * Step 2: Neighborhood similarity computation
     */
    Dataset<Row> neighborSimilarityDS =
        getNeighborsSimilarity(
            ratingDS, ids, userRatedItemDS) /*.persist(StorageLevel.MEMORY_ONLY())*/;

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      // Drop NA
      VerboseUtils.showPivot(
          neighborSimilarityDS, COL.LHS, COL.RHS, params.getSimilarityMeasurer().getOutputCol(), 7);
    }

    if (!Objects.equals(params.getWeightedAverage(), WeightedAverage.SIMPLE)) {
      Dataset<Row> groupStatsDS = getGroupStats(ratingDS);

      neighborsRatingDS =
          neighborsRatingDS
              .join(
                  groupStatsDS.hint(SPARK.HINT_BROADCAST),
                  neighborsRatingDS.col(COL.RHS).equalTo(groupStatsDS.col(params.getGroupCol())))
              .drop(params.getGroupCol())
              .withColumnRenamed(COL.MEAN, COL.RHS_MEAN)
              .withColumnRenamed(COL.STDDEV, COL.RHS_STDDEV);

      neighborSimilarityDS =
          neighborSimilarityDS
              .join(
                  groupStatsDS.hint(SPARK.HINT_BROADCAST),
                  neighborSimilarityDS.col(COL.LHS).equalTo(groupStatsDS.col(params.getGroupCol())))
              .drop(params.getGroupCol())
              .withColumnRenamed(COL.MEAN, COL.LHS_MEAN)
              .withColumnRenamed(COL.STDDEV, COL.LHS_STDDEV);
    }

    /*
     * Step 3: Rating prediction
     */

    // TODO 컬럼으로 리턴받는건 어떤지?
    Column outputColumn;
    switch (params.getWeightedAverage()) {
      case SIMPLE:
        outputColumn =
            WeightedAverage.SIMPLE
                .invoke(params.getVerbose(), params.getMinimumNeighbors())
                .apply(
                    slice(
                        sort_array(
                            // TODO ScalaUtils 일괄 제거
                            collect_list(
                                struct(
                                    col(params.getSimilarityMeasurer().getOutputCol()),
                                    col(params.getRatingCol()))),
                            false),
                        1,
                        params.getK()))
                .as(params.getOutputCol());
        break;
      case MEAN_CENTERING:
        outputColumn =
            first(col(COL.LHS_MEAN))
                .plus(
                    WeightedAverage.MEAN_CENTERING
                        .invoke(params.getVerbose(), params.getMinimumNeighbors())
                        .apply(
                            slice(
                                sort_array(
                                    collect_list(
                                        struct(
                                            col(params.getSimilarityMeasurer().getOutputCol()),
                                            col(params.getRatingCol()),
                                            col(COL.RHS_MEAN))),
                                    false),
                                1,
                                params.getK())))
                .as(params.getOutputCol());

        break;
      case Z_SCORE:
        outputColumn =
            first(col(COL.LHS_MEAN))
                .plus(
                    first(col(COL.LHS_STDDEV))
                        .multiply(
                            WeightedAverage.Z_SCORE
                                .invoke(params.getVerbose(), params.getMinimumNeighbors())
                                .apply(
                                    slice(
                                        sort_array(
                                            collect_list(
                                                struct(
                                                    col(
                                                        params
                                                            .getSimilarityMeasurer()
                                                            .getOutputCol()),
                                                    col(params.getRatingCol()),
                                                    col(COL.RHS_MEAN),
                                                    col(COL.RHS_STDDEV))),
                                            false),
                                        1,
                                        params.getK()))))
                .as(params.getOutputCol());
        break;
      default:
        throw new UnsupportedOperationException();
    }

    Dataset<Row> nearestNeighborsDS = neighborsRatingDS.join(neighborSimilarityDS, COL.RHS);
    //         neighborsRatingDS.cache().join(neighborSimilarityDS.cache(), COL.RHS); 4m46s + 4s

    /*
       log.info("neighborsRatingDS.count {}", neighborsRatingDS.count());
       log.info("neighborsRatingDS.schema {}", neighborsRatingDS.schema().prettyJson());
       neighborsRatingDS.explain();
       log.info("neighborSimilarityDS.count {}", neighborSimilarityDS.count());
       log.info("neighborSimilarityDS.schema {}", neighborSimilarityDS.schema().prettyJson());
       neighborSimilarityDS.explain();
       log.info("nearestNeighborsDS.count {}", nearestNeighborsDS.count());
       log.info("nearestNeighborsDS.schema {}", nearestNeighborsDS.schema().prettyJson());
       nearestNeighborsDS.explain();
    */
    if (Objects.equals(params.getGroup(), Group.ITEM)) {
      nearestNeighborsDS = nearestNeighborsDS.withColumnRenamed(COL.LHS, params.getItemCol());
    }

    debugUserBased(neighborsRatingDS, nearestNeighborsDS);

    // k most similar users or items (nearest neighborss)
    return nearestNeighborsDS.groupBy(params.getItemCol()).agg(outputColumn);
  }

  private Dataset<Row> getNeighborsRating(
      Dataset<Row> ratingDS, Object[] userId, Dataset<Row> userRatedItemDS) {

    Dataset<Row> neighborsRatingDS = null;
    if (Objects.equals(params.getGroup(), Group.USER)) {

      neighborsRatingDS =
          SparkUtils.leftAntiJoin(
                  ratingDS, userRatedItemDS.hint(SPARK.HINT_BROADCAST), params.getItemCol())
              .withColumnRenamed(params.getGroupCol(), COL.RHS);

    } else if (Objects.equals(params.getGroup(), Group.ITEM)) {
      neighborsRatingDS =
          userRatedItemDS
              .hint(SPARK.HINT_BROADCAST)
              .join(ratingDS.where(col(params.getUserCol()).isin(userId)), params.getItemCol())
              .withColumnRenamed(params.getGroupCol(), COL.RHS);
    }

    return Objects.requireNonNull(neighborsRatingDS);
  }

  /*  private Dataset<Row> getNeighborsRating(Dataset<Row> ratingDS, Object[] userId) {

    // TODO 중복 Broadcast를 하던지
    Dataset<Row> userRatedItemDS =
        ratingDS.where(col(params.getUserCol()).isin(userId)).select(params.getItemCol());

    Dataset<Row> neighborsRatingDS = null;
    if (Objects.equals(params.getCriterion(), Criterion.USER)) {

      neighborsRatingDS =
          SparkUtils.leftAntiJoin(
                  ratingDS, userRatedItemDS.hint(SPARK.HINT_BROADCAST), params.getItemCol())
              .withColumnRenamed(params.getCriterionCol(), COL.RHS);

    } else if (Objects.equals(params.getCriterion(), Criterion.ITEM)) {
      neighborsRatingDS =
          userRatedItemDS
              .hint(SPARK.HINT_BROADCAST)
              .join(ratingDS.where(col(params.getUserCol()).isin(userId)), params.getItemCol())
              .withColumnRenamed(params.getCriterionCol(), COL.RHS);
    }

    return Objects.requireNonNull(neighborsRatingDS);
  }*/

  // TODO userRatedItems는 User에서는 쓰이지 않음..
  private Dataset<Row> getNeighborsSimilarity(
      Dataset<Row> ratingDS, Object[] userId, Dataset<Row> userRatedItemDS) {

    if (!Objects.equals(params.getWeightedAverage(), WeightedAverage.SIMPLE)) {
      ratingDS = Objects.requireNonNull(params.getNormalizer()).transform(ratingDS);

      //      log.warn("ratingDS.count: {}",
      // ratingDS.where(col(params.getRatingCol()).isNaN()).count());

      if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
        log.info("ratingDS: {}", ratingDS.count());
        VerboseUtils.showPivot(
            ratingDS, params.getUserCol(), params.getItemCol(), params.getRatingCol(), 7);
      }
    }

    /*    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {

      log.info("ratingDS.count: {}", String.valueOf(ratingDS.count()));
      log.info("ratingDS.schema: {}", ratingDS.schema().simpleString());

      VerboseUtils.showPivot(
          ratingDS,
          params.getUserCol(),
          params.getItemCol(),
          Objects.isNull(params.getScaler())
              ? params.getRatingCol()
              : params.getScaler().getOutputCol(),
          7);
    }*/

    Dataset<Row> neighborSimilarityDS = null;
    if (Objects.equals(params.getGroup(), Group.USER)) {
      neighborSimilarityDS =
          Objects.requireNonNull(params.getSimilarityMeasurer().setIds(userId)).transform(ratingDS);
    } else if (Objects.equals(params.getGroup(), Group.ITEM)) {

      List<?> userRatedItemList =
          userRatedItemDS.agg(collect_list(params.getItemCol())).head().getList(0);

      neighborSimilarityDS =
          Objects.requireNonNull(params.getSimilarityMeasurer().setIds(userRatedItemList.toArray()))
              .transform(ratingDS)
              .select(
                  col(COL.RHS).as(COL.LHS),
                  col(COL.LHS).as(COL.RHS),
                  col(params.getSimilarityMeasurer().getOutputCol()));

      /*

      neighborSimilarityDS =
          params
              .getSimilarityMeasurer()
              .compute(ratingDS, userRatedItemDS)
              .select(
                  col(COL.RHS).as(COL.LHS),
                  col(COL.LHS).as(COL.RHS),
                  col(params.getSimilarityMeasurer().getOutputCol()));
                  */
    }

    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {
      // Non Drop NA
      VerboseUtils.showPivot(
          Objects.requireNonNull(neighborSimilarityDS),
          COL.LHS,
          COL.RHS,
          params.getSimilarityMeasurer().getOutputCol(),
          7);
    }

    return Objects.requireNonNull(neighborSimilarityDS)
        .where(col(COL.SIMILARITY).$greater(0d))
        .na()
        .drop() /*.persist(StorageLevel.MEMORY_ONLY())*/; // .cache();
  }

  // TODO 입력에 따라서 통계정보 결과가 가변적이게? avg stddev
  private Dataset<Row> getGroupStats(Dataset<Row> ratingDS) {
    return ratingDS
        .groupBy(col(params.getGroupCol()))
        .agg(avg(params.getRatingCol()).as(COL.MEAN), stddev(params.getRatingCol()).as(COL.STDDEV));
  }

  @Deprecated
  public static List getUserRatedItems(
      Dataset<Row> ratingDS, Object[] userId, String userCol, String itemCol, boolean veerbose) {

    List userRatedItems =
        ratingDS
            .select(itemCol)
            .where(col(userCol).isin(userId))
            .agg(collect_list(itemCol))
            .head()
            .getList(0);

    if (Objects.equals(veerbose, Boolean.TRUE)) {
      log.info("userRatedItems: {}\n{}", userRatedItems.size(), userRatedItems);
    }

    return userRatedItems;
  }

  @Deprecated
  private void debugUserBased(Dataset<Row> neighborsRatingDS, Dataset<Row> nearestNeighborsDS) {
    if (Objects.equals(params.getVerbose(), Boolean.TRUE)) {

      String similarityCol = params.getSimilarityMeasurer().getOutputCol();

      String groupByCol =
          params.getGroupCol().equalsIgnoreCase(params.getUserCol())
              ? params.getItemCol()
              : params.getUserCol();

      VerboseUtils.showPivot(neighborsRatingDS, COL.RHS, groupByCol, params.getRatingCol(), 1);

      String lhsCol =
          (Objects.equals(params.getGroup(), Group.ITEM)) ? params.getItemCol() : COL.LHS;

      if (!Objects.equals(params.getWeightedAverage(), WeightedAverage.SIMPLE)) {
        nearestNeighborsDS
            .select(
                col(lhsCol),
                col(COL.LHS_MEAN),
                col(COL.LHS_STDDEV),
                col(groupByCol),
                col(COL.RHS),
                col(similarityCol),
                col(params.getRatingCol()),
                col(COL.RHS_MEAN),
                col(COL.RHS_STDDEV))
            .orderBy(
                col(lhsCol), length(col(groupByCol)), col(groupByCol), col(similarityCol).desc())
            .show();
      } else {

        nearestNeighborsDS
            .select(
                col(lhsCol),
                col(groupByCol),
                col(COL.RHS),
                col(similarityCol),
                col(params.getRatingCol()))
            .orderBy(
                length(col(lhsCol)),
                col(lhsCol),
                length(col(groupByCol)),
                col(groupByCol),
                col(similarityCol).desc())
            .show();
      }
    }
  }
}
