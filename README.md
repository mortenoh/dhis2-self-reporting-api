
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

### Using

After starting up `self-report` you should now have a API endpoint on `<host>/api/self-reporting/vital-signs`

You can then send JSON payloads like this

````json
{
    "id": "TEI-UID",
    "systolic": "x",
    "diastolic": "x",
    "pulse": "x",
    "weight": "x"
}
````

### Compile native image

**Requirements**: GraalVM 22 or later

#### Compile and run

```
gu install native-image
mvn -Pnative -DskipTests clean package
./target/self-reporting
```
