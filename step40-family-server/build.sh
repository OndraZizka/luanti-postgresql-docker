

source ../config/config.sh

IMAGE_ID=$DOCKERHUB_USER/luanti-mineclonia-zizka:latest

docker build -f ./Dockerfile -t $IMAGE_ID .
#docker login --username ondrejzizka   ## Needs a password from a personal token from Dockerhub.
docker push $IMAGE_ID
