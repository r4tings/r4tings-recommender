/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.data.normalize;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.spark.sql.functions.udf;

@Slf4j
@RequiredArgsConstructor
public enum NormalizeMethod {
  MEAN_CENTERING("mean-centering") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf((Double rating, Double mean) -> rating - mean, DataTypes.DoubleType);
    }
  },
  MEAN_CENTERING_INVERSE("mean-centering-inverse") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      // return udf((Double rating, Double mean) -> rating + mean, DataTypes.DoubleType);
      return udf((UDF2<Double, Double, Double>) Double::sum, DataTypes.DoubleType);
    }
  },
  Z_SCORE("z-score") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Double mean, Double stddev) ->
              Objects.isNull(stddev) || Double.isNaN(stddev) || Objects.equals(stddev, 0d)
                  ? Double.NaN
                  : (rating - mean) / stddev,
          DataTypes.DoubleType);
    }
  },
  Z_SCORE_INVERSE("z-score-inverse") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Double mean, Double stddev) -> (rating * stddev) + mean,
          DataTypes.DoubleType);
    }
  },
  MIN_MAX("min-max") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Double min, Double max, Double newMin, Double newMax) ->
              Objects.equals(min, max)
                  ? Double.NaN
                  : (rating - min) / (max - min) * (newMax - newMin) + newMin,
          DataTypes.DoubleType);
    }
  },
  MIN_MAX_INVERSE("min-max-inverse") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Double min, Double max, Double newMin, Double newMax) ->
              rating.isNaN()
                  ? Double.NaN
                  // TODO min과 max값이 같은 경우인데, 원본값이냐 아니면 NaN이냐?
                  : ((rating - newMin) * (max - min) / (newMax - newMin)) + min,
          DataTypes.DoubleType);
    }
  },
  DECIMAL_SCALING("decimal-scaling") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Integer exponent) -> rating / Math.pow(10d, exponent),
          DataTypes.DoubleType);
    }
  },
  DECIMAL_SCALING_INVERSE("decimal-scaling-inverse") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Integer exponent) -> rating * Math.pow(10d, exponent),
          DataTypes.DoubleType);
    }
  },
  BINARY_THRESHOLDING("binary-thresholding") {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          (Double rating, Double threshold) -> rating >= threshold ? 1d : 0d, DataTypes.DoubleType);
    }
  };

  private static final Map<String, NormalizeMethod> ENUM_MAP =
      Collections.unmodifiableMap(
          Stream.of(values())
              .collect(Collectors.toMap(NormalizeMethod::getDescription, Function.identity())));
  @Getter private final String description;

  public static NormalizeMethod get(String description) {
    return Optional.ofNullable(ENUM_MAP.get(description.replace("_", "-").toLowerCase()))
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Unknown: '%s'", description)));
  }

  public static List<String> descriptions() {
    return new ArrayList<>(ENUM_MAP.keySet());
  }

  public abstract UserDefinedFunction invoke(Boolean verbose);

  @Override
  public String toString() {
    return this.description;
  }
}
