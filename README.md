# Todo App

A reactive Spring Boot application for managing tasks with a RESTful API.

## Description

Todo App is a simple task management application built with Spring Boot and WebFlux. It provides a reactive API for streaming and paginating tasks. The application uses PostgreSQL for data persistence and Flyway for database migrations.

## Technologies Used

- Java 21
- Spring Boot 3.5.3
- Spring WebFlux (Reactive Web)
- Spring Data JPA
- PostgreSQL
- Flyway (Database Migrations)
- Lombok
- Docker & Docker Compose
- Gradle

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Gradle (or use the included Gradle wrapper)

## Setup and Installation

### 1. Clone the repository

```bash
git clone https://github.com/nzuwera/todo-app.git
cd todo-app
```

### 2. Start the PostgreSQL database

```bash
cd docker-compose
docker-compose -f postgres.yaml up -d
cd ..
```

This will start:
- PostgreSQL database on port 5432
- pgAdmin web interface on port 5050 (accessible at http://localhost:5050)

### 3. Build and run the application

Using Gradle wrapper:

```bash
./gradlew bootRun
```

Or using your installed Gradle:

```bash
gradle bootRun
```

The application will start on port 8080 by default.

## Configuration

The application uses environment variables for configuration. These are loaded from the `.env` file in the project root:

```properties
DATASOURCE_URL=jdbc:postgresql://localhost:5432/<DB_NAME>
DATASOURCE_USERNAME=<DB_APP_USERNAME>
DATASOURCE_PASSWORD=<DB_APP_PASSWORD>
```

Database credentials for Docker containers are in `docker-compose/.env`:

```properties
POSTGRES_USER=<DB_APP_USERNAME>
POSTGRES_PASSWORD=<DB_APP_PASSWORD>
POSTGRES_DB=<DB_NAME>
PGADMIN_DEFAULT_EMAIL=<PGADMIN_LOGIN_EMAIL>
PGADMIN_DEFAULT_PASSWORD=<PGADMIN_LOGIN_PASSWORD>
```

## Database Access

You can access the PostgreSQL database using pgAdmin:

1. Open http://localhost:5050 in your browser
2. Login with the credentials from `docker-compose/.env`:
   - Email: <PGADMIN_LOGIN_EMAIL>
   - Password: <PGADMIN_LOGIN_PASSWORD>
3. Add a new server with the following details:
   - Name: todo-app
   - Host: postgres
   - Port: 5432
   - Database: <DB_NAME>
   - Username: <DB_APP_USERNAME>
   - Password: <DB_APP_PASSWORD>

## Development

### Running Tests

```bash
./gradlew test
```

### Building the Application

```bash
./gradlew build
```

This will create a JAR file in the `build/libs` directory.

## License

This project is licensed under the MIT License.