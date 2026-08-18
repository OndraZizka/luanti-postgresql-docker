

source ../config/config.sh

RESULTING_IMAGE=$DOCKERHUB_USER/luanti-mineclonia-mods:latest

docker build -f ./Dockerfile -t $RESULTING_IMAGE .
#docker login --username ondrejzizka   ## Needs a password from a personal token from Dockerhub.

echo
echo "Pushing: $RESULTING_IMAGE"
docker push $RESULTING_IMAGE
