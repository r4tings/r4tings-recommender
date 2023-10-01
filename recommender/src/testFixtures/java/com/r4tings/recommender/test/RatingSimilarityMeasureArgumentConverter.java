package com.r4tings.recommender.test;

import com.r4tings.recommender.model.measures.similarity.SimilarityMeasure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class RatingSimilarityMeasureArgumentConverter extends SimpleArgumentConverter {

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
        Stream.of(((String) source).trim().split("\\s*,\\s*", 5))
            .map(s -> s.length() == 0 ? null : s)
            .toArray(String[]::new);

    //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);

    log.debug("params: {}", Arrays.toString(params));

    SimilarityMeasure similarityMeasure = SimilarityMeasure.valueOf(params[0].toUpperCase());
    Boolean imputeZero = Boolean.parseBoolean(params[1]);
    Boolean verbose = Boolean.parseBoolean(params[2]);

    return similarityMeasure.invoke(imputeZero, verbose);
  }
}
