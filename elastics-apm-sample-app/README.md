
# Docker

```
$ mvn clean install

$ docker build -t leedh/elastics-apm:v1.0 .
$ docker images
REPOSITORY           TAG                 IMAGE ID            CREATED             SIZE
leedh/elastics-apm   v1.0                 07f92f1d085c        57 seconds ago      131MB
openjdk              8-jdk-alpine        a3562aa0b991        21 months ago       105MB
$ docker push leedh/elastics-apm:v1.0

```

