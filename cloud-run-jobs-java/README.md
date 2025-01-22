# Cloud Run Job

A template repository for a Cloud Run Job with Spring boot to call local file (Cloud Storage via mounting), Cloud SQL w/wo VPC (private/public ip). Written in Java.

## Prerequisite
Java 17

Create 2 files <em>applicaiton-dev.properties</em> and <em>application-prod.properties</em> under __resources__ dir. Values are different for reach enviroment.
```bash
# jpa - postgresql
spring.datasource.url=jdbc:postgresql://{db_server_host}:{db_port}/{db_name}
spring.datasource.username={user}
spring.datasource.password={password}
```

## Run
```bash
mvn spring-boot:run
```

## Build and push container image to Artifact Registry

```bash
mvn compile jib:build
```


