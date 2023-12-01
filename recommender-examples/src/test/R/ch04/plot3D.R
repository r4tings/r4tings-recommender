package_version(R.version)
if (!require('devtools')) install.packages('devtools', repos = "http://cran.us.r-project.org", dependencies = TRUE); library('devtools')  # alternative installation of the %>%
find_rtools()
if (!require('plot3D')) install.packages('plot3D', repos = "http://cran.us.r-project.org"); library('plot3D')

(a <- c(1, 3, 4))
(b <- c(3, 2, 3))


border3D(0, 0, 0, 5, 5, 5, theta = 50, phi = 30, ticktype = "detailed", col = "darkgray", col.grid = "lightgray", contour = T)

#A
arrows3D(0, 0, 0, 1, 3, 4, lwd = 2, col = "black", add = T)
text3D(1.1, 3.1, 4.1, "a (1,3,4)", col = "black", add = T)

#B
arrows3D(0, 0, 0, 3, 2, 3, lwd = 2, col = "black", add = T)
text3D(3.1, 2.1, 2.9, "b (3,2,3)", col = "black", add = T)

#A-B(theta)
arrows3D(.2, .6, .8, .6, .4, .6, col = "green", lwd = 2, lty = 1, code = 0, add = T)
text3D(.2, .7, .7, expression(bold(theta)), add = T)

#A-B euclid
arrows3D(1, 3, 4, 3, 2, 3, col = "red", lwd = 2, lty = 2, code = 0, add = T)

#A-B manhattan
arrows3D(1, 3, 4, 3, 3, 4, col = "blue", lwd = 2, lty = 3, code = 0, add = T)
arrows3D(3, 3, 4, 3, 3, 3, col = "blue", lwd = 2, lty = 3, code = 0, add = T)
arrows3D(3, 3, 3, 3, 2, 3, col = "blue", lwd = 2, lty = 3, code = 0, add = T)


# dist box
rect3D(1, 3, 4, x1 = 3, z1 = 3, facets = TRUE, lty = 0, col = "darkgray", border = "black", alpha = 0.1, add = T)
rect3D(1, 2, 4, x1 = 3, z1 = 3, facets = TRUE, lty = 0, col = "darkgray", border = "black", alpha = 0.1, add = T)
rect3D(1, 3, 4, y1 = 2, z1 = 3, facets = TRUE, lty = 0, col = "darkgray", border = "black", alpha = 0.1, add = T)
rect3D(3, 3, 4, y1 = 2, z1 = 3, , facets = TRUE, lty = 0, col = "darkgray", border = "black", alpha = 0.1, add = T)

rect3D(1, 3, 0, x1 = 3, y1 = 2, facets = TRUE, lty = 2, col = "darkgray", border = "black", alpha = 0.1, add = T)
rect3D(0, 3, 4, y1 = 2, z1 = 3, facets = TRUE, lty = 2, col = "darkgray", border = "black", alpha = 0.1, add = T)
rect3D(5, 3, 4, y1 = 2, z1 = 3, facets = TRUE, lty = 2, col = "darkgray", border = "black", alpha = 0.1, add = T)

#X
rect3D(5, 0, 0, x1 = 1, y1 = 3, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)
rect3D(5, 0, 0, x1 = 3, y1 = 2, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)

#arrows3D(1, 3, 4, 1, 3, 0 , col="darkgray", lwd=1, lty=2, code=0, add=T)
#arrows3D(3, 2, 3, 3,2, 0 , col="darkgray", lwd=1, lty=2, code=0, add=T)

#Y
rect3D(5, 0, 0, y1 = 3, z1 = 4, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)
rect3D(5, 0, 0, y1 = 2, z1 = 3, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)

#arrows3D(1, 3, 4, 5, 3, 4 , col="darkgray", lwd=1, lty=2, code=0, add=T)
#arrows3D(3, 2, 3, 5,2, 3 , col="darkgray", lwd=1, lty=2, code=0, add=T)
#Z
rect3D(0, 0, 0, y1 = 3, z1 = 4, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)
rect3D(0, 0, 0, y1 = 2, z1 = 3, , facets = TRUE, lty = 3, col = "white", border = "lightblue", alpha = 0.1, add = T)

#arrows3D(1, 3, 4, 0, 3, 4 , col="darkgray", lwd=1, lty=2, code=0, add=T)
#arrows3D(3, 2, 3, 0,2, 3 , col="darkgray", lwd=1, lty=2, code=0, add=T)


#rect3D(0,0,0,  x1=1.5,y1=1,  , facets = TRUE, lty=3, col="white", border ="lightblue", alpha=0.1, add=T)


arrows3D(3, 3, 4, 1, 3, 4, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)
arrows3D(1, 3, 0, 1, 3, 4, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)


arrows3D(5, 2, 3, 3, 2, 3, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)
arrows3D(3, 2, 0, 3, 2, 3, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)

arrows3D(1, 2, 4, 3, 2, 4, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)
arrows3D(4, 3, 4, 3, 3, 4, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)
arrows3D(1, 3, 3, 3, 3, 3, col = "darkgray", lwd = 1, lty = 2, code = 0, add = T)


#sim(A,C)
dist(rbind(a, b))
#as.vector(dist(rbind(a,b)) )

simil(rbind(a, b), method = 'cosine')

dist(rbind(a, b), method = 'euclidean')
dist(rbind(a, b), method = 'manhattan')


simil(rbind(a, c), method = 'cosine')

