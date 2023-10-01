package com.r4tings.recommender.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.RealMatrix;

import java.text.DecimalFormat;
import java.util.stream.IntStream;

@Slf4j
// @UtilityClass
public class DebugUtils {

  public static final String DOUBLE_DELTA_7_FORMAT_STRING = "0.0######";

  private DebugUtils() {}

  public static String showRealMatrix(RealMatrix realMatrix) {
    return showSubMatrix(realMatrix.getData(), DOUBLE_DELTA_7_FORMAT_STRING, 12, "");
  }

  public static String showMatrix(double[][] matrix) {
    return showSubMatrix(matrix, DOUBLE_DELTA_7_FORMAT_STRING, 12, "");
  }

  public static String showSubMatrix(
      double[][] matrix, String pattern, int width, String separator) {
    StringBuilder stringBuilder = new StringBuilder();

    DecimalFormat df = new DecimalFormat(pattern);
    // df.setRoundingMode(RoundingMode.DOWN);
    String format = "%".concat(String.valueOf(width)).concat("s").concat(separator);

    int index = 1;
    for (double[] row : matrix) {

      if (separator.length() == 0) {
        if (index == 1) {
          stringBuilder.append("\n");
          stringBuilder.append(String.format(format, ""));
          IntStream.range(1, row.length + 1)
              .forEach(
                  v ->
                      stringBuilder.append(
                          String.format(
                              "%".concat(String.valueOf(width)).concat("s"), "[" + v + "]")));

          stringBuilder.append("\n");
        }

        stringBuilder.append(String.format(format, "[" + index + "]"));
      }

      for (double elem : row) {
        String v = Double.isNaN(elem) ? "NA" : df.format(elem);

        stringBuilder.append(String.format(format, v));
      }
      stringBuilder.append("\n");

      index++;
    }

    //  logger.info("{}", stringBuilder.toString());

    return stringBuilder.toString();
  }
}
