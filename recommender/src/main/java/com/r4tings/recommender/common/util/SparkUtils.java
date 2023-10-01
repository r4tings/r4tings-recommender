package com.r4tings.recommender.common.util;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.r4tings.recommender.common.Constants.SPARK;
import static org.apache.spark.sql.functions.*;

// @UtilityClass
public class SparkUtils {

  private SparkUtils() {}

  public static Column[] validateInputColumns(String[] columns, String... inputCols) {
    if (!Arrays.asList(columns).containsAll(Arrays.asList(inputCols))) {
      List<String> columnDifferences =
          Stream.of(inputCols)
              .filter(e -> Stream.of(columns).noneMatch(e::equals))
              .collect(Collectors.toList());

      throw new UnsupportedOperationException(
          String.format(
              "The following required columns are missing: '%s' in %s",
              String.join(", ", columnDifferences), Arrays.toString(columns)));
    }

    return Arrays.stream(inputCols).map(functions::col).toArray(Column[]::new);
  }

  public static Dataset<Row> zipWithIndex(Dataset<Row> df, String name) {
    return zipWithIndex(df, name, 0);
  }

  public static Dataset<Row> zipWithIndex(Dataset<Row> df, String name, long start) {
    JavaRDD<Row> rdd =
        df.javaRDD()
            .zipWithIndex()
            .map(
                t -> {
                  Row r = t._1;
                  Long index = t._2 + start;
                  ArrayList<Object> list = new ArrayList<>();
                  scala.collection.Iterator<Object> iterator = r.toSeq().iterator();
                  while (iterator.hasNext()) {
                    Object value = iterator.next();
                    assert value != null;
                    list.add(value);
                  }
                  list.add(index);

                  return RowFactory.create(list.toArray());
                });

    StructType newSchema =
        df.schema().add(new StructField(name, DataTypes.LongType, true, Metadata.empty()));

    return df.sparkSession().createDataFrame(rdd, newSchema);
  }

  //    https://www.waitingforcode.com/apache-spark-sql/join-types-spark-sql/read
  // NOT EXIST(NOT IN)
  /*
  return only left dataset information without matching
   */
  public static Dataset<Row> leftAntiJoin(
      Dataset<Row> leftDS, Dataset<Row> rightDS, String... joinCol) {

    return leftDS.join(
        rightDS,
        // JavaConverters.asScalaIteratorConverter( keyCols.iterator() ).asScala().toSeq()
        JavaConverters.asScalaBuffer(Arrays.asList(joinCol)),
        SPARK.JOINTYPE_LEFT_ANTI);
  }

  public static Dataset<Row> leftAntiJoin(
      Dataset<Row> leftDS, Dataset<Row> rightDS, Column joinColumn) {

    return leftDS.join(rightDS, joinColumn, SPARK.JOINTYPE_LEFT_ANTI);
  }

  // EXIST
  /*
  return only the matching rows with left table columns exclusively
    */
  public static Dataset<Row> leftSemiJoin(
      Dataset<Row> leftDS, Dataset<Row> rightDS, String... joinCol) {

    return leftDS.join(
        rightDS, JavaConverters.asScalaBuffer(Arrays.asList(joinCol)), SPARK.JOINTYPE_LEFT_SEMI);
  }

  public static Dataset<Row> leftSemiJoin(
      Dataset<Row> leftDS, Dataset<Row> rightDS, Column joinColumn) {

    return leftDS.join(rightDS, joinColumn, SPARK.JOINTYPE_LEFT_SEMI);
  }

  public static Dataset<Row> pivot(
      Dataset<Row> ds, String groupByCol, String pivotCol, String aggColName, int d) {

    Dataset<Row> pivotDS =
        ds.withColumn(
                aggColName,
                when(col(aggColName).isNaN(), col(aggColName))
                    .otherwise(format_number(round(col(aggColName), d), d)))
            .select(groupByCol, pivotCol, aggColName)
            .groupBy(groupByCol)
            .pivot(pivotCol)
            .agg(first(aggColName))
            .na()
            .fill("")
            .orderBy(length(col(groupByCol)), col(groupByCol));

    Comparator<String> comp = Comparator.comparing(e -> e.substring(0, 1));

    String[] sortedColumns =
        Stream.of(pivotDS.columns())
            .skip(1)
            .sorted(comp.thenComparing(String::length).thenComparing(Comparator.naturalOrder()))
            .toArray(String[]::new);

    return pivotDS.select(groupByCol, sortedColumns);
  }
}
