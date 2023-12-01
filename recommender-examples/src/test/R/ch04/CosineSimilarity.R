package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require('recommenderlab')) install.packages('recommenderlab', repos = "http://cran.us.r-project.org"); library('recommenderlab')
if (!require('gtools')) install.packages('gtools', repos = "https://cran.rstudio.com"); library('gtools')

filePath <- file.path("dataset") # filePath <- file.path("C:/GitHub/r4tings-recommender/dataset")

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
# Cosine          # 
###################

###################
# User-User       # 
###################

ratings.mat[c("u4", "u5"),]

similarity.rrm <- similarity(ratings.rrm, method = "cosine", which = "user")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]


normalized.rrm <- normalize(ratings.rrm, method = "center", row = T)
normalized.mat <- as(normalized.rrm, "matrix")
normalized.mat[, mixedsort(colnames(normalized.mat))]
normalized.mat[c("u4", "u5"),]


# TODO rmm -> dist 
similarity.dist <- similarity(normalized.rrm, method = "cosine", which = "user")
similarity.mat <- as(similarity.dist, "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]


normalized.mat[is.na(normalized.mat)] <- 0
normalized.rrm <- as(normalized.mat, "realRatingMatrix")
normalized.mat[c("u4", "u5"),]

similarity.rrm <- similarity(normalized.rrm, method = "cosine", which = "user")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]


normalized.rrm <- normalize(ratings.rrm, method = "Z-score", row = T)
normalized.mat <- as(normalized.rrm, "matrix")
normalized.mat[, mixedsort(colnames(normalized.mat))]
normalized.mat[c("u4", "u5"),]

similarity.rrm <- similarity(normalized.rrm, method = "cosine", which = "user")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat))
similarity.mat["u4", "u5"]

###################
# Item-Item       # 
###################

t(ratings.mat[, c("i3", "i1")])

similarity.rrm <- similarity(ratings.rrm, method = "cosine", which = "item")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))
similarity.mat["i3", "i1"]


normalized.rrm <- normalize(ratings.rrm, method = "center", row = F)
normalized.mat <- as(normalized.rrm, "matrix")
normalized.mat[, mixedsort(colnames(normalized.mat))]
t(normalized.mat[, c("i3", "i1")])

similarity.rrm <- similarity(normalized.rrm, method = "cosine", which = "item")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))
similarity.mat["i3", "i1"]


normalized.mat[is.na(normalized.mat)] <- 0
normalized.rrm <- as(normalized.mat, "realRatingMatrix")
t(normalized.mat[, c("i3", "i1")])

similarity.rrm <- similarity(normalized.rrm, method = "cosine", which = "item")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))
similarity.mat["i3", "i1"]


normalized.rrm <- normalize(ratings.rrm, method = "Z-score", row = F)
normalized.mat <- as(normalized.rrm, "matrix")
normalized.mat[, mixedsort(colnames(normalized.mat))]
t(normalized.mat[, c("i3", "i1")])

similarity.rrm <- similarity(normalized.rrm, method = "cosine", which = "item")
similarity.mat <- as(similarity.rrm, "matrix")

(tril(similarity.mat[mixedsort(rownames(similarity.mat)), mixedsort(colnames(similarity.mat))]))
similarity.mat["i3", "i1"]
