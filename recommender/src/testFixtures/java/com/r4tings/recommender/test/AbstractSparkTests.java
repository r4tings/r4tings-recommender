/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.test;

// import com.r4tings.recommender.common.util.SysPathUtils;
import com.r4tings.recommender.common.util.SysPathUtils;
import com.r4tings.recommender.model.measures.similarity.SimilarityMeasure;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractSparkTests {

  /*
    protected enum ScalerType {
      RAW,
      MEAN_CENTERING,
      Z_SCORE,
      MIN_MAX,
      DECIMAL_SCALING,
      BINARY_THRESHOLDING
    }

    protected enum SimilarityMeasureType {
      COSINE,
      PEARSON,
      MANHATTAN,
      EUCLIDEAN,
      SMC,
      JACCARD,
      EXTENDED_JACCARD
    }
  */

  protected TestInfo testInfo;
  protected TestReporter testReporter;

  protected static transient SparkSession spark;
  protected transient JavaSparkContext jsc;

  @BeforeEach
  protected void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {

    if (SystemUtils.IS_OS_WINDOWS
        && StringUtils.isEmpty(System.getenv("HADOOP_HOME"))
        && StringUtils.isNotEmpty(System.getenv("nativePath"))) {

      if (StringUtils.isEmpty(System.getenv("HADOOP_HOME"))) {
        log.warn(
            "Problems running Hadoop on Windows\nUnable to find native drivers in HADOOP_HOME. Please, refer to <a href=\\\"https://cwiki.apache.org/confluence/display/HADOOP2/WindowsProblems\\\">Hadoop Wiki</a> for more details.\nApache Spark uses Hadoop’s libraries for distributed data processing tasks. If Spark cannot find these libraries, it will fail to initialize the Spark Context.");
      }

      SysPathUtils.addLibraryPath(System.getenv("nativePath") + "\\bin");
      // System.setProperty("PATH", System.getenv("PATH") + ";" +  System.getenv("HADOOP_HOME") +
      // "
      // \\bin");

      log.info(
          "OS:{} HADOOP_HOME: {} nativePath: {}",
          SystemUtils.OS_NAME,
          System.getenv("HADOOP_HOME"),
          System.getenv("nativePath"));
    }

    Path tempPath = Paths.get(System.getenv("rootPath") + "\\tmp\\local");

    if (Files.exists(tempPath)) {

      try {
        Files.walk(tempPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            // .peek(System.out::println)
            .forEach(File::delete);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    this.testInfo = testInfo;
    this.testReporter = testReporter;

    SparkConf sparkConf =
        new SparkConf()
            .setMaster("local[6]")
            .set("spark.driver.memory", "12g")
            .set("spark.ui.enabled", "true")
            .set("spark.local.dir", tempPath.toString())
            .set("spark.worker.cleanup.enabled", "true")
            .set("spark.storage.cleanupFilesAfterExecutorExit", "true");

    sparkConf.set("spark.sql.autoBroadcastJoinThreshold", String.valueOf(50 * 1024 * 1024));

    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
    sparkConf.set("spark.sql.shuffle.partitions", String.valueOf(6));

    // SysPathUtils.addLibraryPath(System.getProperty("user.dir") + "\\hadoop-2.8.3\\bin");

    spark =
        SparkSession.builder().config(sparkConf).appName(getClass().getSimpleName()).getOrCreate();

    this.jsc = new JavaSparkContext(spark.sparkContext());

    // jsc.setLogLevel("INFO");

    /*
     https://github.com/Transkribus/TranskribusInterfaces/blob/master/src/main/java/eu/transkribus/interfaces/util/SysPathUtils.java
    */

    log.info(
        "spark.version: {} core: {}", spark.version(), Runtime.getRuntime().availableProcessors());
  }

  @AfterEach
  protected void tearDown() {

    try {
      spark.stop();
      spark = null;
    } finally {
      SparkSession.clearDefaultSession();
      SparkSession.clearActiveSession();
    }
  }

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(DatasetArgumentConverter.class)
  protected @interface ConvertDataset {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(DatasetArrayArgumentConverter.class)
  protected @interface ConvertDatasetArray {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(StringArrayConverter.class)
  protected @interface ConvertStringArray {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(RatingNormalizerArgumentConverter.class)
  protected @interface ConvertRatingNormalizer {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(RatingSimilarityMeasurerArgumentConverter.class)
  protected @interface ConvertRatingSimilarityMeasurer {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(BaselienPredictorArgumentConverter.class)
  protected @interface ConvertBaselienPredictor {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(RatingSimilarityMeasureArgumentConverter.class)
  protected @interface ConvertRatingSimilarityMeasure {}

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(KNearestNeighborRecommenderArgumentConverter.class)
  protected @interface ConvertKNearestNeighborRecommender {}

  protected static class KNearestNeighborRecommenderArgumentConverter
      extends SimpleArgumentConverter {

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
    protected Object convert(Object source, Class<?> targetType)
        throws ArgumentConversionException {

      if (Objects.isNull(source) || targetType.isInstance(source)) {
        return source;
      }

      log.info("source: {}", source);

      String[] params = ((String) source).trim().split("\\s*,\\s*", 3);

      log.debug("params: {}", Arrays.toString(params));

      //   KNearestNeighborParams params = new KNearestNeighborParams().setCriterion(criterion);

      return null;
    }
  }

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(DatasetPathStringConverter.class)
  protected @interface ConvertPathString {}

  static class DatasetPathStringConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) {
      log.debug("source:{}", source);
      // assertEquals(Dataset.class, targetType, "Can only convert to Dataset");
      // TODO 공통화
      List<String> paramList =
          Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
              .map(s -> s.length() == 0 ? null : s)
              .collect(Collectors.toList());

      return Stream.concat(Stream.of(System.getenv("rootPath")), paramList.stream())
          .map(Object::toString)
          .collect(Collectors.joining("/"));
      // return String.join("/", System.getenv("rootPath"), params[0], params[1]);
      // return spark.read().load(String.valueOf(source)).persist(StorageLevels.MEMORY_ONLY);
    }
  }

  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @ConvertWith(SimilarityMeasureArgumentConverter.class)
  protected @interface ConvertSimilarityMeasure {}

  static class SimilarityMeasureArgumentConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType)
        throws ArgumentConversionException {

      if (Objects.isNull(source) || targetType.isInstance(source)) {
        return source;
      }

      log.debug("source: {}", source);

      String[] params =
          Stream.of(((String) source).trim().split("\\s*,\\s*", 4))
              .map(s -> s.length() == 0 ? null : s)
              .toArray(String[]::new);

      //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);

      log.debug("params: {}", Arrays.toString(params));

      String measure = params[0];
      boolean imputeZero = Boolean.parseBoolean(params[1]);
      boolean verbose = Boolean.parseBoolean(params[2]);

      return SimilarityMeasure.get(measure).invoke(imputeZero, verbose);
    }
  }
}
