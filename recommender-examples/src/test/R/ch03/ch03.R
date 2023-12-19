package_version(R.version)

if (!require("ggplot2")) install.packages("ggplot2", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("ggplot2")
if (!require("ggridges")) install.packages("ggridges", repos = "http://cran.us.r-project.org" , dependencies = TRUE); library("ggridges")


# filePath <- file.path("dataset")
filePath <- file.path("C:/r4tings/r4tings-recommender/docs/workbook/ch03")

list.files(filePath)
setwd(filePath)

system.time(
  raw.dt <-
    fread(
      "raw.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  mean.dt <-
    fread(
      "mean.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  mean_user.dt <-
    fread(
      "mean_user.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  mean_item.dt <-
    fread(
      "mean_item.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  zscore.dt <-
    fread(
      "zscore.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  zscore_user.dt <-
    fread(
      "zscore_user.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  zscore_item.dt <-
    fread(
      "zscore_item.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  minmax.dt <-
    fread(
      "minmax.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  minmax_user.dt <-
    fread(
      "minmax_user.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  minmax_item.dt <-
    fread(
      "minmax_item.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)


system.time(
  decimal.dt <-
    fread(
      "decimal.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)

system.time(
  binary.dt <-
    fread(
      "binary.csv",
      verbose = FALSE,
      encoding = "UTF-8"
    )
)


p1 <- hist(raw.dt$rating, breaks=10, freq = FALSE)
p2 <- hist(mean.dt$rating, breaks=10, freq = FALSE)
p3 <- hist(mean_user.dt$rating, breaks=10, freq = FALSE)
p4 <- hist(mean_item.dt$rating, breaks=10, freq = FALSE)


lines(density(raw.dt$rating)) 

dat1 = data.frame(rating=raw.dt$rating, method="원본")
dat2 = data.frame(rating=mean.dt$rating, method="평균중심화")
dat3 = data.frame(rating=mean_user.dt$rating, method="평균중심화(사용자)")
dat4 = data.frame(rating=mean_item.dt$rating, method="평균중심화(아이템)")
dat5 = data.frame(rating=zscore.dt$rating, method="Z점수")
dat6 = data.frame(rating=zscore_user.dt$rating, method="Z점수(사용자)")
dat7 = data.frame(rating=zscore_item.dt$rating, method="Z점수(아이템)")
dat8 = data.frame(rating=minmax.dt$rating, method="최소최대")
dat9 = data.frame(rating=minmax_user.dt$rating, method="최소최대(사용자)")
dat10 = data.frame(rating=minmax_item.dt$rating, method="최소최대(아이템)")
dat11 = data.frame(rating=decimal.dt$rating, method="소수자릿수")
dat12 = data.frame(rating=binary.dt$rating, method="이진임계")



dat = rbind(dat1,dat2, dat3, dat4)

dat = rbind(dat1,dat2, dat3, dat4, na.omit(dat5), na.omit(dat6), na.omit(dat7), na.omit(dat8), na.omit(dat9), na.omit(dat10), dat11, dat12)

(min <- min(dat$rating))
(max <-max(dat$rating))

ggplot(dat, aes(x = rating, , y=method, fill=method, color=method)) +
  xlim(round(min) -1, round(max)+2) +
  geom_vline(xintercept = 3, linetype= "dotted", size=1, color="red") +
  annotate(x = 3, y = -Inf, label = "평점 평균", vjust = -1, geom = "text") +
  geom_vline(xintercept = 0, linetype= "dotted", size=1) +
  geom_density_ridges(alpha=0.7, scale=1, stat="binline") + 
  ggtitle("평균 중심 정규화") +   
  xlab("평점") +
  ylab("평점 정규화") +
  
  theme_ridges() +
  theme(
    plot.title = element_text(hjust = 0.5 ,size=20, face='bold'),
#    legend.position="none",
legend.title=element_blank(),

    panel.spacing = unit(0.1, "lines"),
    strip.text.x = element_text(size = 8)
  ) 

## 참고

ggplot(dat, aes(x = rating, , y=method, fill=method, color=method)) + 
  xlim(round(min) -1, round(max)+2) +
  geom_vline(xintercept = 3, linetype= "dotted", size=1, color="red") +
  annotate(x = 3, y = -Inf, label = "평점 평균", vjust = -1, geom = "text") +
  geom_vline(xintercept = 0, linetype= "dotted", size=1) +
  geom_density_ridges(alpha=0.7, scale=1) + 
  ggtitle("평균 중심 정규화") +   
  theme_ridges() +
  theme(
    plot.title = element_text(hjust = 0.5 ,size=20, face='bold'),
    legend.title=element_blank(),
    panel.spacing = unit(0.1, "lines"),
    strip.text.x = element_text(size = 8)
  ) + 
  xlab("평점") +
  ylab("평점 정규화") 

  


ggplot(dat, aes(rating, fill = method)) + 
  geom_vline(xintercept = 3, linetype= "dotted", size=1) +
  annotate(x = 3, y = +Inf, label = "평균", vjust = 2, geom = "label") +
  geom_vline(xintercept = 0, linetype= "dotted", size=1) +
  annotate(x = 0, y = +Inf, label = "0", vjust = 2, geom = "label") +
  geom_histogram(aes(y = ..density..), binwidth = 0.1) + 
  geom_density(alpha=0.2, lwd=0.8, adjust=0.8) +
  facet_grid(method ~ .)


plot( p1, col=rgb(0,0,1,1/4), xlim=c(0,10))  # first histogram


p5 <- hist(raw.dt$rating, breaks=10, freq = FALSE)
p6 <- hist(raw.dt$rating, breaks=10, freq = FALSE)
p7 <- hist(raw.dt$rating, breaks=10, freq = FALSE)
p8 <- hist(raw.dt$rating, breaks=10, freq = FALSE)

