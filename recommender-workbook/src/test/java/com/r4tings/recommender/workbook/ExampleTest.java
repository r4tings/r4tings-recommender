// /*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples;
//
// import com.r4tings.recommender.common.ml.param.Group;
// import com.r4tings.recommender.common.util.SysPathUtils;
// import com.r4tings.recommender.data.normalize.*;
// import com.r4tings.recommender.model.measures.similarity.*;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.apache.commons.lang3.SystemUtils;
// import org.apache.spark.api.java.JavaSparkContext;
// import org.apache.spark.sql.Dataset;
// import org.apache.spark.sql.Row;
// import org.apache.spark.sql.SparkSession;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.TestInfo;
// import org.junit.jupiter.api.TestReporter;
// import org.junit.jupiter.params.converter.ArgumentConversionException;
// import org.junit.jupiter.params.converter.ConvertWith;
// import org.junit.jupiter.params.converter.SimpleArgumentConverter;
// import org.junit.platform.commons.util.Preconditions;
//
// import java.io.File;
// import java.io.IOException;
// import java.lang.annotation.ElementType;
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;
// import java.lang.management.ManagementFactory;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.Arrays;
// import java.util.Comparator;
// import java.util.Objects;
// import java.util.stream.Stream;
//
// import static org.apache.spark.sql.functions.*;
//
// @Slf4j
// public abstract class ExampleTest {
//
//  protected TestInfo testInfo;
//  protected TestReporter testReporter;
//
//  protected static transient SparkSession spark;
//  protected transient JavaSparkContext jsc;
//
//  //
// https://github.com/TileDB-Inc/TileDB-VCF/blob/master/apis/spark/src/test/java/io/tiledb/vcf/SharedJavaSparkContext.java
//
//  private static boolean checkHadoopWindowsAvailable() throws Exception {
//
//    String nativePath = System.getenv("HADOOP_HOME");
//
//    log.warn("HADOOP_HOME: {}", nativePath);
//
//    if (StringUtils.isBlank(nativePath)) {
//
//      throw new IllegalStateException(
//          "Unable to find native drivers in HADOOP_HOME. Please, refer to <a
// href=\\\"https://cwiki.apache.org/confluence/display/HADOOP2/WindowsProblems\\\">Hadoop Wiki</a>
// for more details.: Apache Spark uses Hadoop’s libraries for distributed data processing tasks. If
// Spark cannot find these libraries, it will fail to initialize the Spark Context.");
//
//    } else {
//      if (SystemUtils.IS_OS_WINDOWS) {
//
//        log.warn("IS_OS_WINDOWS: {}", SystemUtils.IS_OS_WINDOWS);
//
//        if (nativePath != null) {
//          Path path = Paths.get(nativePath);
//          if (Files.isDirectory(path)
//              && Files.exists(path.resolve("bin").resolve("winutils.exe"))
//              && Files.exists(path.resolve("bin").resolve("hadoop.dll"))) {
//
//            //  System.setProperty("hadoop.home.dir",  ".\\hadoop-2.8.3");
//
//            /*
//                File file = new File(".");
//                System.getProperties().put("hadoop.home.dir",
//                        file.getAbsolutePath()+ "\\hadoop-2.8.3");
//
//
//                System.getProperties().put("java.library.path",
// "C:\\Users\\user\\Documents\\r4tings-recommender-workbook\\hadoop-2.8.3\\bin");
//                log.debug("HADOOP_HOME: {}", System.getProperty("hadoop.home.dir"));
//            */
//
//            log.warn("{}",nativePath+ "\\bin");
//
//
//            log.warn("{}", System.getenv("PATH"));
//
//             SysPathUtils.addLibraryPath(System.getProperty("user.dir") + "\\hadoop-2.8.3\\bin");
//            // System.loadLibrary("hadoop");
//            // log.debug("java.library.path: {}", SysPathUtils.getJavaLibraryPath());
//
//            return true;
//          } else {
//            throw new IllegalStateException(
//                "HADOOP_HOME: "
//                    + path
//                    + " is invalid, winutils.exe should be in in \\$HADOOP_HOME\\bin directory for
// Windows platform");
//          }
//        }
//      }
//    }
//
//    return true;
//  }
//
//  /**
//   * Windows 환경에서 Spark Dataset(DataFrame) 정보를 파일로 write 하려면
//   *
//   * <p>- Case1: Hadoop 전체를 Windows에 설치 하거나, - Case2: Hadoop 의 HDFS 라이브러리만 따로 Windows에 설치 하면 된다.
//   *
//   * <p>참고)
//   *
// https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-tips-and-tricks-running-spark-windows.html
//   * https://github.com/steveloughran/winutils
//   */
//
//  //      if (System.getProperty("os.name").toLowerCase().contains("win")) {
//  //    System.out.println("Detected windows");
//  //    System.setProperty("hadoop.home.dir", winutilPath);
//  //    System.setProperty("HADOOP_HOME", winutilPath);
//  //  }
//
//  /*
//  https://cwiki.apache.org/confluence/display/HADOOP2/WindowsProblems
//   */
//
//  @BeforeEach
//  protected void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {
//
//    checkHadoopWindowsAvailable();
//
//    log.debug(testInfo.getDisplayName());
//
//    Path tempPath = Paths.get("tmp/local");
//
//    if (Files.exists(tempPath)) {
//      try {
//        Files.walk(tempPath)
//            .sorted(Comparator.reverseOrder())
//            .map(Path::toFile)
//            // .peek(System.out::println)
//            .forEach(File::delete);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//
//    this.testInfo = testInfo;
//    this.testReporter = testReporter;
//
//    int cores = java.lang.Runtime.getRuntime().availableProcessors() - 1;
//
//    log.debug("JAVA_HOME {}", System.getProperty("java.home"));
//    log.debug("Heap Memory Usage: {}", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
//
//    spark =
//        SparkSession.builder()
//            .master("local[" + cores + "]")
//            .config("spark.ui.enabled", "true")
//            .config("spark.local.dir", "tmp/local")
//            .config("spark.sql.shuffle.partitions", String.valueOf(cores))
//            .appName(getClass().getSimpleName())
//            .getOrCreate();
//
//    this.jsc = new JavaSparkContext(spark.sparkContext());
//
//    log.debug("spark: {}\n{}", spark.version(), spark.conf().getAll().mkString("\n"));
//  }
//
//  @AfterEach
//  protected void tearDown() {
//
//    try {
//      spark.stop();
//      spark = null;
//    } finally {
//      SparkSession.clearDefaultSession();
//      SparkSession.clearActiveSession();
//    }
//  }
//
//  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
//  @Retention(RetentionPolicy.RUNTIME)
//  @ConvertWith(DatasetPathStringConverter.class)
//  protected @interface ConvertPathString {}
//
//  static class DatasetPathStringConverter extends SimpleArgumentConverter {
//    @Override
//    protected Object convert(Object source, Class<?> targetType) {
//      log.debug("source:{}", source);
//      // assertEquals(Dataset.class, targetType, "Can only convert to Dataset");
//      // TODO 공통화
//      String[] params =
//          Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
//              .map(s -> s.length() == 0 ? null : s)
//              .toArray(String[]::new);
//
//      return String.join("/", params[0], params[1]);
//      // return spark.read().load(String.valueOf(source)).persist(StorageLevels.MEMORY_ONLY);
//    }
//  }
//
//  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
//  @Retention(RetentionPolicy.RUNTIME)
//  @ConvertWith(StringArrayConverter.class)
//  protected @interface ConvertStringArray {}
//
//  static class StringArrayConverter extends SimpleArgumentConverter {
//
//    /*
//
// https://github.com/kingdelee/jdk11_Lee_Project/blob/master/tdd/src/test/java/org/codefx/demo/junit5/parameterized/CustomArgumentConverterTest.java
//     */
//    @Override
//    protected Object convert(Object source, Class<?> targetType)
//        throws ArgumentConversionException {
//
//      log.debug("source: {}", source);
//
//      if (Objects.isNull(source) || targetType.isInstance(source)) {
//        return source;
//      } else if (source instanceof String && String[].class.isAssignableFrom(targetType)) {
//        Preconditions.condition(true, "Convert only from String");
//        // Preconditions.condition( String[].class.isAssignableFrom(targetType), "Convert to
//        // String
//        // []");
//        String[] params =
//            Stream.of(((String) source).trim().split("\\s*,\\s*"))
//                .map(s -> s.length() == 0 ? null : s)
//                .toArray(String[]::new);
//        log.debug("params: {}", Arrays.toString(params));
//
//        return params;
//      }
//      throw new ArgumentConversionException(
//          String.format(
//              "No implicit conversion to convert object of type %s to type %s.",
//              source.getClass().getName(), targetType.getName()));
//    }
//  }
//
//  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
//  @Retention(RetentionPolicy.RUNTIME)
//  @ConvertWith(RatingNormalizerArgumentConverter.class)
//  protected @interface ConvertRatingNormalizer {}
//
//  static class RatingNormalizerArgumentConverter extends SimpleArgumentConverter {
//    @Override
//    protected Object convert(Object source, Class<?> targetType)
//        throws ArgumentConversionException {
//
//      if (Objects.isNull(source) || targetType.isInstance(source)) {
//        return source;
//      }
//
//      // TODO 공통화
//      String[] params =
//          Stream.of(((String) source).trim().split("\\s*,\\s*", 6))
//              .map(s -> s.length() == 0 ? null : s)
//              .toArray(String[]::new);
//
//      log.debug("{} {}", this.getClass().getSimpleName(), Arrays.toString(params));
//
//      NormalizeMethod normalizeMethod = NormalizeMethod.valueOf(params[1]);
//
//      RatingNormalizer normalizer = null;
//      switch (normalizeMethod) {
//        case MEAN_CENTERING:
//          normalizer = new MeanCenteringNormalizer();
//          break;
//        case Z_SCORE:
//          normalizer = new ZScoreNormalizer();
//          break;
//        case MIN_MAX:
//          normalizer =
//              new MinMaxNormalizer()
//                  .setLower(Double.parseDouble(params[3]))
//                  .setUpper(Double.parseDouble(params[4]));
//          break;
//        case DECIMAL_SCALING:
//          normalizer = new DecimalScalingNormalizer();
//          break;
//        case BINARY_THRESHOLDING:
//          normalizer = new ThresholdBinarizer().setThreshold(Double.parseDouble(params[5]));
//          break;
//      }
//
//      if (Objects.nonNull(params[2])) {
//        Objects.requireNonNull(normalizer).setVerbose(Boolean.valueOf(params[2]));
//      }
//
//      // TODO 순서 변경 필요 NormalizeMethod 명 순으로
//      if (Objects.nonNull(params[0])) {
//        normalizer.setGroup(Group.valueOf(params[0]));
//      }
//
//      return normalizer;
//    }
//  }
//
//  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
//  @Retention(RetentionPolicy.RUNTIME)
//  @ConvertWith(RatingSimilarityMeasurerArgumentConverter.class)
//  protected @interface ConvertRatingSimilarityMeasurer {}
//
//  static class RatingSimilarityMeasurerArgumentConverter extends SimpleArgumentConverter {
//    @Override
//    protected Object convert(Object source, Class<?> targetType)
//        throws ArgumentConversionException {
//
//      if (Objects.isNull(source) || targetType.isInstance(source)) {
//        return source;
//      }
//
//      log.debug("source: {}", source);
//
//      String[] params =
//          Stream.of(((String) source).trim().split("\\s*,\\s*", 4))
//              .map(s -> s.length() == 0 ? null : s)
//              .toArray(String[]::new);
//
//      //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);
//
//      log.debug("params: {}", Arrays.toString(params));
//
//      Group group = Group.valueOf(params[0]);
//      SimilarityMeasure similarityMeasure = SimilarityMeasure.valueOf(params[1]);
//      boolean imputeZero = Boolean.parseBoolean(params[2]);
//      boolean verbose = Boolean.parseBoolean(params[3]);
//
//      RatingSimilarityMeasurer measurer = null;
//      switch (similarityMeasure) {
//        case COSINE:
//          measurer = new CosineSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case PEARSON:
//          measurer = new PearsonSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case MANHATTAN:
//          measurer = new ManhattanSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case EUCLIDEAN:
//          measurer = new EuclideanSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case BINARY_SMC:
//          measurer = new SimpleMatchingSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case BINARY_JACCARD:
//          measurer = new JaccardSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//        case BINARY_EXTENDED_JACCARD:
//          measurer = new ExtendedJaccardSimilarityMeasurer().setVerbose(verbose).setGroup(group);
//          break;
//      }
//
//      return Objects.requireNonNull(measurer).setImputeZero(imputeZero);
//    }
//  }
//
//  @Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
//  @Retention(RetentionPolicy.RUNTIME)
//  @ConvertWith(SimilarityMeasureArgumentConverter.class)
//  protected @interface ConvertSimilarityMeasure {}
//
//  static class SimilarityMeasureArgumentConverter extends SimpleArgumentConverter {
//    @Override
//    protected Object convert(Object source, Class<?> targetType)
//        throws ArgumentConversionException {
//
//      if (Objects.isNull(source) || targetType.isInstance(source)) {
//        return source;
//      }
//
//      log.debug("source: {}", source);
//
//      String[] params =
//          Stream.of(((String) source).trim().split("\\s*,\\s*", 4))
//              .map(s -> s.length() == 0 ? null : s)
//              .toArray(String[]::new);
//
//      //  String[] params = ((String) source).trim().split("\\s*,\\s*", 6);
//
//      log.debug("params: {}", Arrays.toString(params));
//
//      String measure = params[0];
//      boolean imputeZero = Boolean.parseBoolean(params[1]);
//      boolean verbose = Boolean.parseBoolean(params[2]);
//
//      return SimilarityMeasure.get(measure).invoke(imputeZero, verbose);
//    }
//  }
//
//  public String showString(
//      Dataset<Row> ds, String groupByCol, String pivotCol, String aggColName, int d) {
//    return showString(ds, groupByCol, pivotCol, aggColName, d, 50);
//  }
//
//  public String showString(
//      Dataset<Row> ds, String groupByCol, String pivotCol, String aggColName, int d, int numRows)
// {
//
//    Dataset<Row> pivotDS =
//        ds.withColumn(
//                aggColName,
//                when(col(aggColName).isNaN(), col(aggColName))
//                    .otherwise(format_number(round(col(aggColName), d), d)))
//            .select(groupByCol, pivotCol, aggColName)
//            .groupBy(groupByCol)
//            .pivot(pivotCol)
//            .agg(first(aggColName))
//            .na()
//            .fill("")
//            .orderBy(length(col(groupByCol)), col(groupByCol));
//
//    Comparator<String> comp = Comparator.comparing(e -> e.substring(0, 1));
//
//    String[] sortedColumns =
//        Stream.of(pivotDS.columns())
//            .skip(1)
//            .sorted(comp.thenComparing(String::length).thenComparing(Comparator.naturalOrder()))
//            .toArray(String[]::new);
//
//    return pivotDS.select(groupByCol, sortedColumns).showString(numRows, 0, false);
//  }
// }
