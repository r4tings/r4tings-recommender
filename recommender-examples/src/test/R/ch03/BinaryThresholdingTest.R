if (!require('data.table')) install.packages('data.table'); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab'); library('recommenderlab')

filePath <- file.path("C:/Users/user/Documents/r4tings-recommender-examples/dataset")
# filePath <- file.path("C:/GitHub/r4tings-recommender-examples/dataset")

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

#######################
# Binary Thresholding # 
#######################

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
(ratings.mat[, mixedsort(colnames(ratings.mat))])

ratings.mat["u4", "i1"]

binarized.rrm <- binarize(ratings.rrm, minRating = 3)
binarized.mat <- as(binarized.rrm, "matrix")
1 * (binarized.mat[, mixedsort(colnames(binarized.mat))])

1 * binarized.mat["u4", "i1"]


