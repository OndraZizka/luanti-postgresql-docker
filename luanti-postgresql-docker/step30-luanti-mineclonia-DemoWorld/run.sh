

source ../config/config.sh

docker run -it --rm -e "CLI_ARGS=--gameid Mineclonia" --name luanti-mineclonia-demo $DOCKERHUB_USER/luanti-mineclonia-demo

##  See inside:
# docker exec -it luanti-mineclonia-demo sh
# ls -la config/.minetest/games/

##  Kill it:
# docker container kill luanti-mineclonia-demo