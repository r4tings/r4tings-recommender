package_version(R.version)

if (!require("ggplot2")) install.packages("ggplot2", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("ggplot2")
if (!require("ggridges")) install.packages("ggridges", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("ggridges")
if (!require("reshape2")) install.packages("reshape2", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("reshape2")
if (!require("corrplot")) install.packages("corrplot", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("corrplot")

if (!require('gtools')) install.packages('gtools', repos="https://cran.rstudio.com"); library('gtools')
if (!require('showtext')) install.packages('showtext', repos="https://cran.rstudio.com"); library('showtext')

showtext_auto()


# filePath <- file.path("dataset")
filePath <- file.path("C:/r4tings/r4tings-recommender/docs/workbook/ch04")

list.files(filePath)
setwd(filePath)

system.time(cosine_user.dt <- fread('cosine_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_user_MeanCenteringNormalizer.dt <- fread('cosine_user_MeanCenteringNormalizer.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_user_MeanCenteringNormalizer_true.dt <- fread('cosine_user_MeanCenteringNormalizer_true.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_user_ZScoreNormalizer.dt <- fread('cosine_user_ZScoreNormalizer.csv', verbose = FALSE, encoding = 'UTF-8'))

system.time(cosine_item.dt <- fread('cosine_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_item_MeanCenteringNormalizer.dt <- fread('cosine_item_MeanCenteringNormalizer.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_item_MeanCenteringNormalizer_true.dt <- fread('cosine_item_MeanCenteringNormalizer_true.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(cosine_item_ZScoreNormalizer.dt <- fread('cosine_item_ZScoreNormalizer.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(euclidean_item.dt <- fread('euclidean_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(euclidean_item_5.dt <- fread('euclidean_item_5.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(euclidean_user.dt <- fread('euclidean_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(euclidean_user_10.dt <- fread('euclidean_user_10.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(manhattan_item.dt <- fread('manhattan_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(manhattan_item_5.dt <- fread('manhattan_item_5.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(manhattan_user.dt <- fread('manhattan_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(manhattan_user_10.dt <- fread('manhattan_user_10.csv', verbose = FALSE, encoding = 'UTF-8'))

system.time(extendedJaccard_item.dt <- fread('extendedJaccard_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(extendedJaccard_user.dt <- fread('extendedJaccard_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(jaccard_item.dt <- fread('jaccard_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(jaccard_user.dt <- fread('jaccard_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(pearson_item.dt <- fread('pearson_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(pearson_user.dt <- fread('pearson_user.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(smc_item.dt <- fread('smc_item.csv', verbose = FALSE, encoding = 'UTF-8'))
system.time(smc_user.dt <- fread('smc_user.csv', verbose = FALSE, encoding = 'UTF-8'))


cosine_item.mat <- as.matrix(acast(cosine_item.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_item_MeanCenteringNormalizer.mat <- as.matrix(acast(cosine_item_MeanCenteringNormalizer.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_item_MeanCenteringNormalizer_true.mat <- as.matrix(acast(cosine_item_MeanCenteringNormalizer_true.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_item_ZScoreNormalizer.mat <- as.matrix(acast(cosine_item_ZScoreNormalizer.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_user.mat <-as.matrix(acast(cosine_user.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_user_MeanCenteringNormalizer.mat <- as.matrix(acast(cosine_user_MeanCenteringNormalizer.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_user_MeanCenteringNormalizer_true.mat <- as.matrix(acast(cosine_user_MeanCenteringNormalizer_true.dt, lhs ~ rhs, value.var = 'similarity'))
cosine_user_ZScoreNormalizer.mat <- as.matrix(acast(cosine_user_ZScoreNormalizer.dt, lhs ~ rhs, value.var = 'similarity'))
euclidean_item.mat <- as.matrix(acast(euclidean_item.dt, lhs ~ rhs, value.var = 'similarity'))
euclidean_item_5.mat <- as.matrix(acast(euclidean_item_5.dt, lhs ~ rhs, value.var = 'similarity'))
euclidean_user.mat <- as.matrix(acast(euclidean_user.dt, lhs ~ rhs, value.var = 'similarity'))
euclidean_user_10.mat <- as.matrix(acast(euclidean_user_10.dt, lhs ~ rhs, value.var = 'similarity'))
manhattan_item.mat <- as.matrix(acast(manhattan_item.dt, lhs ~ rhs, value.var = 'similarity'))
manhattan_item_5.mat <- as.matrix(acast(manhattan_item_5.dt, lhs ~ rhs, value.var = 'similarity'))
manhattan_user.mat <- as.matrix(acast(manhattan_user.dt, lhs ~ rhs, value.var = 'similarity'))
manhattan_user_10.mat <- as.matrix(acast(manhattan_user_10.dt, lhs ~ rhs, value.var = 'similarity'))

extendedJaccard_item.mat <- as.matrix(acast(extendedJaccard_item.dt, lhs ~ rhs, value.var = 'similarity'))
extendedJaccard_user.mat <- as.matrix(acast(extendedJaccard_user.dt, lhs ~ rhs, value.var = 'similarity'))
jaccard_item.mat <- as.matrix(acast(jaccard_item.dt, lhs ~ rhs, value.var = 'similarity'))
jaccard_user.mat <- as.matrix(acast(jaccard_user.dt, lhs ~ rhs, value.var = 'similarity'))
pearson_item.mat <- as.matrix(acast(pearson_item.dt, lhs ~ rhs, value.var = 'similarity'))
pearson_user.mat <- as.matrix(acast(pearson_user.dt, lhs ~ rhs, value.var = 'similarity'))
smc_item.mat <- as.matrix(acast(smc_item.dt, lhs ~ rhs, value.var = 'similarity'))
smc_user.mat <- as.matrix(acast(smc_user.dt, lhs ~ rhs, value.var = 'similarity'))


cosine_item.mat<- (cosine_item.mat[mixedsort(rownames(cosine_item.mat)), mixedsort(colnames(cosine_item.mat))])
cosine_item_MeanCenteringNormalizer.mat<- (cosine_item_MeanCenteringNormalizer.mat[mixedsort(rownames(cosine_item_MeanCenteringNormalizer.mat)), mixedsort(colnames(cosine_item_MeanCenteringNormalizer.mat))])
cosine_item_MeanCenteringNormalizer_true.mat<- (cosine_item_MeanCenteringNormalizer_true.mat[mixedsort(rownames(cosine_item_MeanCenteringNormalizer_true.mat)), mixedsort(colnames(cosine_item_MeanCenteringNormalizer_true.mat))])
cosine_item_ZScoreNormalizer.mat<- (cosine_item_ZScoreNormalizer.mat[mixedsort(rownames(cosine_item_ZScoreNormalizer.mat)), mixedsort(colnames(cosine_item_ZScoreNormalizer.mat))])
cosine_user.mat<- (cosine_user.mat[mixedsort(rownames(cosine_user.mat)), mixedsort(colnames(cosine_user.mat))])
cosine_user_MeanCenteringNormalizer.mat<- (cosine_user_MeanCenteringNormalizer.mat[mixedsort(rownames(cosine_user_MeanCenteringNormalizer.mat)), mixedsort(colnames(cosine_user_MeanCenteringNormalizer.mat))])
cosine_user_MeanCenteringNormalizer_true.mat<- (cosine_user_MeanCenteringNormalizer_true.mat[mixedsort(rownames(cosine_user_MeanCenteringNormalizer_true.mat)), mixedsort(colnames(cosine_user_MeanCenteringNormalizer_true.mat))])
cosine_user_ZScoreNormalizer.mat<- (cosine_user_ZScoreNormalizer.mat[mixedsort(rownames(cosine_user_ZScoreNormalizer.mat)), mixedsort(colnames(cosine_user_ZScoreNormalizer.mat))])
euclidean_item.mat<- (euclidean_item.mat[mixedsort(rownames(euclidean_item.mat)), mixedsort(colnames(euclidean_item.mat))])
euclidean_item_5.mat<- (euclidean_item_5.mat[mixedsort(rownames(euclidean_item_5.mat)), mixedsort(colnames(euclidean_item_5.mat))])
euclidean_user.mat<- (euclidean_user.mat[mixedsort(rownames(euclidean_user.mat)), mixedsort(colnames(euclidean_user.mat))])
euclidean_user_10.mat<- (euclidean_user_10.mat[mixedsort(rownames(euclidean_user_10.mat)), mixedsort(colnames(euclidean_user_10.mat))])
manhattan_item.mat<- (manhattan_item.mat[mixedsort(rownames(manhattan_item.mat)), mixedsort(colnames(manhattan_item.mat))])
manhattan_item_5.mat<- (manhattan_item_5.mat[mixedsort(rownames(manhattan_item_5.mat)), mixedsort(colnames(manhattan_item_5.mat))])
manhattan_user.mat<- (manhattan_user.mat[mixedsort(rownames(manhattan_user.mat)), mixedsort(colnames(manhattan_user.mat))])
manhattan_user_10.mat<- (manhattan_user_10.mat[mixedsort(rownames(manhattan_user_10.mat)), mixedsort(colnames(manhattan_user_10.mat))])

extendedJaccard_item.mat<- (extendedJaccard_item.mat[mixedsort(rownames(extendedJaccard_item.mat)), mixedsort(colnames(extendedJaccard_item.mat))])
extendedJaccard_user.mat<- (extendedJaccard_user.mat[mixedsort(rownames(extendedJaccard_user.mat)), mixedsort(colnames(extendedJaccard_user.mat))])
jaccard_item.mat<- (jaccard_item.mat[mixedsort(rownames(jaccard_item.mat)), mixedsort(colnames(jaccard_item.mat))])
jaccard_user.mat<- (jaccard_user.mat[mixedsort(rownames(jaccard_user.mat)), mixedsort(colnames(jaccard_user.mat))])
pearson_item.mat<- (pearson_item.mat[mixedsort(rownames(pearson_item.mat)), mixedsort(colnames(pearson_item.mat))])
pearson_user.mat<- (pearson_user.mat[mixedsort(rownames(pearson_user.mat)), mixedsort(colnames(pearson_user.mat))])
smc_item.mat<- (smc_item.mat[mixedsort(rownames(smc_item.mat)), mixedsort(colnames(smc_item.mat))])
smc_user.mat<- (smc_user.mat[mixedsort(rownames(smc_user.mat)), mixedsort(colnames(smc_user.mat))])





#dcast.similarity.dt <- acast(cosine_user_ZScoreNormalizer.dt, lhs ~ rhs, value.var = "similarity") # library(reshape2)
#str(dcast.similarity.dt)
#similarity.mat <- as.matrix(dcast.similarity.dt)
#dimnames(similarity.mat) <- list(lhs = rownames(similarity.mat), rhs = colnames(similarity.mat))



custom_colors <- colorRampPalette(c("blue","red"))(10)

trunc_digits <- function(x,digits){if (!is.na(mat[x])) x else e <- 10^digits; trunc(x*e) / e} 

trunc_digits <- function(mat,digits) {
  
  result <- mat  # Initialize a matrix with NaN
  
  e <- 10^digits
  
  for (i in 1:nrow(mat)) {
    for (j in 1:ncol(mat)) {
      if (!is.nan(mat[i, j])) {
        
        if(trunc(mat[i, j]*e)/e !=0){
        
        string_representation <- as.character(mat[i, j])
        cut_string <- if( substr(string_representation, 1, 1) == "-") substr(string_representation, 1, 7) else substr(string_representation, 1, 6)
        converted_number <- as.numeric(cut_string)

        result[i, j] <- converted_number
        } else{
          result[i, j] <- 0.0000
        }
        
      }else{
        result[i, j] <-NaN
      }
    }
  }
  
  return(result)
  
}


# 코사인 유사도 (사용자)


par(mfrow = c(2, 2))


corrplot.mixed(
  trunc_digits(cosine_user.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 코사인 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(cosine_user_MeanCenteringNormalizer.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="사용자 코사인 유사도(평균 중심화)", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(cosine_user_MeanCenteringNormalizer_true.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 코사인 유사도(평균 중심화/결측값 0 대체)", mar=c(0,0,2,0),
  cex.main = 1.2,
)

corrplot.mixed(
  trunc_digits(cosine_user_ZScoreNormalizer.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 코사인 유사도(Z점수화)", mar=c(0,0,2,0),
  cex.main = 1.2,
)

resetPar <- function() {
  dev.new()
  op <- par(no.readonly = TRUE)
  dev.off()
  op
}

par(resetPar())  

# 코사인 유사도 - 아이템

par(mfrow = c(2, 2))


corrplot.mixed(
  trunc_digits(cosine_item.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="아이템 코사인 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(cosine_item_MeanCenteringNormalizer.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="아이템 코사인 유사도(평균 중심화)", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(cosine_item_MeanCenteringNormalizer_true.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="아이템 코사인 유사도(평균 중심화/결측값 0 대체)", mar=c(0,0,2,0),
  cex.main = 1.2,
)

corrplot.mixed(
  trunc_digits(cosine_item_ZScoreNormalizer.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="아이템 코사인 유사도(Z점수화)", mar=c(0,0,2,0),
  cex.main = 1.2,
)

resetPar <- function() {
  dev.new()
  op <- par(no.readonly = TRUE)
  dev.off()
  op
}

par(resetPar())  

#+---+----------+----------+----------+----------+---------+----------+----------+---------+----------+----------+
#|lhs|        i1|        i2|        i3|        i4|       i5|        i6|        i7|       i8|        i9|       i10|
#+---+----------+----------+----------+----------+---------+----------+----------+---------+----------+----------+
#| i1| 1.0000000|-0.4891160| 0.9754410|-0.7071068|0.0000000| 0.0000000| 1.0000000|0.0000000|-0.7592566|-0.9486833|
#| i2|-0.4891160| 1.0000000|-0.7279472|-0.4934638|0.0000000| 0.6082192|-0.9065472|0.0000000| 0.6257827| 0.9912279|
#| i3| 0.9754410|-0.7279472| 1.0000000|-0.7739573|0.0000000| 0.3431640| 0.9325886|      NaN| 0.1104315|-0.9950372|
#| i4|-0.7071068|-0.4934638|-0.7739573| 1.0000000|0.0000000|-0.9805807| 0.0000000|0.0000000|-0.8320503| 0.0000000|
#| i5| 0.0000000| 0.0000000| 0.0000000| 0.0000000|0.0000000| 0.0000000| 0.0000000|      NaN| 0.0000000| 0.0000000|
#| i6| 0.0000000| 0.6082192| 0.3431640|-0.9805807|0.0000000| 1.0000000|-0.8944272|0.0000000| 0.8660254| 0.0000000|
#| i7| 1.0000000|-0.9065472| 0.9325886| 0.0000000|0.0000000|-0.8944272| 1.0000000|      NaN| 0.0000000|-0.9486833|
#| i8| 0.0000000| 0.0000000|       NaN| 0.0000000|      NaN| 0.0000000|       NaN|0.0000000| 0.0000000|       NaN|
#| i9|-0.7592566| 0.6257827| 0.1104315|-0.8320503|0.0000000| 0.8660254| 0.0000000|0.0000000| 1.0000000| 0.0000000|
#|i10|-0.9486833| 0.9912279|-0.9950372| 0.0000000|0.0000000| 0.0000000|-0.9486833|      NaN| 0.0000000| 1.0000000|
# +---+----------+----------+----------+----------+---------+----------+----------+---------+----------+----------+


# 코사인 유사도 (사용자)


par(mfrow = c(2, 2))


corrplot.mixed(
  trunc_digits(pearson_user.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 피어슨 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(pearson_item.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="아이템 피어슨 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)

par(resetPar())  



# 거리 유사도 (사용자)


par(mfrow = c(2, 2))


corrplot.mixed(
  trunc_digits(euclidean_user.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 유클리드 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(euclidean_user_10.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="사용자 가중 유클리드 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)

corrplot.mixed(
  trunc_digits(manhattan_user.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="사용자 맨해튼 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(manhattan_user_10.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="사용자 가중 맨해튼 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


par(resetPar())  

# 거리 유사도 (아이템)


par(mfrow = c(2, 2))


corrplot.mixed(
  trunc_digits(euclidean_item.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="아이템 유클리드 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(euclidean_item_5.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="아이템 가중 유클리드 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(manhattan_item.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  title="아이템 맨해튼 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)


corrplot.mixed(
  trunc_digits(manhattan_item_5.mat,4),lower="number",upper="pie",
  number.digits = 4,
  upper.col = custom_colors,
  lower.col = custom_colors,
  main="아이템 가중 맨해튼 유사도", mar=c(0,0,2,0),
  cex.main = 1.2,
)

par(resetPar())  