/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.measures.similarity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.JavaConverters;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.r4tings.recommender.common.util.DebugUtils.showRealMatrix;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import static org.apache.spark.sql.functions.udf;

@Slf4j
@RequiredArgsConstructor
public enum SimilarityMeasure {
  COSINE("cosine") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      /*
      Cosine similarity is a measure of similarity between two non-zero vectors of an inner product space
      that measures the cosine of the angle between them.
      unit-length vectors
      When x and y are unit vectors, then:

      When at least one vector is of unit length the dot product is the length of the projection of
      the non-unit length vector onto the axis of the normalized vector (When they are both unit length,
      they are both moving the same amount in the direction of each other).
       */
      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    try {
                      return realMatrix.getColumnVector(0).cosine(realMatrix.getColumnVector(1));
                    } catch (MathArithmeticException mae) {
                      if (Objects.equals(verbose, Boolean.TRUE)) {
                        // if (Objects.equals(verbose, Boolean.TRUE)) {
                        // 둘 중 하나의 벡터가 zero norm
                        log.warn("{}: {}", mae.getMessage(), showRealMatrix(realMatrix));
                      }

                      return 0d;
                    }
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  PEARSON("pearson") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double correlation =
                        new PearsonsCorrelation()
                            .correlation(realMatrix.getColumn(0), realMatrix.getColumn(1));

                    // if (log.isDebugEnabled()) {
                    if (Objects.equals(verbose, Boolean.TRUE)) {
                      if (Double.compare(correlation, 0d) == 0) {
                        log.warn("zero correlation: {}", correlation);
                      } else if (Double.compare(correlation, 0d) < 0) {
                        log.warn("negative correlation: {}", correlation);
                      } else if (Double.isNaN(correlation)) {
                        double covar =
                            new Covariance()
                                .covariance(realMatrix.getColumn(0), realMatrix.getColumn(1));
                        log.warn(
                            "covariance is NaN: Covariance {} {}",
                            covar,
                            showRealMatrix(realMatrix));
                      }
                    }

                    return (Double.compare(correlation, 0d) <= 0)
                        ? 0.5 // 1d / (1d + (1d - 0d));
                        : 1d / (1d + (1d - correlation));
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  MANHATTAN("manhattan") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double distance =
                        MathArrays.distance1(realMatrix.getColumn(0), realMatrix.getColumn(1));

                    return 1d / (1d + distance);
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  WEIGHTED_MANHATTAN("weighted_manhattan") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap,
              Integer weight) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double distance =
                        MathArrays.distance1(realMatrix.getColumn(0), realMatrix.getColumn(1));

                    double scaleFactor = (double) weight / realMatrix.getRowDimension();
                    distance *= FastMath.sqrt(scaleFactor);

                    return 1d / (1d + distance);
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },

  EUCLIDEAN("euclidean") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double distance =
                        MathArrays.distance(realMatrix.getColumn(0), realMatrix.getColumn(1));

                    return 1d / (1d + distance);
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  WEIGHTED_EUCLIDEAN("weighted_euclidean") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap,
              Integer weight) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double distance =
                        MathArrays.distance(realMatrix.getColumn(0), realMatrix.getColumn(1));

                    double scaleFactor = (double) weight / realMatrix.getRowDimension();
                    distance *= FastMath.sqrt(scaleFactor);

                    return 1d / (1d + distance);
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  BINARY_SMC("binary_smc") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    Map<Double, Double> rowSums =
                        IntStream.range(0, realMatrix.getRowDimension())
                            .parallel()
                            .boxed()
                            .map(i -> realMatrix.getEntry(i, 0) + realMatrix.getEntry(i, 1))
                            .collect(groupingBy(Function.identity(), summingDouble(e -> 1d)));

                    /*
                       ## Matching Coefficient (Sokal and Michener, 1958)

                       #we need d only here
                       #d <- ncol(x) - a_b_c
                       d <- ncol(x) - (a+b+c)

                       dist <- 1 - (a + d) / (a + b + c + d)
                       #dist <- 1 - ((a + d) / (a_b_c + d))
                    */
                    double matches = rowSums.getOrDefault(2d, 0d) + rowSums.getOrDefault(0d, 0d);
                    double misMatches = rowSums.getOrDefault(1d, 0d);

                    double denominator = matches + misMatches;

                    // if (log.isDebugEnabled()) {
                    if (Objects.equals(verbose, Boolean.TRUE)) {
                      log.debug(
                          "{} = {} / {({} + {})}\nrowSums {} {}",
                          matches / (matches + misMatches),
                          matches,
                          matches,
                          misMatches,
                          rowSums,
                          showRealMatrix(realMatrix));
                    }

                    // Division by zero is NAN
                    return Double.compare(denominator, 0d) == 0
                        ? Double.NaN
                        : matches / denominator;
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  BINARY_JACCARD("binary_jaccard") {
    /*
    https://jootse84.github.io/notes/jaccard-index-calculation-in-R

    a = number of rows where both columns are 1
    b = number of rows where this and not the other column is 1
    c = number of rows where the other and not this column is 1

      TODO 값이 없는(NaN)인 경우에는 0으로 치환?
      TODO 유닛벡터인경우는? 길이가 1이면 결과도 의미 없지 않나?
      TODO 입력 데이터는 NaN값이 포함되어 있음, fill zero 하여야함
    */
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    // TODO SMC와 중복 제거 enum 이용
                    final double INTERSECTION = 2d;
                    final double SYMMETRIC_DIFFERENCE = 1d;
                    Map<Double, Double> rowSums =
                        IntStream.range(0, realMatrix.getRowDimension())
                            .parallel()
                            .boxed()
                            .map(i -> realMatrix.getEntry(i, 0) + realMatrix.getEntry(i, 1))
                            .collect(groupingBy(Function.identity(), summingDouble(e -> 1d)));

                    double intersection = rowSums.getOrDefault(INTERSECTION, 0d);
                    double union = intersection + rowSums.getOrDefault(SYMMETRIC_DIFFERENCE, 0d);

                    // if (log.isDebugEnabled()) {
                    if (Objects.equals(verbose, Boolean.TRUE)) {
                      log.debug(
                          "{} = {} / {}\nrowSums {} {}",
                          intersection / union,
                          intersection,
                          union,
                          rowSums,
                          showRealMatrix(realMatrix));
                    }

                    // Division by zero is NAN
                    return Double.compare(union, 0d) == 0 ? Double.NaN : intersection / union;
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  },
  BINARY_EXTENDED_JACCARD("binary_extended_jaccard") {
    @Override
    public UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose) {

      return udf(
          (scala.collection.Map<String, Double> lhsScalaMap,
              scala.collection.Map<String, Double> rhsScalaMap) ->
              calculate(
                  lhsScalaMap,
                  rhsScalaMap,
                  imputeZero,
                  (RealMatrix realMatrix) -> {
                    double dot =
                        realMatrix.getColumnVector(0).dotProduct(realMatrix.getColumnVector(1));

                    double a = Math.pow(realMatrix.getColumnVector(0).getNorm(), 2d);

                    double b = Math.pow(realMatrix.getColumnVector(1).getNorm(), 2d);

                    return dot / (a + b - dot);
                    // Division by zero is NAN
                    // return isEqual.test(union, 0d) ? Double.NaN :
                    // intersection / union;
                  },
                  verbose),
          DataTypes.DoubleType);
    }
  };

  private static final Map<String, SimilarityMeasure> ENUM_MAP =
      Collections.unmodifiableMap(
          Stream.of(values())
              .collect(Collectors.toMap(SimilarityMeasure::getDescription, Function.identity())));

  @Getter private final String description;

  public static SimilarityMeasure get(String description) {
    return Optional.ofNullable(ENUM_MAP.get(description.toLowerCase()))
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Unknown: '%s'", description)));
  }

  public static List<String> descriptions() {
    return new ArrayList<>(ENUM_MAP.keySet());
  }

  public abstract UserDefinedFunction invoke(Boolean imputeZero, Boolean verbose);

  @Override
  public String toString() {
    return this.description;
  }

  /*
  TODO 이건 struct 구조를 재고민해봐야할듯 udf 속 udf가 아닌 현재 구조대로
   */
  Double calculate(
      scala.collection.Map<?, Double> lhsScalaMap,
      scala.collection.Map<?, Double> rhsScalaMap,
      Boolean fillZero,
      ToDoubleFunction<RealMatrix> compute,
      Boolean verbose) {

    Map<?, Double> lhsMap = JavaConverters.mapAsJavaMap(lhsScalaMap);
    Map<?, Double> rhsMap = JavaConverters.mapAsJavaMap(rhsScalaMap);

    /*
    TODO  데이터 검증용 출력후 대체
        if (CollectionUtils.intersection(lhsMap.keySet(), rhsMap.keySet()).size() < 2) {
          if (Objects.equals(verbose, Boolean.TRUE)) {
            log.debug(
                "intersection of {} with {} has size at least two {}",
                lhsMap.keySet(),
                rhsMap.keySet());
          }
          return Double.NaN;
        }
    */

    // 키를 합치고 키에 해당하는 2차원 배열을 만든다.

    double[][] array2D;
    if (Objects.equals(fillZero, Boolean.TRUE)) {
      array2D =
          Stream.of(lhsMap.keySet(), rhsMap.keySet())
              .flatMap(Set::stream)
              .distinct()
              .map(e -> getDoubles(lhsMap, rhsMap, e, 0d))
              .toArray(double[][]::new);
    } else {

      // TODO imputeZero가 아닌 경우에만으로 적용 필요
      int intersectSize = CollectionUtils.intersection(lhsMap.keySet(), rhsMap.keySet()).size();

      if (intersectSize < 2) {
        // 두 벡터 중 하나가 zero norm이면 NaN
        // TODO zero norm은 처음부터 들어오면 안됨
        // 두 벡터 중 하나의 크기가 2보다 작으면 NaN(교집합이 0이거나 1인경우 )
        //  0 대체는 교집합이 0이면 대응하지 않음 -> NaN을 제거하고 난 크기가 0이면 NaN(교집합X)
        /*
          TODO enum을 통한 메시지 출력으로 참고
          throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION,  xArray.length, 2);
        */
        if (Objects.equals(verbose, Boolean.TRUE)) {
          log.warn(
              "insufficient dimension {}, intersection of {} with {} has size at least two",
              intersectSize,
              lhsMap.keySet(),
              rhsMap.keySet());

          if (intersectSize == 0) {
            // TODO 데이터 검증용 출력으로 일괄 삭제
            // return -111d; // recommenderlab은 NA
            return Double.NaN;
          } else if (intersectSize == 1) {
            // TODO 데이터 검증용 출력으로 일괄 삭제
            // return -222d;
            return 0d;
          }
        }

        return Double.NaN;
      }

      array2D =
          Stream.of(lhsMap.keySet(), rhsMap.keySet())
              .flatMap(Set::stream)
              .distinct()
              .map(e -> getDoubles(lhsMap, rhsMap, e, Double.NaN))
              .filter(x -> Arrays.stream(x).noneMatch(Double::isNaN))
              .toArray(double[][]::new);
    }

    try {
      RealMatrix matrix = new Array2DRowRealMatrix(array2D);

      return compute.applyAsDouble(matrix);
    } catch (NoDataException nde) {

      //      if (Objects.equals(verbose, Boolean.TRUE)) {
      //        // if (Objects.equals(verbose, Boolean.TRUE)) {
      //        //  matrix must have at least one row - row or column dimension is zero
      //        log.warn("{} - {} {} {}", nde.getMessage(), intersectSize, lhsMap, rhsMap);
      //      }

      return 0d;
    }
  }

  private double[] getDoubles(
      Map<?, Double> lhsMap, Map<?, Double> rhsMap, Object key, double defaultValue) {

    Object lhs = lhsMap.getOrDefault(key, defaultValue);
    Object rhs = rhsMap.getOrDefault(key, defaultValue);

    // TODO 평점 속성을 그냥 더블로 하는 것이 맞을듯... 실행시 validation 추가

    return new double[] {(double) lhs, (double) rhs};
    /*

          if (lhs instanceof Double && rhs instanceof Double) {
          return new double[] {(double) lhs, (double) rhs};
        } else {
          return new double[] {
            Double.valueOf(String.valueOf(lhs)), Double.valueOf(String.valueOf(rhs))
          };
        }
    */
  }
}
