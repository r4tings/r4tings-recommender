package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('data.table')) install.packages('data.table', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('data.table')
if (!require('vctrs')) install.packages('vctrs', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('vctrs')  # alternative installation of the %>%
if (!require('dlookr')) install.packages('dlookr', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('dlookr')
if (!require('dplyr')) install.packages('dplyr', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('dplyr')  # alternative installation of the %>%

filePath <- file.path("dataset")
list.files(filePath)
setwd(filePath)

#########################
# Book-Crossing dataset # 
#########################

system.time(
  bx_books.dt <-
    fread(
      "Book-Crossing/BX-Books.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

bx_books.dt

dlookr::diagnose(bx_books.dt)

# bx_books.dt %>% diagnose_report(output_format = "html")

system.time(
  bx_ratings.dt <-
    fread(
      "Book-Crossing/BX-Book-Ratings.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

bx_ratings.dt

dlookr::diagnose(bx_ratings.dt)

# bx_ratings.dt %>% diagnose_report(output_format = "html")

#####################
# MovieLens dataset # 
#####################

system.time(
  ml_movies.dt <-
    fread(
      "MovieLens/ml-latest/movies.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

ml_movies.dt

dlookr::diagnose(ml_movies.dt)

# ml_movies.dt %>% diagnose_report(output_format = "html")

system.time(
  ml_ratings.dt <-
    fread(
      "MovieLens/ml-latest/ratings.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

ml_ratings.dt

dlookr::diagnose(ml_movies.dt)

# ml_movies.dt %>% diagnose_report(output_format = "html")

system.time(
  ml_tags.dt <-
    fread(
      "MovieLens/ml-latest/tags.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

ml_tags.dt

dlookr::diagnose(ml_tags.dt)

# ml_tags.dt %>% diagnose_report(output_format = "html")

###################
# r4tings dataset # 
###################

system.time(
  r4_items.dt <-
    fread(
      "r4tings/items.csv",
      verbose = FALSE,
      encoding = "UTF-8")
)

r4_items.dt

dlookr::diagnose(r4_items.dt)

# r4_items.dt %>% diagnose_report(output_format = "html")

system.time(
  r4_ratings.dt <-
    fread(
      "r4tings/ratings.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

r4_ratings.dt

dlookr::diagnose(r4_ratings.dt)

# r4_ratings.dt %>% diagnose_report(output_format = "html")

system.time(
  r4_tags.dt <-
    fread(
      "r4tings/tags.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

r4_tags.dt

dlookr::diagnose(r4_tags.dt)

# r4_tags.dt %>% diagnose_report(output_format = "html")

system.time(
  r4_terms.dt <-
    fread(
      "r4tings/terms.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

r4_terms.dt

dlookr::diagnose(r4_terms.dt)

# r4_terms.dt %>% diagnose_report(output_format = "html")
