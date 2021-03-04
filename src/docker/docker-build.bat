echo off

REM docker-build.bat writeon 2.0.0-SNAPSHOT snapshots 1

echo building and pushing the docker image
echo Application: %1
echo Version: %2
echo Tag %3

echo Press CTRL+C to exit, or
pause

REM ./RoomScreen-0.0.1-SNAPSHOT.jar

echo docker build
docker build -t %1:%2-%3 --no-cache --build-arg VERSION=%2 --build-arg APPLICATION=%1 . 

echo docker tag %1:%2-%3
docker tag %1:%2-%3 thornhill:5000/%1:%2-%3

echo docker push thornhill:5000/%1:%2-%3
docker push thornhill:5000/%1:%2-%3

echo Done, (don't forget to run the new container on the server!)
pause
