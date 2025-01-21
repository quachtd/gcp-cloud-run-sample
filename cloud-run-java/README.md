# Cloud Run Validator

A template repository for a Cloud Run microservice, written in Java. [Link](https://github.com/GoogleCloudPlatform/cloud-code-custom-samples-example/)

### Prerequisite
Create 2 files applicaiton-dev.properties and application-prod.properties under resources dir. Values are different for reach enviroment.
```bash
# jpa - postgresql
spring.datasource.url=jdbc:postgresql://{db_server_host}:{db_port}/{db_name}
spring.datasource.username={user}
spring.datasource.password={password}
```

### Run
```bash
mvn spring-boot:run
```
### Build and push image to Artifact Registry

```bash
cd cloud-run-java
mvn compile jib:build
```

### Reference
[Cloud code cutom samples][Link]

[Link]: https://github.com/GoogleCloudPlatform/cloud-code-custom-samples-example/