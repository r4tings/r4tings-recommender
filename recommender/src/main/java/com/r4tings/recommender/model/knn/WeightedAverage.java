package com.r4tings.recommender.model.knn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.spark.sql.functions.udf;

// TODO enum은 일괄 수정 필요
@Slf4j
@RequiredArgsConstructor
public enum WeightedAverage {
  SIMPLE("simple") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose, Integer minimumNeighbors) {
      // TODO 동일 유사도 값인 경우, 정렬 기준이 모호함 struct에 id값이라도 넣어야하나 어떻게 정렬되는지?

      return udf(
          (Seq<Row> rowSeq) ->
              calculate(
                  verbose,
                  minimumNeighbors,
                  rowSeq,
                  row -> row.getDouble(0) * row.getDouble(1),
                  row -> Math.abs(row.getDouble(0))),
          DataTypes.DoubleType);
    }
  },
  MEAN_CENTERING("mean-centering") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose, Integer minimumNeighbors) {

      // neighborSimilarity * (neighborRating - meanNeighborRating);
      // neighbor's similarity * (neighbor's rating - mean neighbor's rating)

      return udf(
          (Seq<Row> rowSeq) ->
              calculate(
                  verbose,
                  minimumNeighbors,
                  rowSeq,
                  row -> row.getDouble(0) * (row.getDouble(1) - row.getDouble(2)),
                  row -> Math.abs(row.getDouble(0))),
          DataTypes.DoubleType);
    }
  },
  Z_SCORE("z-score") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose, Integer minimumNeighbors) {

      // similarity * (rating - meanRating) / stddev

      return udf(
          (Seq<Row> rowSeq) ->
              calculate(
                  verbose,
                  minimumNeighbors,
                  rowSeq,
                  row ->
                      row.getDouble(0)
                          * ((row.getDouble(1) - row.getDouble(2)) / row.getDouble(3)), // z-score,
                  row -> Math.abs(row.getDouble(0))),
          DataTypes.DoubleType);
    }
  };

  @Getter private final String description;

  private static final Map<String, WeightedAverage> ENUM_MAP =
      Collections.unmodifiableMap(
          Stream.of(values())
              .collect(Collectors.toMap(WeightedAverage::getDescription, Function.identity())));

  public static WeightedAverage get(String description) {
    return Optional.ofNullable(ENUM_MAP.get(description.toLowerCase()))
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Unknown: '%s'", description)));
  }

  public static List<String> descriptions() {
    return new ArrayList<>(ENUM_MAP.keySet());
  }

  // TODO  abstract UserDefinedFunction invoke(Boolean verbose);
  public abstract UserDefinedFunction invoke(Boolean verbose, Integer minimumNeighbors);

  // Weighted Average =   Σwx/Σw w is the weight, x is the value we want the average of

  // simple weighted average
  // computing the weighted average of the active user’srating on the K most similar items. Using
  // weightedsum
  // If most of the item-item similarities are negative, then itwould result in negative prediction,
  // which is not correct. Thisformula can be corrected, by using the adjusted weightedsum that
  // considers the deviation of ratings from the averagerating of the active use
  // aggregation approaches - adjusted weighted aggregation  (Deviation-From-Mean)
  //  weighted sum of the mean centered rating

  Double calculate(
      Boolean verbose,
      Integer minimumNeighbors,
      Seq<Row> rowSeq,
      ToDoubleFunction<Row> weightNumerator,
      ToDoubleFunction<Row> weightDenominator) {

    // k - the number of nearest neighbors to use for prediction
    // K is the number of nearest neighbors used in to find the average predicted ratings of user x
    // on item y
    if (rowSeq.isEmpty() || rowSeq.size() < minimumNeighbors) {

      log.debug("rowSeq: {} ", rowSeq);

      return Double.NaN;

      /*      if (Objects.equals(verbose, Boolean.TRUE)) {
        log.warn("k closet similar items is empty or less than {} - {}", minimumNeighbors, rowSeq);
      }

      return Double.NaN;
      */
    }

    List<Row> kNearestNeighborList = JavaConverters.seqAsJavaList(rowSeq);

    /*
    TODO 확인 필요 - 절대값을 계산하는 이유는 because negative weights can produce ratings outside the allowed range
    */

    if (Objects.equals(verbose, Boolean.TRUE)) {
      log.debug("kNearestNeighborList: {}", kNearestNeighborList);
    }

    double numerator = kNearestNeighborList.parallelStream().mapToDouble(weightNumerator).sum();

    // sum of the similarity weights of neighbors
    double denominator = kNearestNeighborList.parallelStream().mapToDouble(weightDenominator).sum();

    if (Objects.equals(verbose, Boolean.TRUE)) {
      log.debug("numerator / denominator: {} / {}", numerator, denominator);
    }

    /*
    TODO Double.compare(d1, d2) >= 0d; 이건 어떻게 해야하나 함수로 추가해야하나?
     */

    return numerator / denominator;
  }

  @Override
  public String toString() {
    return this.description;
  }
}
