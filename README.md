
# DHIS2 Self Reporting API

**Requirements**: JDK 11, Maven 3

### Quickstart

Compile the project using

```shell
$ mvn package
```

### Running

```shell
$ java -jar target/self-reporting.jar
```

### Example `application.properties`

```
self-reporting.base-url=
self-reporting.username=
self-reporting.password=
```

### Compile native image

**Requirements**: GraalVM 22 or later

### Compile and run

```
gu install native-image
mvn -Pnative -DskipTests clean package
```
