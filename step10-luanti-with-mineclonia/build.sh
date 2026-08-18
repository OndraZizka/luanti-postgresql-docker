

DOCKERHUB_USER=ondrejzizka
RESULTING_IMAGE=$DOCKERHUB_USER/luanti-mineclonia:latest

docker build -f ./Dockerfile -t $RESULTING_IMAGE .
#docker login --username ondrejzizka   ## Needs a password from a personal token from Dockerhub.

echo
echo "Pushing: $IMAGE_ID"
docker push $RESULTING_IMAGE
