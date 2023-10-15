/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.util;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.spark.sql.functions.*;

// @Slf4j
// @UtilityClass
public class VerboseUtils {

  private VerboseUtils() {}

  public static String showString(Dataset<Row> ds, int numRows) {

    return "\n" + ds.showString(numRows, 0, false);
  }

  public static void show(
      Dataset<Row> dataset, String orderByColName, String resultColName, int d) {
    dataset
        .orderBy(length(col(orderByColName)), col(orderByColName))
        .withColumn(resultColName, round(col(resultColName), d))
        .show();
  }

  public static void showPivot(Dataset<Row> ds, String groupByCol, String pivotCol, int d) {

    ds.select(groupByCol, pivotCol).groupBy(groupByCol).pivot(pivotCol).agg(first(pivotCol)).show();
  }

  public static void showPivot(
      Dataset<Row> ds, String groupByCol, String pivotCol, String aggColName, int d) {

    if (ds.count() > 100) {
      ds.orderBy(col(aggColName).desc()).show();
    } else {
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

      pivotDS.select(groupByCol, sortedColumns).show();
    }
  }

  public static void showPivot2(
      Dataset<Row> dataset,
      String groupByCol,
      String pivotCol,
      String aggColName,
      boolean truncate) {

    long count = dataset.count();

    if (count > 100) {
      dataset.orderBy(col(aggColName).desc()).show();
    } else {
      Dataset<Row> pivotDS =
          dataset
              .select(groupByCol, pivotCol, aggColName)
              .groupBy(groupByCol)
              .pivot(pivotCol)
              .agg(
                  when(
                      size(collect_list(aggColName)).$greater(0d),
                      sort_array(collect_list(aggColName), true)))
              .na()
              .fill("")
              .orderBy(length(col(groupByCol)), col(groupByCol));

      String[] sortedColumns =
          Stream.of(pivotDS.columns())
              .skip(1)
              .sorted(
                  Comparator.nullsFirst(
                      Comparator.comparing(String::length)
                          .thenComparing(Comparator.naturalOrder())))
              .toArray(String[]::new);

      pivotDS.select(groupByCol, sortedColumns).show(truncate);
    }
  }

  public static void showCrosstab(Dataset<Row> dataset, String col1, String col2) {

    String crosstabCol = String.join("_", col1, col2);

    Dataset<Row> crosstabDS = dataset.stat().crosstab(col1, col2).orderBy(crosstabCol);

    List<String> colList = Arrays.asList(crosstabDS.columns());
    colList.sort(
        (s1, s2) -> {
          //  final String regExp = "[0-9]+([,.][0-9]{1,2})?";

          if (Objects.equals(s2, crosstabCol)) {
            return 0;
          } else {
            try {
              Integer n1 = Integer.parseInt(s1.replaceFirst("[a-zA-Z]", ""));
              Integer n2 = Integer.parseInt(s2.replaceFirst("[a-zA-Z]", ""));

              return n1 - n2;
            } catch (NumberFormatException nfe) {
              return s1.compareTo(s2);
            }
          }
        });

    List<Column> cols1 = colList.stream().map(Column::new).collect(Collectors.toList());

    crosstabDS
        .select(JavaConverters.asScalaBuffer(cols1))
        .orderBy(length(col(crosstabCol)), col(crosstabCol))
        .show();
  }

  public static void console(
      Boolean verbose,
      Logger logger,
      String label,
      Dataset<Row> dataset,
      String groupByCol,
      String pivotCol,
      String aggColName,
      int d) {

    if (Objects.equals(verbose, Boolean.TRUE)) {
      logger.warn("{}.count: {}", label, dataset.count());
      logger.info("{}.schema: {}", label, dataset.schema().simpleString());
      showPivot(dataset, groupByCol, pivotCol, aggColName, d);
    }
  }

  public static void console(Boolean verbose, Logger logger, String label, Dataset<Row> dataset) {
    console(verbose, logger, label, dataset, false, false);
  }

  public static void console(
      Boolean verbose,
      Logger logger,
      String label,
      Dataset<Row> ds,
      boolean truncate,
      boolean explain) {

    if (Objects.equals(verbose, Boolean.TRUE)) {
      logger.info("{}.count: {}", label, ds.count());
      logger.info("{}.schema: {}", label, ds.schema().simpleString());
      ds.show(truncate);
      if (explain) {
        ds.explain();
      }
    }
  }
}
