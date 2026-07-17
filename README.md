# Job Application Tracker

A web application for tracking your job search — record the companies you apply to,
the roles, and where each application stands in the pipeline. Built with **Spring Boot**,
**Thymeleaf**, and **PostgreSQL**, with form-based authentication and role-based access control.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%2B-blue)
![Build](https://img.shields.io/badge/build-Maven-red)

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Screenshots](#screenshots)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Running the app](#running-the-app)
- [Creating the first admin](#creating-the-first-admin)
- [Running the tests](#running-the-tests)
- [Project structure](#project-structure)

---

## Features

- **User accounts** — sign up and sign in with form-based authentication (Spring Security), passwords hashed with BCrypt.
- **Job application tracking** — create, edit, and delete applications, each capturing the company, job title, and current status.
- **Application statuses** — track each application through `APPLIED`, `INTERVIEW`, `OFFER`, and `REJECTED`.
- **Per-user data isolation** — every user only sees and manages their own applications; ownership is enforced at the data-access layer.
- **Company reuse** — companies are matched case-insensitively and reused across applications instead of being duplicated.
- **Role-based access control** — two roles (`USER`, `ADMIN`). Admin-only pages are protected both in the security filter chain and in the UI.
- **Admin user management** — admins can view all users and **promote another user to admin**.
- **Server-side validation** — form input is validated with Jakarta Bean Validation, with inline error messages.
- **CSRF protection** — enabled by default on all state-changing requests.

## Tech stack

| Layer            | Technology                                              |
|------------------|---------------------------------------------------------|
| Language         | Java 17                                                 |
| Framework        | Spring Boot 4.0 (Web MVC, Security, Data JPA, Validation) |
| View             | Thymeleaf + Bootstrap 5                                 |
| Persistence      | Spring Data JPA / Hibernate                             |
| Database         | PostgreSQL                                              |
| Security         | Spring Security + `thymeleaf-extras-springsecurity6`    |
| Build            | Maven (via the included Maven Wrapper)                  |
| Boilerplate      | Lombok                                                  |
| Testing          | JUnit 5, Mockito, AssertJ                               |

## Screenshots

> _Replace the placeholders below with real screenshots (e.g. add PNGs under `docs/screenshots/`)._

| Sign in | Applications list |
|---------|-------------------|
| ![Sign in](docs/screenshots/login.png) | ![Applications](docs/screenshots/applications.png) |

| Add / edit application | Users (admin) |
|------------------------|---------------|
| ![Add application](docs/screenshots/add-application.png) | ![Users](docs/screenshots/users-admin.png) |

## Getting started

### Prerequisites

- **JDK 17** (or newer)
- **PostgreSQL** running locally
- No local Maven install needed — the project ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`)

### 1. Clone

```bash
git clone https://github.com/danil-1007/JobApplicationTracker-demo.git
cd JobApplicationTracker-demo
```

### 2. Create the database

```sql
CREATE DATABASE job;
```

Tables are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`).

## Configuration

Application config lives in `src/main/resources/application.yaml`, which is **git-ignored** so
credentials never land in version control. Copy the provided template and fill in your values:

```bash
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

```yaml
server:
  port: 1703

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/job
    username: postgres
    password: YOUR_PASSWORD_HERE
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## Running the app

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The app starts on **http://localhost:1703**. Open it and sign up to create your first account.

To build a runnable JAR instead:

```bash
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Creating the first admin

New sign-ups are created with the `USER` role. Because there is no admin yet to promote anyone,
the **first** admin is set directly in the database; from then on, that admin can promote others
from the **Users** page in the UI.

```sql
-- role is stored as an ordinal: 0 = USER, 1 = ADMIN
UPDATE users SET role = 1 WHERE email = 'your-email@example.com';
```

Log out and back in for the new authority to take effect. The **Users** link then appears in the
navbar, where each non-admin has a **Make admin** action.

## Running the tests

```bash
./mvnw test
```

The suite covers the service layer (application CRUD, per-user isolation, company reuse,
user sign-up, and admin promotion) with JUnit 5, Mockito, and AssertJ.

## Project structure

```
src/main/java/JobApplicationTracker/demo
├── config/         # Spring Security configuration
├── controllers/    # MVC controllers (auth, applications, users)
├── entity/         # JPA entities (User, JobApplication, Company, enums)
├── exception/      # Application exceptions (e.g. ResourceNotFoundException -> 404)
├── forms/          # Form-backing / validation objects
├── repos/          # Spring Data JPA repositories
└── services/       # Business logic (ApplicationService, UserService, ...)

src/main/resources
├── templates/      # Thymeleaf views (+ shared layout fragment)
└── application.yaml.example
```
