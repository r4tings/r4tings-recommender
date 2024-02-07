package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('gtools')) install.packages('gtools', repos="https://cran.rstudio.com"); library('gtools')
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org"); library('data.table')
if (!require("recommenderlab")) install.packages("recommenderlab", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("recommenderlab")
if (!require("reshape2")) install.packages("reshape2", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("reshape2")
if (!require("ggplot2")) install.packages("ggplot2", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("ggplot2")

# filePath <- file.path("dataset")
filePath <- file.path("C:/r4tings/r4tings-recommender/dataset")

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


df <- na.omit(melt(normalized.mat))  # reshaping
df <- df[order(df$Var1), ]   # ordering
colnames(df) <- c("user", "item", "rating") # setting colnames
 

dat = data.frame(rating=df$rating, nomalize="rating")

ggplot(dat, aes(x = rating, fill = nomalize)) + 
  geom_density()+
  # geom_histogram(aes(y = ..density..), breaks = seq(min, max, 0.01)) +
  facet_wrap( ~ nomalize, nrow = 4) + theme(legend.position="none") 


