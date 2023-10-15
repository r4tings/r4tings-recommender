/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.test;

import com.r4tings.recommender.common.ml.param.Group;
import com.r4tings.recommender.model.measures.similarity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class RatingSimilarityMeasurerArgumentConverter extends SimpleArgumentConverter {
  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {

    if (Objects.isNull(source) || targetType.isInstance(source)) {
      return source;
    }

    log.info("source: {}", source);

    String[] params =
        Stream.of(((String) source).trim().split("\\s*,\\s*", 4))
            .map(s -> s.length() == 0 ? null : s)
            .toArray(String[]::new);

    //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);

    log.debug("params: {}", Arrays.toString(params));

    Group group = Group.valueOf(params[0]);
    SimilarityMeasure similarityMeasure = SimilarityMeasure.valueOf(params[1]);
    boolean imputeZero = Boolean.parseBoolean(params[2]);
    boolean verbose = Boolean.parseBoolean(params[3]);

    RatingSimilarityMeasurer measurer = null;
    switch (similarityMeasure) {
      case COSINE:
        measurer = new CosineSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case PEARSON:
        measurer = new PearsonSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case MANHATTAN:
        measurer = new ManhattanSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case EUCLIDEAN:
        measurer = new EuclideanSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case BINARY_SMC:
        measurer = new SimpleMatchingSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case BINARY_JACCARD:
        measurer = new JaccardSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
      case BINARY_EXTENDED_JACCARD:
        measurer = new ExtendedJaccardSimilarityMeasurer().setVerbose(verbose).setGroup(group);
        break;
    }

    return measurer.setImputeZero(imputeZero);
  }
}
