# TaskFlow 🚀

A full-stack task management application built with Spring Boot and React TypeScript.


![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

## Screenshots

### Login
![Login](docs/screenshots/login.png)

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)

### Kanban Board
![Kanban](docs/screenshots/kanban.png)

## Features

- **JWT Authentication** — Secure register/login with BCrypt password hashing
- **Project Management** — Create, update, delete projects with member management
- **Kanban Board** — Move tasks between TODO / IN_PROGRESS / DONE columns
- **Role-based Access** — ADMIN and USER roles with protected endpoints
- **Pagination** — Paginated task listing with status filtering
- **Full Docker Support** — One command to run the entire stack
- **Unit Tests** — 12 tests with JUnit 5 and Mockito

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.5 | Framework |
| Spring Security | 6 | Authentication & Authorization |
| Spring Data JPA | 3.5 | ORM |
| PostgreSQL | 16 | Database |
| JWT (jjwt) | 0.12.3 | Token-based auth |
| Lombok | latest | Boilerplate reduction |
| JUnit 5 + Mockito | latest | Unit testing |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 18 | UI Framework |
| TypeScript | 5 | Type safety |
| Vite | 6 | Build tool |
| Tailwind CSS | 4 | Styling |
| shadcn/ui | latest | UI Components |
| Axios | latest | HTTP client |
| React Router | 6 | Client-side routing |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker | Containerization |
| Docker Compose | Local orchestration |
| GitHub Actions | CI/CD pipeline |
| nginx | Frontend serving |

## Architecture

**Backend** — Spring Boot layered architecture

- `controller/` — REST endpoints, request validation
- `service/` — Business logic, @Transactional
- `repository/` — JPA repositories, database queries
- `model/` — JPA entities (User, Project, Task)
- `dto/` — Request/Response objects
- `security/` — JWT filter chain, Spring Security config
- `exception/` — Global error handling with @RestControllerAdvice

**Frontend** — Feature-based architecture

- `features/auth/` — Login, Register pages and hooks
- `features/projects/` — Dashboard, Project management
- `features/tasks/` — Kanban board, task CRUD
- `shared/` — AuthContext, Axios instance, TypeScript types, PrivateRoute

## Getting Started

### Prerequisites

- Docker Desktop
- Git

### Run with Docker Compose

Clone the repository

    git clone https://github.com/amirm-code/taskflow.git
    cd taskflow

Copy environment variables

    cp .env.example .env

Edit .env with your values, then start the entire stack

    docker compose up --build

The app will be available at:

- **Frontend** → http://localhost:3000
- **Backend API** → http://localhost:8080
- **API Health** → http://localhost:8080/actuator/health

## API Endpoints

### Authentication

    POST /api/auth/register   → Create account
    POST /api/auth/login      → Get JWT token

### Projects

    GET    /api/projects                           → List my projects
    POST   /api/projects                           → Create project
    GET    /api/projects/{id}                      → Get project
    PUT    /api/projects/{id}                      → Update project
    DELETE /api/projects/{id}                      → Delete project
    POST   /api/projects/{id}/members/{userId}     → Add member

### Tasks

    GET    /api/projects/{id}/tasks                        → List tasks
    POST   /api/projects/{id}/tasks                        → Create task
    PUT    /api/projects/{id}/tasks/{taskId}               → Update task
    PATCH  /api/projects/{id}/tasks/{taskId}/status        → Update status
    DELETE /api/projects/{id}/tasks/{taskId}               → Delete task
    GET    /api/projects/{id}/tasks/paginated              → Paginated tasks

## Environment Variables

Copy `.env.example` and fill in your values:

    DB_NAME=taskflow
    DB_USER=your_db_user
    DB_PASSWORD=your_db_password
    JWT_SECRET=your_jwt_secret_base64

## Running Tests

    cd backend
    ./mvnw test

Result: Tests run: 12, Failures: 0, Errors: 0 — BUILD SUCCESS

## CI/CD

GitHub Actions pipeline runs on every push to main:

- ✅ Maven build and tests
- ✅ Docker image build
- ✅ Push to GitHub Container Registry (GHCR)

## License

MIT