# GraphQL Java with a NoSQL database on Java EE 8

A simple and pure CRUD type REST API is sometimes not a good fit when you want to
avoid the dreaded N+1 call problem when querying related data. This showcase uses
GraphQL Java with MongoDB on Java EE 8.

## Usage

To run everything locally, you need to first compile and build the showcase image.
``` 
$ ./gradlew assemble
$ docker-compose up --build
```

First, try the different Hello requests provided. For example to issue a simple Hello GET
request, use the following URL:
```
$ http get localhost:8080/api/graphql?query={hello}
```

Once everything has started, use a REST client (e.g. Postman) to first insert some
sample data for Vehicle and Parts. Then you can issue more advanced queries like the
follwing GET request:
```
$ http get localhost:8080/api/graphql?query={vehicles{vin17,brand}}
$ http get localhost:8080/api/graphql?query={vehicles(vin17:"12345678901234567"){vin17,brand}}
$ http get localhost:8080/api/graphql?query={vehicles(vin17:"12345678901234567"){vin17,brand,baureihe,parts{name}}}
```

## Maintainer

M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

## License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
