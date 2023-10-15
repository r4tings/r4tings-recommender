/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.platform.commons.util.Preconditions;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class StringArrayConverter extends SimpleArgumentConverter {

  /*
  https://github.com/kingdelee/jdk11_Lee_Project/blob/master/tdd/src/test/java/org/codefx/demo/junit5/parameterized/CustomArgumentConverterTest.java
   */
  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {

    log.info("source: {}", source);

    if (Objects.isNull(source) || targetType.isInstance(source)) {
      return source;
    } else if (source instanceof String && String[].class.isAssignableFrom(targetType)) {
      Preconditions.condition(true, "Convert only from String");
      // Preconditions.condition( String[].class.isAssignableFrom(targetType), "Convert to String
      // []");
      String[] params =
          Stream.of(((String) source).trim().split("\\s*,\\s*"))
              .map(s -> s.length() == 0 ? null : s)
              .toArray(String[]::new);
      log.debug("params: {}", Arrays.toString(params));

      return params;
    }
    throw new ArgumentConversionException(
        String.format(
            "No implicit conversion to convert object of type %s to type %s.",
            source.getClass().getName(), targetType.getName()));
  }
}
