# HuskyPlan

HuskyPlan is a full-stack course planning application for building multi-quarter University of Washington course schedules and checking prerequisite eligibility.

The project was built with React, TypeScript, Spring Boot, PostgreSQL, and Docker.

## Features

- Search a course catalog by course code or title
- View course details including credits, prerequisites, and descriptions
- Track completed coursework
- Check whether prerequisite requirements are satisfied
- Build a multi-quarter course plan
- Allow courses from earlier planned quarters to satisfy later prerequisites
- Prevent courses in future quarters from satisfying earlier prerequisites
- Block invalid course additions when prerequisites are missing
- Prevent duplicate courses from being added to the plan
- Persist saved schedules in PostgreSQL
- Run the frontend, backend, and database using Docker Compose
- Run automated backend integration tests during backend builds

## Tech Stack

### Frontend

- React
- TypeScript
- Vite
- CSS

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation

### Database

- PostgreSQL
- H2 for automated tests

### Development / Infrastructure

- Docker
- Docker Compose
- Maven
- Nginx

## Architecture

HuskyPlan uses a standard three-tier architecture:

```text
React + TypeScript frontend
        |
        | REST API
        v
Spring Boot backend
        |
        | JPA / Hibernate
        v
PostgreSQL
```

The frontend runs through Nginx in Docker.

The backend exposes REST endpoints for:

- course search
- course details
- prerequisite checking
- saved-plan management
- plan validation
- add-course validation

## Prerequisite Validation

Prerequisites are represented as requirement groups.

For example:

```text
(CSE 123 OR CSE 143)
AND
(MATH 126 OR MATH 135)
```

A course is eligible only when at least one course from every prerequisite group has been satisfied.

HuskyPlan also validates prerequisites across planned quarters.

For example:

```text
Autumn 2026
CSE 311

Winter 2027
CSE 332
```

CSE 311 can satisfy the prerequisite for CSE 332 because it appears in an earlier quarter.

This ordering is invalid:

```text
Autumn 2026
CSE 332

Winter 2027
CSE 311
```

A future course cannot satisfy a prerequisite for an earlier course.

Courses planned in the same quarter also do not satisfy each other's prerequisites.

## Automated Tests

The backend includes integration tests using Spring Boot, MockMvc, JUnit, and H2.

Current tests verify:

- CSE 332 is blocked without CSE 311
- CSE 311 is allowed when its prerequisite groups are satisfied
- an earlier planned course can satisfy a later prerequisite
- a future course cannot satisfy an earlier prerequisite
- duplicate courses are rejected

Backend tests run automatically during the Docker backend build.

If a test fails, the backend image will not build.

## Running Locally

### Requirements

Install:

- Docker Desktop
- Git

### Start the application

From the project root:

```bash
docker compose up -d
```

Then open:

```text
http://localhost:5173
```

### Rebuild after code changes

```bash
docker compose up --build -d
```

### Run backend tests

```bash
docker compose build backend
```

## Services

The Docker Compose environment contains:

```text
Frontend:   localhost:5173
Backend:    localhost:8080
PostgreSQL: localhost:5432
```

## Project Structure

```text
huskyplan/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/huskyplan/
│   │   └── test/
│   │       └── java/com/huskyplan/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   └── package.json
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

## Course Data

The current course catalog is demo data modeled around University of Washington Computer Science courses.

HuskyPlan is not an official UW advising tool, and prerequisite information should be verified against official university sources before making academic decisions.

## Future Improvements

Potential future improvements include:

- user accounts and authentication
- per-user saved schedules
- importing completed courses from transcripts
- broader UW course coverage
- degree-requirement tracking
- schedule conflict detection
- production deployment
- CI/CD testing

## Author

Ethan Chiu  
Computer Science, University of Washington