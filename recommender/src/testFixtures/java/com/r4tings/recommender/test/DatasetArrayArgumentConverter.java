package com.r4tings.recommender.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.sql.Dataset;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class DatasetArrayArgumentConverter extends SimpleArgumentConverter {
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

    String[] params =
        Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
            .map(param -> param.length() == 0 ? null : param)
            .toArray(String[]::new);

    log.info("source: {}  params: {}", source, Arrays.toString(params));

    return Arrays.stream(Arrays.copyOfRange(params, 1, params.length))
        .map(
            e -> {
              Map<String, String> options =
                  new HashMap<>() {
                    {
                      put("header", "true");
                      put("inferSchema", "true");
                      put("ignoreLeadingWhiteSpace", "true");
                      put("ignoreTrailingWhiteSpace", "true");
                    }
                  };

              if (String.valueOf(source).endsWith("csv")) {
                return AbstractSparkTests.spark
                    .read()
                    .options(options)
                    .csv(String.join("/", System.getenv("rootPath"), params[0], e))
                    .persist(StorageLevels.MEMORY_ONLY);
              } else {
                return AbstractSparkTests.spark
                    .read()
                    .options(options)
                    .load(String.join("/", System.getenv("rootPath"), params[0], e))
                    .persist(StorageLevels.MEMORY_ONLY);
              }
            })
        .toArray(Dataset[]::new);
  }
}
