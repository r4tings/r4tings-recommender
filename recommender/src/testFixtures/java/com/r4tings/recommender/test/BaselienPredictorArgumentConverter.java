/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.test;

import com.r4tings.recommender.common.ml.CommonEstimator;
import com.r4tings.recommender.model.svd.baseline.GeneralMeanRatingBaseline;
import com.r4tings.recommender.model.svd.baseline.SimpleMeanRatingBaseline;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class BaselienPredictorArgumentConverter extends SimpleArgumentConverter {

  protected enum BaselineType {
    SIMPLE,
    GENERAL
  }

  /**
   * Convert the supplied {@code source} object into to the supplied {@code targetType}.
   *
   * @param source the source object to convert; may be {@code null}
   * @param targetType the target type the source object should be converted into; never {@code
   *     null}
   * @return the converted object; may be {@code null} but only if the target type is a reference
   *     type
   * @throws ArgumentConversionException in case an error occurs during the conversion
   */
  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
    if (Objects.isNull(source) || targetType.isInstance(source)) {
      return source;
    }

    log.info("source: {}", source);

    String[] params =
        Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
            .map(s -> s.length() == 0 ? null : s)
            .toArray(String[]::new);

    //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);

    log.debug("params: {}", Arrays.toString(params));

    BaselineType baselineType = BaselineType.valueOf(params[0]);

    CommonEstimator baselinePredictor = null;

    if (Objects.equals(baselineType, BaselineType.SIMPLE)) {
      baselinePredictor = new SimpleMeanRatingBaseline();
    } else if (Objects.equals(baselineType, BaselineType.GENERAL)) {
      baselinePredictor =
          new GeneralMeanRatingBaseline()
              .setLambda2(Objects.nonNull(params[2]) ? Integer.valueOf(params[2]) : null)
              .setLambda3(Objects.nonNull(params[3]) ? Integer.valueOf(params[3]) : null);
    }

    return Objects.requireNonNull(baselinePredictor).setVerbose(Boolean.valueOf(params[1]));
  }
}
