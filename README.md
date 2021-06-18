## Getting Started

Deploy with: docker-compose up

Test with: bash start_client.sh

Endpoint for acessing bft-smart replicas h2 database:   
https://localhost:{8090-8097}/h2

spring.datasource.url      = jdbc:h2:mem:state
spring.datasource.username = sa
spring.datasource.password =

Endpoint for acessing the proxys h2 database:           
https://localhost:{8080-8083}/h2

spring.datasource.url      = jdbc:h2:file:./ledger
spring.datasource.username = sa
spring.datasource.password =


Consult the file "./proxy/main/java/.../impl/LedgerController" for other endpoints.
