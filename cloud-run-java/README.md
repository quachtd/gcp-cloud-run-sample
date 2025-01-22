# Cloud Run Validator

A template repository for a Cloud Run to call Cloud Storage, local file (via mounting), Cloud SQL w/wo VPC (private/public ip), Cloud Run Job. Written in Java.

## Prerequisite
Java 17

Create 2 files <em>applicaiton-dev.properties</em> and <em>application-prod.properties</em> under __resources__ dir. Values are different for reach enviroment.
```bash
# jpa - postgresql
spring.datasource.url=jdbc:postgresql://{db_server_host}:{db_port}/{db_name}
spring.datasource.username={user}
spring.datasource.password={password}
# db2rest endpoint
db2rest_endpoint=https://{endpoint}
```

## Run
```bash
mvn spring-boot:run
```

## Test
**Execute Cloud Run Job**
```bash
http../validator?opt=job
```

**Cloud Storage write and read - directly**
```bash
http../validator?opt=storage.write&bucket={bucket-name}&env=vpc
http../validator?opt=storage.read&bucket={bucket-name}&env=vpc
```

**Cloud Storage write and read - via volumn mount (file)**
```bash
http../validator?opt=storage.write&&env=local
http../validator?opt=storage.read&env=local
```

**JDBC - public or private ip depend where we deploy the app**
```bash
http../validator?opt=jdbc&&table={tableName}
```

## Build and push image to Artifact Registry

```bash
mvn compile jib:build
```

## Reference
[Cloud code cutom samples][Link]

[Link]: https://github.com/GoogleCloudPlatform/cloud-code-custom-samples-example/