package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab', repos = "http://cran.us.r-project.org"); library('recommenderlab')
if (!require('gtools')) install.packages('gtools', repos = "https://cran.rstudio.com"); library('gtools')


filePath <- file.path("dataset")
# filePath <- file.path("C:/GitHub/r4tings-recommender/dataset")

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
