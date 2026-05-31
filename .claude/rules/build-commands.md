---
description: Maven build, test, and Docker commands for this project
alwaysApply: true
---

## Build & Run

```bash
mvn clean install
mvn clean package -DskipTests
mvn spring-boot:run
```

## Tests

```bash
mvn test
mvn test -Dtest=ClassName           # single test class
mvn test -Dtest=ClassName#method    # single test method
```

## Docker

```bash
docker build -t cctv-core-app:latest .
```
