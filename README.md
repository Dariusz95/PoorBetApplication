# PoorBetApplication – Sports Betting Platform (Microservices)

https://poorbet.pl/app

## 📌 Overview
PoorBetApplication is a personal full-stack project designed as a **sports betting-style platform** built using a **microservices architecture**.  
The system consists of a backend implemented in **Java Spring Boot** and a frontend application built with **Angular**, communicating through an **API Gateway**.

The project focuses on backend architecture design, authentication mechanisms, and modern frontend development practices.

---

## 🧱 Architecture

The system is based on a microservices approach:

- **API Gateway** – single entry point for frontend communication
- **Auth Service** – user authentication and authorization (JWT-based)
- **Frontend Application** – Angular SPA
- **PostgreSQL** – relational database for persistence
- **Docker Compose** – containerized local environment

### High-level flow:
Frontend (Angular) → API Gateway → Microservices → PostgreSQL

---

## 🔐 Authentication & Security
- JWT-based authentication
- Spring Security for securing endpoints
- Role-based access control (RBAC)
- Stateless session management

---

## 🖥️ Frontend (Angular)
- Angular (latest version)
- Standalone Components
- Angular Signals
- RxJS for reactive programming
- Reactive Forms
- REST API integration
- Responsive Web Design (RWD)

---

## ⚙️ Backend (Spring Boot)
- Spring Boot microservices architecture
- Spring Security
- RESTful APIs
- PostgreSQL integration
- Separation of domain services

---

## 🐳 DevOps / Environment
- Dockerized services
- Docker Compose for local development
- Isolated services with container networking

---

## 🚀 How to run the project

### Prerequisites:
- Docker
- Docker Compose

### Run:
```bash
docker-compose up --build
