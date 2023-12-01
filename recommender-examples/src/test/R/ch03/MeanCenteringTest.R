package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('gtools')) install.packages('gtools', repos="https://cran.rstudio.com"); library('gtools')
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require("recommenderlab")) install.packages("recommenderlab", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("recommenderlab")

filePath <- file.path("dataset")

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
# Mean Centering  # 
###################

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
(ratings.mat[, mixedsort(colnames(ratings.mat))])

ratings.mat["u4", "i1"]

normalized.rrm <- normalize(ratings.rrm, method = "center", row = T)
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]

normalized.rrm <- normalize(ratings.rrm, method = "center", row = F)
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]
