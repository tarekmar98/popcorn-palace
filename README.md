## Project Overview
This repository contains the implementation of **popcorn palace server**, a server designed to handle cinema tickets management.

### Features:
- Build movies with Its Info.
- Build showtimes for the movies.
- Build tickets for those showtimes.
- Tests for the API's.
- Dockerized infrastructure for local run with PostgreSQL.

## Instructions for running and testing the project

### Prerequisite
- Java SDK 21
- Java IDE
- Docker - make sure it works and you can init on it postgres.

### Configuration of the Terminal
- Make sure the terminal configured on java 21 to prevent issues.
- Make sure you are in the directory popcorn-palace.

### Configuration of intellij - if you want to run on intellij
- Set in the configuration java 21.
- Set in the project structure java 21.

### Database used
- **The Application** uses a PostgreSQL database, which is managed and executed using Docker Compose. The database configuration is defined within the 'compose.yml' file.<br><br>
- **The Tests** use a PostgreSQL database, which is managed and executed using Docker Compose. The database configuration is defined within the 'composeTest.yml' file.

### Run the App
#### Init the docker for the app:
- `docker-compose -f compose.yml up -d`

#### Run the app:
- `./mvnw spring-boot:run`

#### Terminate the app:
- Press 'ctrl + c' in the terminal then enter 'y'.

#### Terminate the docker and delete the data:
- `docker-compose -f compose.yml down --remove-orphans`

### Run the Tests
#### Init the docker for the tests:
- `docker-compose -f composeTest.yml up -d`

#### Run the tests:
- There is three classes for tests: **MovieTest**, **ShowtimeTest** and **TicketTest**.<br><br>
- Each one of them have a couple of tests, **to run all the tests of a specific class** you can use this command:<br>
`./mvnw -Dtest=<ClassName> test` - **replace `<ClassName>` with the name of the class you want to run.**<br><br>
- **To run a specific test** in one of the classes use the command:<br>
`./mvnw -Dtest=<ClassName>#<MethodName> test` - **replace `<ClassName>` and `<MethodName>` with the relevant class and method you want to run.**<br><br>
- **The steps of each test are written in the code before the test.**<br>
> ⚠️ **Warning:** Don't run a couple of classes at the same time, because this may cause an issue since they use the same data to manipulate the app and execute the tests.

#### Terminate the docker and delete the data:
- `docker-compose -f composeTest.yml down --remove-orphans`

### Documentation
- **For each class and function there is an explanation of the inputs, the returned value and the purpose of the function.**
