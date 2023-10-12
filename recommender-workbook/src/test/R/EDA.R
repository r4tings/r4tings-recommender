if (!require(data.table)) {
  install.packages("data.table")
}else {
  library(data.table)
}
if (!require(dlookr)) {
  install.packages("dlookr")
}else {
  library(dlookr)
}
if (!require(dplyr)) {
  install.packages("dplyr")   # alternative installation of the %>%
}else {
  library(dplyr)
}
library(kableExtra)


print(Sys.setenv(DATASET_HOME = " C:\Users\user\Documents\r4tings-recommender-workbook\data"))
Sys.getenv("DATASET_HOME")
filePath <- file.path(Sys.getenv("DATASET_HOME"))
list.files(filePath)
setwd(filePath)


system.time(r4_items.dt <- fread("r4tings/items.csv", verbose = TRUE, encoding = "UTF-8"))

dlookr::diagnose(r4_items.dt)

r4_items.dt %>%
  diagnose_report(output_format = "html")

r4_items.dt %>%
  eda_report(output_format = "html")


system.time(r4_ratings.dt <- fread("r4tings/ratings.csv", verbose = TRUE, encoding = "UTF-8", colClasses = 'character'))

dlookr::diagnose(r4_ratings.dt)

r4_ratings.dt %>%
  diagnose_report(output_format = "html")

r4_ratings.dt %>%
  eda_report(output_format = "html")


system.time(r4_keywords.dt <- fread("r4tings/keywords.csv", verbose = TRUE, encoding = "UTF-8"))

dlookr::diagnose(r4_keywords.dt)

r4_keywords.dt %>%
  diagnose_report(output_format = "html")

r4_keywords.dt %>%
  eda_report(output_format = "html")


system.time(ml_movies.dt <- fread("MovieLens/ml-latest/movies.csv", na.strings = "NA", verbose = TRUE, encoding = "UTF-8"))

dlookr::diagnose(ml_movies.dt)

ml_movies.dt %>%
  diagnose_report(output_format = "html")

diagnose_category(ml_movies.dt, "genres", top = 50)


system.time(ml_ratings.dt <- fread("MovieLens/ml-latest/ratings.csv", na.strings = "NA", verbose = TRUE, encoding = "UTF-8", colClasses = 'character'))

diagnose_category(ml_ratings.dt, "rating", top = 50)

dlookr::diagnose(ml_ratings.dt)

ml_ratings.dt[, .N, by = .(userId, movieId)]

ml_ratings.dt %>%
  diagnose_report(output_format = "html")


system.time(ml_tags.dt <- fread("Movielens/ml-latest/tags.csv", na.strings = c("NA", "NULL"), verbose = TRUE, encoding = "UTF-8"))

describe(ml_tags.dt)

ml_tags.dt[order(tag),]


dlookr::diagnose(ml_tags.dt)

ml_tags.dt %>%
  eda_report(output_format = "html")

ml_tags.dt %>%
  diagnose_report(output_format = "html")

ml_tags.dt %>%
  select(find_na(.)) %>%
  diagnose()

ml_tags.dt[is.na(ml_tags.dt$tag),]

ml_tags.dt[, .N, by = .(userId, movieId)]


sum(is.na(ml_tags.dt$tag))

system.time(bx_users.dt <- fread("Book-Crossing/BX-Users.csv", na.strings = c(NULL, "", " ", "NULL"), verbose = TRUE, encoding = "UTF-8", colClasses = 'character'))

dlookr::diagnose(bx_users.dt, "User-ID", "Location", "Age")


bx_users.dt[, .N, by = .(`User-ID`)]


bx_users.dt %>%
  diagnose_report(output_format = "html")


diagn_overview <- diagnose(bx_users.dt)

names(diagn_overview) <- c("속성", "유형", "결측치(n)",
                           "결측치(%)", "고유치(n)",
                           "고유치(n/N)")

cap <- " 사용자 데이터 CSV 파일(BX-Users.csv)의 기본 구조"

knitr::kable(diagn_overview, digits = 2, caption = cap, format = "html",
             format.args = list(big.mark = ",")) %>%
  kable_styling(full_width = FALSE, font_size = 15, position = "left")


system.time(bx_books.dt <- fread("Book-Crossing/BX-Books.csv", na.strings = c("", " ", "NULL"), verbose = TRUE, encoding = "UTF-8", colClasses = 'character'))


ml_ratings.dt[, .N, by = .(userId, movieId)]


dlookr::diagnose(bx_books.dt)

bx_books.dt %>%
  diagnose_report(output_format = "html")

bx_books.dt[, .N, by = .(ISBN)]


system.time(bx_ratings.dt <- fread("Book-Crossing/BX-Book-Ratings.csv", na.strings = c("", " ", "NULL"), verbose = TRUE, encoding = "UTF-8", colClasses = 'character'))
bx_ratings.dt[bx_ratings.dt == 0] <- NA

dlookr::diagnose(bx_ratings.dt)

bx_ratings.dt[, .N, by = .(`User-ID`, ISBN)]


diagnose_category(bx_ratings.dt, "Book-Rating", top = 11)

v123[order(v1, -v2, v3),]


bx_ratings.dt %>%
  diagnose_report(output_format = "html")


a <- bx_ratings.dt[is.na("Book-Rating"), "Book-Rating" := 0]

str(bx_ratings.dt)

# https://rdrr.io/cran/dlookr/f/inst/report/Diagnosis_Report_KR.Rmd

diagnose_outlier(bx_ratings.dt)

bx_ratings.dt %>%
  diagnose_report(output_format = "html")


diagnose(bx_ratings.dt, `User-ID`, ISBN, `Book-Rating`)

bx_ratings.dt %>% dlookr::describe()


# install.packages("devtools")
# devtools::install_github("tidyverse/tibble")
# install.packages("Rttf2pt1", dependencies = TRUE)
# install.packages("tibble",dependencies = TRUE)
# https://funnystatistics.tistory.com/23
# install Rtools and run Rstudio as administrator.
if (!require(dlookr)) {
  install.packages("dlookr")
}else {
  library(dlookr)
}

install.packages("dplyr")    # alternative installation of the %>%
library(dplyr)    # alternatively, this also loads %>%


dlookr::diagnose(bx_users.dt)

# remove.packages ("dlookr")

