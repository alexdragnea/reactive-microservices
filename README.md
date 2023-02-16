# Reactive Microservices

## Microservices Architecture

This project is the copy of [Spring-Cloud-Microservices](https://github.com/alexdragnea/spring-cloud-microservices) build on reactive style with Spring Webflux.

The project structure is made up of:

- Rating/Book services which are Spring Webflux based projects which performs CRUD operations.
- API Gateway from Spring Cloud family.
- Eureka Discovery Service which hold the information about all client-service applications.
- MongoDB for persisting the data.
