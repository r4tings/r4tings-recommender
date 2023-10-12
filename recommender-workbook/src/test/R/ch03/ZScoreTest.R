if (!require('data.table')) install.packages('data.table'); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab'); library('recommenderlab')

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

###################
# Z-Score         # 
###################

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
(ratings.mat[, mixedsort(colnames(ratings.mat))])

ratings.mat["u4", "i1"]

normalized.rrm <- normalize(ratings.rrm, method = "Z-score", row = T)
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]

normalized.rrm <- normalize(ratings.rrm, method = "Z-score", row = F)
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]