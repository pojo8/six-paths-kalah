# Kalah Inori Kagura

The project contains the java code, SQL scripts and a postman collection necessary for facilitating the game of "Six 'Paths' Kalah". 

## Pre-requisite Run locally

To run the application locally set the Spring profile to dev within the application.properties:

```spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}```

The dev Environment utilises an in memory H2 database. That offers a fast and low 


## Building locally

To build it locally run the command:

```mvn package -DskipTests```

This packages the project into a jar file that can be found at the following location of the project:

```target/kalah-0.0.1-SNAPSHOT.jar```

##Running Locally
To run the created Jar use the following command:

```java -jar target/kalah-0.0.1-SNAPSHOT.jar```

This will launch the jar provided the section related to locally testing. Once running the jar will expose run an api endpoint on the following at the following location:

```localhost:8080/api/```

You can test the api with the following endpoint used for Health checks:

```curl localhost:8080/api/health```

Alternatively the project can be run using the command

```mvn spring-boot:run```

# Project structure

## Model

The models relate to the data objects that map to the columns in the database.

Each variable to a column in the table and is also used to produce JSONs that are required using POST and PUT.

## Repository

These classes form the methods that are tied to the SQL functions executed on the table.
It utilises native queries in creating the SQL statements. The methods in this class are using in froming the REST controllers

## Controller

These form the exposed REST endpoints that are exposed in running the application. The annotated Mappings define if it a GET, POST or PUSH request.

## Resources

The Sql scripts can be found in the following folders:

src/main/resources/sql/*

For examples of the http rest call lok in the following folder:

src/main/resources/http/*


