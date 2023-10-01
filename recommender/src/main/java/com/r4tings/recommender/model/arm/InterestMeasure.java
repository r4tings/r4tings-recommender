package com.r4tings.recommender.model.arm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.r4tings.recommender.common.Constants.COL;
import static org.apache.spark.sql.functions.udf;

@Slf4j
@RequiredArgsConstructor
public enum InterestMeasure {
  SUPPORT(COL.SUPPORT) {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      // sup(X -> Y) = sup(Y -> X) = P(X and Y)
      return udf(
          (Integer supportCount, Long totalTransaction) -> {
            double probability = supportCount.doubleValue() / totalTransaction.doubleValue();
            return (Double.isNaN(probability) || Double.isInfinite(probability))
                ? Double.NaN
                : probability;
          },
          DataTypes.DoubleType);
    }
  },
  CONFIDENCE(COL.CONFIDENCE) {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      // conf(X -> Y) = P(Y | X) = P(X and Y)/P(X) = sup(X -> Y)/sup(X)
      return udf((Double supportXY, Double supportX) -> supportXY / supportX, DataTypes.DoubleType);
    }
  },
  LIFT(COL.LIFT) {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      // lift(X -> Y)  = P(X and Y)/(P(X)P(Y)) = conf(X -> Y)/sup(Y) = conf(Y -> X)/sup(X)
      return udf(
          (Double confidence, Double supportY) -> confidence / supportY, DataTypes.DoubleType);
    }
  },
  LEVERAGE(COL.LEVERAGE) {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      // leverage(X -> Y) = P(X and Y) - (P(X)P(Y))
      return udf(
          (Double supportXY, Double supportX, Double supportY) -> supportXY - (supportX * supportY),
          DataTypes.DoubleType);
    }
  },
  CONVICTION(COL.CONVICTION) {
    @Override
    public UserDefinedFunction invoke(Boolean verbose) {
      return udf(
          // conviction(X -> Y) = P(X)P(not Y)/P(X and not Y)=(1-sup(Y))/(1-conf(X -> Y))
          (Double supportX, Double confidence) -> (1 - supportX) / (1 - confidence),
          DataTypes.DoubleType);
    }
  };

  @Getter private final String description;

  private static final Map<String, InterestMeasure> ENUM_MAP =
      Collections.unmodifiableMap(
          Stream.of(values())
              .collect(Collectors.toMap(InterestMeasure::getDescription, Function.identity())));

  public static InterestMeasure get(String description) {
    return Optional.ofNullable(ENUM_MAP.get(description.toLowerCase()))
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Unknown: '%s'", description)));
  }

  public static List<String> descriptions() {
    return new ArrayList<>(ENUM_MAP.keySet());
  }

  // TODO 위치가 어디인지 생성자 다음 아닌가?
  public abstract UserDefinedFunction invoke(Boolean verbose);
}
