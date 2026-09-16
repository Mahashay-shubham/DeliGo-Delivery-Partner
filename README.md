# DeliGo

A full-stack delivery management platform with customer, delivery-partner, and administrator workflows.

## Features

- JWT authentication with BCrypt password hashing and role authorization
- Customer delivery creation, search, filters, profile management, and tracking history
- Delivery-partner queue and status progression
- Admin user-role management, delivery assignment, and operational counts
- Responsive React interface backed by Spring Boot and PostgreSQL

## Stack

- Java 21, Spring Boot, Spring Security, JPA/Hibernate
- PostgreSQL
- React, Vite, React Router

## Local setup

1. Create a PostgreSQL database named `deligo`.
2. Copy `Deligo-Backend/.env.example` to `Deligo-Backend/.env` and set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`.
3. Start the backend from `Deligo-Backend` with `mvn spring-boot:run`.
4. Start the frontend from `Deligo-Frontend` with `npm install` and `npm run dev`.

The frontend runs at `http://localhost:5173` and the API is served at `http://localhost:8080/api`.

To bootstrap an administrator, set `BOOTSTRAP_ADMIN_EMAIL` before registering that address.
