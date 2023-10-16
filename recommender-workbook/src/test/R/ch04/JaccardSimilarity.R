if (!require('data.table')) install.packages('data.table'); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab'); library('recommenderlab')
if (!require('gtools')) install.packages('gtools'); library('gtools')

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

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
ratings.mat[, mixedsort(colnames(ratings.mat))]


#############################
# Jaccard & Simple Matching # 
#############################

binarized.brm <- binarize(ratings.rrm, minRating = 3)
binarized.mat <- 1 * as(binarized.brm, "matrix")
binarized.mat[, mixedsort(colnames(binarized.mat))]

###################
# User-User       # 
###################

binarized.mat[, mixedsort(colnames(binarized.mat))][c("u4", "u5"),]

similarity.rrm <- similarity(binarized.brm, method = "jaccard", which = "user")

similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]

similarity.mat <- 1 - as(dissimilarity(binarized.brm, method = "matching", which = "user"), "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]

###################
# Item-Item       # 
###################

t(binarized.mat[, c("i3", "i1")])

similarity.rrm <- similarity(binarized.brm, method = "jaccard", which = "item")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))

similarity.mat["i3", "i1"]

similarity.mat <- 1 - as(dissimilarity(binarized.brm, method = "matching", which = "item"), "matrix")

(tril(similarity.mat))
similarity.mat["i3", "i1"]
