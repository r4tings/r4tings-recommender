if (!require('data.table')) install.packages('data.table'); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab'); library('recommenderlab')
if (!require('scales')) install.packages('scales'); library('scales')

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
