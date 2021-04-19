## Getting Started

Step 1: Install docker compose 

Step 2: Deploy with the command:    docker-compose up

Step 3: Enjoy


Endpoint for acessing bft-smart replicas state:   https://localhost:8090-8093/h2

Endpoint for acessing the proxys ledger:          https://localhost:8080/h2   https://localhost:8082/h2


Consult the file "./proxy/main/java/.../impl/LedgerController" for other endpoints.


### How to build the project

Package and install the submodule "./common" in your maven local repo.
Install the package "./lib/BFT-Smart" in your maven local repo.

Package the submodules "./proxy" and "./replica"
