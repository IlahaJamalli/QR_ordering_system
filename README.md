# QR Ordering System

A full-stack, QR-code–based restaurant ordering platform. Diners scan a table-specific QR code, explore the live menu, customise items, and place orders from their own device. Separate dashboards for kitchen staff, waiters, and managers provide real-time order management and menu editing.

---

## Key Features

- Contact-free ordering: guests scan, browse, customise, and submit orders.
- Role-based dashboards: Kitchen, Waiter, and Manager panels with granular access control.
- Live order lifecycle: statuses flow automatically from `NEW → PREPARING → READY → SERVED`.
- Dynamic menu management: managers create, update, delete, and reorder categories or items on the fly.
- Performance optimisations: Redis caching minimises menu-load latency; optional RabbitMQ decouples real-time notifications.
- Container-first workflow: Docker Compose spins up the entire stack with one command.
- Test-driven codebase: JUnit 5 and Spring Boot Test on the back-end, Vitest and React Testing Library on the front-end.
- CI-ready: repository structure and commands are compatible with GitHub Actions for automated testing and Docker image builds.

---

## Tech Stack

| Layer      | Technology                                                            |
| ---------- | --------------------------------------------------------------------- |
| Front-end  | React 18, Vite, React Router, Context API                             |
| Back-end   | Spring Boot 3, Spring Web, Spring Security, Spring Data JPA           |
| Database   | PostgreSQL (primary) · MongoDB (optional, NoSQL features)             |
| Cache      | Redis                                                                 |
| Messaging  | RabbitMQ (optional)                                                   |
| Container  | Docker, Docker Compose                                                |
| Build      | Maven or Gradle (back-end) · npm / Vite (front-end)                   |

---

## Project Structure

```

QR\_ordering\_system/
├── backend/            # Spring Boot application
│   ├── src/main/java/com/qrrestaurant/backend
│   ├── src/main/resources
│   └── Dockerfile
├── frontend/           # React + Vite SPA
│   ├── src/
│   ├── public/
│   └── Dockerfile
├── docker-compose.yml  # Local orchestration
└── README.md

````

---

## Prerequisites

| Tool                | Version (or newer) |
| ------------------- | ------------------ |
| Java                | 17                 |
| Node.js / npm       | 20                 |
| Docker & Compose    | 24                 |
| PostgreSQL          | 15                 |
| (Optional) Redis    | 7                  |
| (Optional) RabbitMQ | 3.13               |

---

## Quick Start

1. **Clone the repository**

   ```bash
   git clone https://github.com/IlahaJamalli/QR_ordering_system.git
   cd QR_ordering_system
````

2. **Create a `.env` file**

   ```env
   # PostgreSQL
   POSTGRES_USER=qradmin
   POSTGRES_PASSWORD=qrpass
   POSTGRES_DB=qr_ordering

   # Redis
   REDIS_HOST=redis
   REDIS_PORT=6379

   # RabbitMQ (optional)
   RABBITMQ_HOST=rabbit
   RABBITMQ_USER=guest
   RABBITMQ_PASSWORD=guest

   # JWT secret
   JWT_SECRET=change-me-in-production
   ```

3. **Start everything with Docker Compose**

   ```bash
   docker compose up --build
   ```

   * Front-end: [http://localhost:5173](http://localhost:5173)
   * Back-end API: [http://localhost:8080](http://localhost:8080)

*Prefer manual setup? See the section below.*

---

## Manual Setup (Without Docker)

### Back-end

```bash
cd backend
./mvnw spring-boot:run   # or: ./gradlew bootRun
```

### Front-end

```bash
cd frontend
npm install
npm run dev
```

The React development server runs on port 5173 by default.

---

## Testing

| Layer     | Command                           |
| --------- | --------------------------------- |
| Back-end  | `./mvnw test` or `./gradlew test` |
| Front-end | `npm run test`                    |

Continuous-integration pipelines should block merges on failing tests.

---

## Security

* Passwords are stored using BCrypt hashing.
* Stateless authentication with JSON Web Tokens (JWT).
  Clients send `Authorization: Bearer <token>` on each request.
* Role-based route protection configured in `SecurityConfig.java`.

---

## Data Persistence

Entity-relationship diagram (ERD) is located in `docs/ERD.png`. Core tables include:

| Table         | Purpose                  |
| ------------- | ------------------------ |
| `users`       | Staff and guest accounts |
| `menu_items`  | Menu catalogue           |
| `orders`      | Order header information |
| `order_items` | Individual line items    |

For local development, `data.sql` seeds sample data. In production, manage schema changes with Liquibase or Flyway.

---

## Build and Deployment

1. **Build images**

   ```bash
   docker compose build
   ```

2. **Push** the images to your container registry of choice.

3. **Deploy** with Docker Swarm, Kubernetes, or any compatible orchestrator.

---

## Contributing

1. Fork the repository and create a feature branch.
2. Follow Conventional Commits for messages.
3. Include unit or integration tests for all changes.
4. Open a pull request describing the enhancement or fix.

