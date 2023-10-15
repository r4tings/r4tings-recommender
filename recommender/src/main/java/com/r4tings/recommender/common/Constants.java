/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common;

public class Constants {

  private Constants() {}

  public static class SPARK {

    private SPARK() {}

    public static final String HINT_BROADCAST = "broadcast";

    @Deprecated public static final String JOINTYPE_LEFT_OUTER = "left_outer";

    @Deprecated public static final String JOINTYPE_LEFT_ANTI = "leftanti";

    @Deprecated public static final String JOINTYPE_LEFT_SEMI = "leftsemi";

    @Deprecated public static final int DEFAULT_NUM_ROWS = 10;
  }

  public static class COL {

    private COL() {}

    public static final String USER = "user";
    public static final String ITEM = "item";
    public static final String RATING = "rating";

    public static final String OUTPUT = "output";

    // Normalization
    public static final String MEAN = "mean";
    public static final String STDDEV = "stddev";

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String NEW_MIN = "new_min";
    public static final String NEW_MAX = "new_max";

    // Similarity
    public static final String RHS = "rhs";
    public static final String LHS = "lhs";
    public static final String SIMILARITY = "similarity";

    public static final String RATINGS = "ratings";
    public static final String RHS_RATINGS = "rhs_ratings";
    public static final String LHS_RATINGS = "lhs_ratings";

    public static final String INDEX = "index";
    public static final String LHS_INDEX = "lhs_index";
    public static final String RHS_INDEX = "rhs_index";

    // Recommender
    public static final String SCORE = "score";

    public static final String RANK = "rank";

    // KNN
    public static final String LHS_MEAN = "lhs_mean";
    public static final String LHS_STDDEV = "lhs_stddev";

    public static final String RHS_MEAN = "rhs_mean";
    public static final String RHS_STDDEV = "rhs_stddev";
    public static final String NEIGHBORS = "neighbors";

    // Baseline
    public static final String ITEM_BIAS = "item_bias";
    public static final String USER_BIAS = "user_bias";
    public static final String BIAS = "bias";
    public static final String RESIDUAL = "residual";

    // SVD
    public static final String DIMENSION = "dimension";
    public static final String USER_INDEX = "user_index";
    public static final String ITEM_INDEX = "item_index";
    public static final String USER_FEATURES = "user_features";
    public static final String ITEM_FEATURES = "item_features";
    public static final String USV = "usv";

    // TFIDF
    public static final String TERM = "term";
    public static final String DOCUMENT = "document";
    public static final String USER_DOCUMENT = "user_document";
    public static final String DOCUMENT_TFIDF = "document_tfidf";
    public static final String USER_TFIDF = "user_tfidf";
    public static final String TF = "tf";
    public static final String IDF = "idf";
    public static final String DF = "df";
    public static final String TFIDF = "tfidf";
    public static final String EUCLIDEAN_NORM = "euclidean_norm";
    public static final String LENGTH_NORMALIZED_TFIDF = "length_normalized_tfidf";

    // ARM
    public static final String SUPPORT = "support";
    public static final String CONFIDENCE = "confidence";
    public static final String LIFT = "lift";
    public static final String LEVERAGE = "leverage";
    public static final String CONVICTION = "conviction";

    public static final String TRANSACTIONS = "transactions";
    public static final String TID = "tid";
    public static final String SUPPORT_COUNT = "support_count";
    public static final String LHS_TRANSACTIONS = "lhs_transactions";
    public static final String LHS_SUPPORT = "lhs_support";
    public static final String RHS_TRANSACTIONS = "rhs_transactions";
    public static final String RHS_SUPPORT = "rhs_support";
  }
}
