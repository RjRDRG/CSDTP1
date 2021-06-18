#!/bin/bash

docker rm -f csd_client_cointainer

docker build -f client.dockerfile --tag csd_client .

docker run -it --name csd_client_cointainer csd_client
