/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.StorageLevels;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DatasetArgumentConverter extends SimpleArgumentConverter {
  @Override
  protected Object convert(Object source, Class<?> targetType) {
    log.info("source:{}", source);
    // assertEquals(Dataset.class, targetType, "Can only convert to Dataset");

    List<String> paramList =
        Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
            .map(s -> s.length() == 0 ? null : s)
            .collect(Collectors.toList());

    String path =
        Stream.concat(Stream.of(Paths.get("").toAbsolutePath().getParent()), paramList.stream())
            .map(Object::toString)
            .collect(Collectors.joining("/"));

    log.warn("path: {}", path);

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
          .csv(path)
          .persist(StorageLevels.MEMORY_ONLY);
    } else {
      return AbstractSparkTests.spark
          .read()
          .options(options)
          .load(path)
          .persist(StorageLevels.MEMORY_ONLY);
    }
  }
}
