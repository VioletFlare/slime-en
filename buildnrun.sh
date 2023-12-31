sudo docker build --no-cache -t slime .
sudo docker run --rm -it --network="host" slime
