if (!require('data.table')) install.packages('data.table'); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab'); library('recommenderlab')
if (!require('rknn')) install.packages('rknn'); library('rknn')

filePath <- file.path("C:/Users/user/Documents/r4tings-recommender-workbook/dataset")
# filePath <- file.path("C:/GitHub/r4tings-recommender-workbook/dataset")

list.files(filePath)
setwd(filePath)

###################
# Load Dataset    # 
###################

system.time(
  ratings.dt <-
    fread(
      "r4tings/ratings.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

ratings.dt

cat("\014")

####################
# Decimal Scailing # 
####################

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
(ratings.mat[, mixedsort(colnames(ratings.mat))])

ratings.mat["u4", "i1"]

ratings.mat[is.na(ratings.mat)] = 0
normalized.mat <- normalize.decscale(ratings.mat)
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]
