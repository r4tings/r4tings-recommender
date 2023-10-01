package com.r4tings.recommender.test;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.data.normalize.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class RatingNormalizerArgumentConverter extends SimpleArgumentConverter {
  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {

    if (Objects.isNull(source) || targetType.isInstance(source)) {
      return source;
    }

    // TODO 공통화
    String[] params =
        Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
            .map(s -> s.length() == 0 ? null : s)
            .toArray(String[]::new);

    log.info("{} {}", this.getClass().getSimpleName(), Arrays.toString(params));

    NormalizeMethod normalizeMethod = NormalizeMethod.valueOf(params[1]);

    RatingNormalizer normalizer = null;
    switch (normalizeMethod) {
      case MEAN_CENTERING:
        normalizer = new MeanCenteringNormalizer();
        break;
      case Z_SCORE:
        normalizer = new ZScoreNormalizer();
        break;
      case MIN_MAX:
        normalizer =
            new MinMaxNormalizer()
                .setLower(Double.parseDouble(params[3]))
                .setUpper(Double.parseDouble(params[4]));
        break;
      case DECIMAL_SCALING:
        normalizer = new DecimalScalingNormalizer();
        break;
      case BINARY_THRESHOLDING:
        normalizer = new ThresholdBinarizer().setThreshold(Double.parseDouble(params[5]));
        break;
    }

    if (Objects.nonNull(params[2])) {
      Objects.requireNonNull(normalizer).setVerbose(Boolean.valueOf(params[2]));
    }

    if (Objects.nonNull(params[0])) {
      normalizer.setGroup(Group.valueOf(params[0]));
    }

    return normalizer;
  }
}
