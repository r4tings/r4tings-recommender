package_version(R.version)
if (!require('gtools')) install.packages('gtools', repos="https://cran.rstudio.com"); library('gtools')
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require("recommenderlab")) install.packages("recommenderlab", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("recommenderlab")

filePath <- file.path("dataset") # filePath <- file.path("C:/GitHub/r4tings-recommender/dataset")

list.files(filePath)
setwd(filePath)

###################
# Load Dataset    #
###################

system.time(ratings.dt <-
fread(
"r4tings/ratings.csv",
verbose = FALSE,
encoding = "UTF-8"
))


ratings.dt

cat("\014")

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
ratings.mat[, mixedsort(colnames(ratings.mat))]

###################
# Target User    #
###################

target_user.rrm <-ratings.rrm[4]
target_user.mat <-as(target_user.rrm, "matrix")
target_user.mat[, mixedsort(colnames(target_user.mat))]

###################
# UBC             #
###################

rec <-
Recommender(
ratings.rrm,
method = "UBCF",
parameter = list(
method = "cosine",
normalize = "center",
nn = 3
)
)

pre <- predict(rec, ratings.rrm[4], type = c("ratings"))
as(pre, "list")

rec <-
Recommender(
ratings.rrm,
method = "UBCF",
parameter = list(
method = "cosine",
normalize = "Z-score",
nn = 3,
verbose = T
)
)

pre <- predict(rec, ratings.rrm[4], type = c("topNList"))
pre@ratings
as(pre, "list")
