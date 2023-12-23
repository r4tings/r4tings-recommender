package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require(data.table)) {
  install.packages("data.table", repos = "http://cran.us.r-project.org")
}
library(data.table)

install.packages("sparklyr", repos = "http://cran.us.r-project.org")


spark_available_versions()
spark_install(version = "2.4")


library(sparklyr)


print(Sys.setenv(DATASET_HOME = "C:/DEV/SCM/GitHub/r4tings-recommender/dataset"))
Sys.getenv("DATASET_HOME")
filePath <- file.path(Sys.getenv("DATASET_HOME"))
list.files(filePath)
setwd(filePath)

sc <- spark_connect(master = "local")

spark_tbl_handle <- spark_read_parquet(sc, "r4tings/ratings.parquet")


a <- data.table(collect(spark_tbl_handle))

str(a)


spark_disconnect(sc)

