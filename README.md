# Task Management System (TMS) - Spring Boot Backend

A production-ready Java Spring Boot backend for the Task Management System (TMS) full-stack application, connected to PostgreSQL.

---

## 🚀 Features

- **Java 21 & Spring Boot 3.3.4** RESTful API.
- **Spring Data JPA & Hibernate 6** for PostgreSQL object-relational mapping.
- **PostgreSQL Connection**: Configured for Neon cloud PostgreSQL (or local PostgreSQL via Spring profiles).
- **Node.js Password Interoperability**: Implements scrypt hash verification with Bouncy Castle (`scrypt$<salt>$<hash>`), allowing existing user accounts (e.g. `admin@gmail.com` with password `admin123`) to log in immediately without resetting passwords.
- **Full REST API Implementation**:
  - Authentication (`/api/login`, `/api/logout`)
  - User Management (`/api/users`, `/api/users/{id}`)
  - Tasks (`/api/tasks`, `/api/tasks/{id}`) with filtering by creator (`userId`) and assignee (`assignedTo`)
  - Task Comments (`/api/tasks/{taskId}/comments`)
  - Admin Customization: Categories & Labels (`/api/categories`, `/api/labels`)
- **CORS Configured**: Pre-configured to allow requests from Next.js frontend (`http://localhost:3000`).
- **Maven Wrapper Included**: Run immediately using `./mvnw` or `mvnw.cmd` without needing Maven pre-installed.

---

## 📁 Project Structure

```text
backend/
├── pom.xml                                      # Maven project configuration & dependencies
├── mvnw / mvnw.cmd                              # Maven wrapper scripts (Windows & Linux/macOS)
├── .mvn/wrapper/maven-wrapper.properties        # Wrapper configuration
└── src/
    ├── main/
    │   ├── java/com/tms/
    │   │   ├── TmsBackendApplication.java       # Spring Boot main entry point
    │   │   ├── config/
    │   │   │   └── CorsConfig.java              # CORS filter & WebMvc config for port 3000
    │   │   ├── controller/
    │   │   │   ├── AuthController.java          # POST /api/login, POST /api/logout
    │   │   │   ├── UserController.java          # GET, POST, PATCH, DELETE /api/users
    │   │   │   ├── TaskController.java          # GET, POST, PUT, PATCH, DELETE /api/tasks
    │   │   │   ├── CommentController.java       # GET, POST /api/tasks/{taskId}/comments
    │   │   │   └── MetadataController.java      # Categories & Labels endpoints
    │   │   ├── dto/                             # Request & Response DTOs
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── RegisterRequest.java
    │   │   │   ├── UserDto.java
    │   │   │   ├── UserResponseWrapper.java
    │   │   │   ├── RoleUpdateRequest.java
    │   │   │   ├── TaskRequest.java
    │   │   │   ├── TaskResponse.java
    │   │   │   ├── CommentDto.java
    │   │   │   ├── CommentRequest.java
    │   │   │   ├── CategoriesRequest.java
    │   │   │   └── LabelsRequest.java
    │   │   ├── entity/                          # JPA Entities mapped to PostgreSQL
    │   │   │   ├── User.java                    # tms_users table
    │   │   │   ├── Task.java                    # tms_tasks table (supports text[] attachments)
    │   │   │   ├── Comment.java                 # tms_comments table
    │   │   │   ├── Category.java                # tms_categories table
    │   │   │   └── Label.java                   # tms_labels table
    │   │   ├── repository/                      # Spring Data JPA Repositories
    │   │   │   ├── UserRepository.java
    │   │   │   ├── TaskRepository.java
    │   │   │   ├── CommentRepository.java
    │   │   │   ├── CategoryRepository.java
    │   │   │   └── LabelRepository.java
    │   │   ├── service/                         # Business Logic Layer
    │   │   │   ├── UserService.java
    │   │   │   ├── TaskService.java
    │   │   │   ├── CommentService.java
    │   │   │   └── MetadataService.java
    │   │   └── util/
    │   │       └── PasswordUtil.java            # Node.js compatible scrypt verification & hashing
    │   └── resources/
    │       ├── application.properties           # Neon cloud PostgreSQL configuration
    │       ├── application-local.properties     # Optional local PostgreSQL configuration
    │       └── schema.sql                       # Database DDL script
    └── test/java/com/tms/
        ├── TmsBackendApplicationTests.java      # Spring Boot context load test
        └── PasswordUtilTest.java                # Scrypt verification unit test
```

---

## ⚙️ Running the Backend

### Option 1: Command Line (Windows PowerShell / CMD)

Navigate into the `backend` folder and run:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The server will start on **`http://localhost:8080`**.

### Option 2: Running with an IDE

1. **Spring Tool Suite (STS) / Eclipse**:
   - Open STS -> `File` -> `Import...` -> `Existing Maven Projects`.
   - Browse to the `backend` folder.
   - Right-click `TmsBackendApplication.java` -> `Run As` -> `Spring Boot App`.

2. **IntelliJ IDEA**:
   - Open IntelliJ -> `Open` -> select `backend/pom.xml`.
   - Click the green Run button next to `TmsBackendApplication`.

3. **VS Code**:
   - Install "Extension Pack for Java" and "Spring Boot Extension Pack".
   - Open the `backend` folder and press `F5` or click "Run" on `TmsBackendApplication.java`.

---

## 🗄️ Database Configuration

By default, `application.properties` connects to the existing Neon PostgreSQL database:

```properties
spring.datasource.url=jdbc:postgresql://ep-purple-sound-azz90ykn-pooler.c-3.ap-southeast-1.aws.neon.tech:5432/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_RKasm63cvzJW
```

To run with a **local PostgreSQL database**:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```
Or edit `application-local.properties` with your local PostgreSQL user and password.

---

## 🔄 Communicating with the Frontend

1. **Next.js Rewrites**:
   The frontend `next.config.ts` includes a proxy rewrite:
   ```typescript
   async rewrites() {
     return [
       {
         source: "/api/:path*",
         destination: "http://localhost:8080/api/:path*",
       },
     ];
   }
   ```
   To route all frontend calls directly to Spring Boot, rename `app/api` to `app/api.backup`. Next.js will automatically proxy all `/api/*` requests to `http://localhost:8080/api/*`.

2. **Direct CORS Calls**:
   `CorsConfig.java` enables cross-origin requests from `http://localhost:3000` (and `http://127.0.0.1:3000`) with credentials, so the frontend can also call `http://localhost:8080/api/...` directly.

---

## 📡 API Reference

| Endpoint | Method | Description |
|---|---|---|
| `/api/login` | `POST` | Authenticate user (`{ email, password }`) |
| `/api/logout` | `POST` | Logout session |
| `/api/users` | `GET` | List all registered users (excluding passwords) |
| `/api/users` | `POST` | Register a new user (`{ name, email, password }`) |
| `/api/users/{id}` | `PATCH` | Update user role (`{ role: "manager" }`) |
| `/api/users/{id}` | `DELETE` | Delete user and cascade their created tasks |
| `/api/tasks` | `GET` | List tasks (supports `?userId=&assignedTo=`) |
| `/api/tasks/{id}` | `GET` | Get single task with its comments |
| `/api/tasks` | `POST` | Create a new task |
| `/api/tasks` | `PATCH` | Update task with completion/status sync |
| `/api/tasks` | `PUT` | Full update of a task |
| `/api/tasks/{id}` | `PUT` | Update task status or fields by ID |
| `/api/tasks?id={id}` | `DELETE` | Delete task by query param |
| `/api/tasks/{id}` | `DELETE` | Delete task by path param |
| `/api/tasks/{taskId}/comments` | `GET` | List comments for a task |
| `/api/tasks/{taskId}/comments` | `POST` | Add comment (`{ userId, message }`) |
| `/api/categories` | `GET` | Get all task categories |
| `/api/categories` | `POST` | Replace task categories (`{ categories: [...] }`) |
| `/api/labels` | `GET` | Get all task labels |
| `/api/labels` | `POST` | Replace task labels (`{ labels: [...] }`) |

