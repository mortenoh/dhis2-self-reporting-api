**WARNING** This just a simple example app for All-Devs 2022 Hackathon, most UIDs are hard coded and does not reflect
any real world scenario.

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
self-reporting.program-id=
self-reporting.first-name-attribute=
self-reporting.last-name-attribute=
self-reporting.dob-attribute=
```

### Using

#### Reporting

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

#### Retrieving the Profile

You'd be able to request the profile data by sending a GET to the endpoint on `<host>/api/self-reporting/vital-signs`

A sample response would be as follows.

```json
{
  "status": "OK",
  "info": {
    "firstName": "Evelyn",
    "lastName": "Jackson",
    "dob": null
  }
}
```

### Compile native image

**Requirements**: GraalVM 22 or later

#### Compile and run

```
gu install native-image
mvn -Pnative -DskipTests clean package
./target/self-reporting
```
