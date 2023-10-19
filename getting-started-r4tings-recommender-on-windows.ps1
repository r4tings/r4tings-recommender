# TODO OSS 검증 후 삭제 set-up-the-project-on-windows.ps1로 대체됨

Write-Host -Foregroundcolor black -backgroundcolor white "`n Get started R4tings Recommender on windows"

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Deleting a exist project folder"

pause

Remove-Item -path /r4tings -recurse -confirm

Write-Host -Foregroundcolor black -backgroundcolor white "`n Setting up R4tings Recommender project"

pause

#########################
# 프로젝트 구성
#########################

cd /

mkdir r4tings

cd r4tings

Invoke-WebRequest https://github.com/r4tings/r4tings-recommender/archive/refs/heads/main.zip -OutFile r4tings-recommender-main.zip

Expand-Archive -LiteralPath r4tings-recommender-main.zip -DestinationPath .

Rename-Item -Path r4tings-recommender-main -NewName r4tings-recommender

cd r4tings-recommender

ls

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n R4tings Recommender project requires java 11 to run"
java -version

pause

Write-Host -Foregroundcolor black -backgroundcolor white "`n Build gradle project"

pause

./gradlew clean build -x test

pause
