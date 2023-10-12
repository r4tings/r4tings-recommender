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

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
ratings.mat[, mixedsort(colnames(ratings.mat))]

###################
# Euclidean       # 
###################

###################
# User-User       # 
###################

ratings.mat[c("u4", "u5"),]

(dissimilarity.rrm <- dissimilarity(ratings.rrm, method = "euclidean", which = "user"))

similarity.mat <- as(1 / (1 + dissimilarity.rrm), "matrix")

(tril(similarity.mat))

similarity.mat["u4", "u5"]

###################
# Item-Item       # 
###################

t(ratings.mat[, c("i3", "i1")])

(dissimilarity.rrm <- dissimilarity(ratings.rrm, method = "euclidean", which = "item"))

similarity.mat <- as(1 / (1 + dissimilarity.rrm), "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))

similarity.mat["i3", "i1"]
