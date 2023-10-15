/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.model.tfidf;

import com.r4tings.recommender.common.ml.CommonParams;
import com.r4tings.recommender.common.ml.param.TermCol;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.sql.expressions.UserDefinedFunction;

import java.util.Objects;

import static com.r4tings.recommender.common.Constants.COL;

@Slf4j
public class TermFrequencyInverseDocumentFrequencyParams
    extends CommonParams<TermFrequencyInverseDocumentFrequencyParams> implements TermCol {

  public TermFrequencyInverseDocumentFrequencyParams() {
    super(TermFrequencyInverseDocumentFrequencyParams.class.getSimpleName());

    setDefault(termCol(), COL.TERM);
    setDefault(outputCol(), COL.SCORE);
    setDefault(threshold(), 0d);
  }

  @Override
  protected TermFrequencyInverseDocumentFrequencyParams self() {
    return this;
  }

  /*
   * Param Definitions
   */

  @Override
  public Param<String> termCol() {
    return new Param<>(this, "termCol", "column name for terms..");
  }

  public DoubleParam threshold() {
    return new DoubleParam(this, "threshold", "threshold is ,,,,");
  }

  public Param<UserDefinedFunction> similarityMeasure() {
    return new Param<>(
        this,
        "SimilarityMeasure",
        "The default value of the similarityMeasure is the value returned when getSimilarityMeasure() is called."
            + "[i.e. return SimilarityMeasure.COSINE.invoke(true, false)]");
  }

  /*
   * Param Getters
   */

  @Override
  public String getTermCol() {
    return getOrDefault(termCol());
  }

  public Double getThreshold() {
    return get(threshold()).isEmpty() ? null : (Double) get(threshold()).get();
  }

  public UserDefinedFunction getSimilarityMeasure() {
    //    return get(similarityMeasure()).isEmpty()
    //        ? SimilarityMeasure.COSINE.invoke(true, false)
    //        : get(similarityMeasure()).get();

    return getOrDefault(similarityMeasure());
  }

  /*
   *  Param Setters
   */

  @Override
  public TermFrequencyInverseDocumentFrequencyParams setTermCol(String value) {
    if (Objects.nonNull(value)) {
      set(termCol(), value);
    }

    return this;
  }

  public TermFrequencyInverseDocumentFrequencyParams setThreshold(Double value) {
    if (Objects.nonNull(value)) {
      set(threshold(), value);
    }

    return this;
  }

  public TermFrequencyInverseDocumentFrequencyParams setSimilarityMeasure(
      UserDefinedFunction value) {
    if (Objects.nonNull(value)) {
      set(similarityMeasure(), value);
    }

    return this;
  }
}
