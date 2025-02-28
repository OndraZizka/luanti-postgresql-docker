

source ../config/config.sh

IMAGE_MINECLONIA=$DOCKERHUB_USER/luanti-mineclonia-demo:latest

docker build -f ./Dockerfile -t $IMAGE_MINECLONIA .
#docker login --username ondrejzizka   ## Needs a password from a personal token from Dockerhub.
docker push $IMAGE_MINECLONIA
