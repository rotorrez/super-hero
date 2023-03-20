# Super Hero

Super Hero Api Version 1.0

## Requirement

Desarrollar, utilizando Maven, Spring Boot, y Java, una API que permita hacer un mantenimiento CRUD de súper héroes.

## Documentation
Swagger: 'http://localhost:8080/swagger-ui/index.html'

## Example Endpoints and payloads

### Get All Super Heroes
curl --location 'http://localhost:8080/superheros' \
--data ''

### Get Super Hero by Id
curl --location 'http://localhost:8080/superheros/1' \
--data ''

### Create Super Hero
curl --location 'http://localhost:8080/superheros' \
--header 'Content-Type: application/json' \
--data '{
"name": "Superman"
}'

### Delete Super Hero by Id
curl --location --request DELETE 'http://localhost:8080/superheros/2' \
--data ''

### Update Super Hero by Id
curl --location --request PUT 'http://localhost:8080/superheros/1' \
--header 'Content-Type: application/json' \
--data '{
"name": "Hulk"
}'

### Get Super Hero by Name
curl --location 'http://localhost:8080/superheros/search/man' \
--data ''