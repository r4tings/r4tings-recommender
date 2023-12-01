package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('gtools')) install.packages('gtools', repos="https://cran.rstudio.com"); library('gtools')
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require("recommenderlab")) install.packages("recommenderlab", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("recommenderlab")
if (!require('dplyr')) install.packages('dplyr', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('dplyr')  # alternative installation of the %>%
if (!require('scales')) install.packages('scales', repos = "http://cran.us.r-project.org"); library('scales')

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

###################
# Min-Max         # 
###################

ratings.rrm <- as(ratings.dt, "realRatingMatrix")
ratings.mat <- as(ratings.rrm, "matrix")
(ratings.mat[, mixedsort(colnames(ratings.mat))])

ratings.mat["u4", "i1"]

normalized.mat <- scales:::rescale(ratings.mat, to = c(1, 5))
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]

normalized.df <- ratings.dt %>%
  group_by(user) %>%
  mutate(rating = rescale(
    rating,
    to = c(1, 5),
    from = range(rating, na.rm = TRUE, finite = TRUE)
  ))

normalized.dt <- data.table(normalized.df)
normalized.rrm <- as(normalized.dt, "realRatingMatrix")
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]

normalized.df <- ratings.dt %>%
  group_by(item) %>%
  mutate(rating = rescale(
    rating,
    to = c(1, 5),
    from = range(rating, na.rm = TRUE, finite = TRUE)
  ))

normalized.dt <- data.table(normalized.df)
normalized.rrm <- as(normalized.dt, "realRatingMatrix")
normalized.mat <- as(normalized.rrm, "matrix")
(normalized.mat[, mixedsort(colnames(normalized.mat))])

normalized.mat["u4", "i1"]
