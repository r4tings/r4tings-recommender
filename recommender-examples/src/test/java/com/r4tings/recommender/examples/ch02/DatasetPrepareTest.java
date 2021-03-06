/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.examples.ch02;

import com.r4tings.recommender.test.AbstractSparkTests;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class DatasetPrepareTest extends AbstractSparkTests {

  // @Disabled
  @ParameterizedTest
  @CsvSource({
    "dataset/r4tings/items.csv   , true",
    "dataset/r4tings/ratings.csv , true",
    "dataset/r4tings/terms.csv   , true",
    "dataset/r4tings/tags.csv    , true",
  })
  public void r4tingsDataset(@ConvertPathString String path, Boolean parquetSave) {

    Map<String, String> options =
        Stream.of(
                new SimpleEntry<>("header", "true"),
                new SimpleEntry<>("inferSchema", "true"),
                new SimpleEntry<>("ignoreLeadingWhiteSpace", "true"),
                new SimpleEntry<>("ignoreTrailingWhiteSpace", "true"))
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    Dataset<Row> csvDS = spark.read().options(options).csv(path);

    log.info("count: {}", csvDS.count());
    csvDS.schema().printTreeString();
    csvDS.show();

    if (parquetSave) {
      String parquetPath = path.replace("csv", "parquet");
      csvDS.repartition(1).write().mode(SaveMode.Overwrite).parquet(parquetPath);
      assertEquals(0, csvDS.except(spark.read().load(parquetPath)).count());
    }
  }

  // @Disabled
  @ParameterizedTest
  @CsvSource({
    "http://www2.informatik.uni-freiburg.de/~cziegler/BX/BX-CSV-Dump.zip, /dataset/Book-Crossing/, BX-CSV-Dump.zip",
    "https://files.grouplens.org/datasets/movielens/ml-latest-small.zip , /dataset/MovieLens/    , ml-latest-samll.zip  ",
    "https://files.grouplens.org/datasets/movielens/ml-latest.zip       , /dataset/MovieLens/    , ml-latest.zip  ",
  })
  public void downloadPublicDatasets(
      String source, @ConvertPathString String path, String downloadFile) {

    try {
      File destination = new File(path + downloadFile);

      if (!destination.exists()) {
        log.info("\nCopy [{}] to [{}]", source, destination);
        FileUtils.copyURLToFile(new URL(source), destination);
      }

      new ZipFile(destination).extractAll(path);

      assertTrue(destination.exists());

    } catch (Exception e) {
      log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }
  }

  // @Disabled
  @ParameterizedTest
  @CsvSource({
    "dataset/Book-Crossing/BX-Books.csv       , true",
    "dataset/Book-Crossing/BX-Book-Ratings.csv, true",
  })
  public void bookCrossingDataset(@ConvertPathString String path, Boolean parquetSave) {

    Map<String, String> options =
        Stream.of(
                new SimpleEntry<>("header", "true"),
                new SimpleEntry<>("ignoreLeadingWhiteSpace", "true"),
                new SimpleEntry<>("ignoreTrailingWhiteSpace", "true"),
                new SimpleEntry<>("sep", ";"))
            .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    Dataset<Row> csvDS = null;
    if (path.contains("BX-Books.csv")) {
      StructType schema =
          DataTypes.createStructType(
              new StructField[] {
                DataTypes.createStructField("ISBN", DataTypes.StringType, false),
                DataTypes.createStructField("Book-Title", DataTypes.StringType, false),
                DataTypes.createStructField("Book-Author", DataTypes.StringType, false),
                DataTypes.createStructField("Year-Of-Publication", DataTypes.IntegerType, false),
                DataTypes.createStructField("Publisher", DataTypes.StringType, false),
                DataTypes.createStructField("Image-URL-S", DataTypes.StringType, false),
                DataTypes.createStructField("Image-URL-M", DataTypes.StringType, false),
                DataTypes.createStructField("Image-URL-L", DataTypes.StringType, false),
              });
      csvDS = spark.read().schema(schema).options(options).csv(path);

    } else if (path.contains("BX-Book-Ratings.csv")) {
      StructType schema =
          DataTypes.createStructType(
              new StructField[] {
                DataTypes.createStructField("User-ID", DataTypes.LongType, false),
                DataTypes.createStructField("ISBN", DataTypes.StringType, false),
                DataTypes.createStructField("Book-Rating", DataTypes.DoubleType, false),
              });
      csvDS =
          spark
              .read()
              .schema(schema)
              .options(options)
              .csv(path)
              .where(not(col("Book-Rating").equalTo(0)));
    }

    log.info("count: {}", Objects.requireNonNull(csvDS).count());

    csvDS.schema().printTreeString();
    csvDS.show();

    if (parquetSave) {
      String parquetPath = path.replace("csv", "parquet");
      csvDS.repartition(1).write().mode(SaveMode.Overwrite).parquet(parquetPath);
      assertEquals(0, csvDS.except(spark.read().load(parquetPath)).count());
    }
  }

  // @Disabled
  @ParameterizedTest
  @CsvSource({
    "dataset/MovieLens/ml-latest/movies.csv       , false",
    "dataset/MovieLens/ml-latest/ratings.csv      , false",
    "dataset/MovieLens/ml-latest/tags.csv         , false",
    //    "dataset/MovieLens/ml-coursera/movies.csv     , false",
    //    "dataset/MovieLens/ml-coursera/ratings.csv    , false",
    //    "dataset/MovieLens/ml-coursera/tags.csv       , false",
    //    "dataset/MovieLens/ml-latest-small/movies.csv , false",
    //    "dataset/MovieLens/ml-latest-small/ratings.csv, false",
    //    "dataset/MovieLens/ml-latest-small/tags.csv   , false",
  })
  public void movieLensDataset(@ConvertPathString String path, Boolean parquetSave) {

    Map<String, String> options =
        Stream.of(
                new SimpleEntry<>("header", "true"),
                new SimpleEntry<>("inferSchema", "true"),
                new SimpleEntry<>("ignoreLeadingWhiteSpace", "true"),
                new SimpleEntry<>("ignoreTrailingWhiteSpace", "true"))
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    Dataset<Row> csvDS = spark.read().options(options).csv(path);

    log.info("count: {}", Objects.requireNonNull(csvDS).count());

    csvDS.schema().printTreeString();
    csvDS.show();

    if (parquetSave) {
      String parquetPath = path.replace("csv", "parquet");
      csvDS.repartition(1).write().mode(SaveMode.Overwrite).parquet(parquetPath);
      assertEquals(0, csvDS.except(spark.read().load(parquetPath)).count());
    }
  }
}
