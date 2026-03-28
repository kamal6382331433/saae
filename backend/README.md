# SAAE Backend

## Overview
Spring Boot REST API backend for the **Students Academic Analysis Engine** (SAAE).  
Provides authentication, CRUD operations, and analytics endpoints.

## Tech Stack
- **Java 17** with **Spring Boot 3.2.2**
- **Spring Data JPA** (Hibernate ORM)
- **Spring Security** (BCrypt password hashing)
- **MySQL** (via `mysql-connector-j`)
- **Thymeleaf** (for setup page)

## File Structure
```
backend/
├── pom.xml                                 # Maven build config
└── src/main/
    ├── java/com/saae/
    │   ├── SaaeApplication.java            # Spring Boot entry point
    │   ├── config/
    │   │   ├── DataInitializer.java        # Default data seeder
    │   │   ├── GlobalExceptionHandler.java # Error handling
    │   │   └── SecurityConfig.java         # Security configuration
    │   ├── controller/
    │   │   ├── AcademicController.java     # Academic operations
    │   │   ├── AdminController.java        # Admin CRUD endpoints
    │   │   ├── AnalyticsController.java    # Analytics & reports
    │   │   ├── AuthController.java         # Login/auth endpoints
    │   │   ├── DatabaseSetupController.java# DB setup utilities
    │   │   ├── FacultyController.java      # Faculty operations
    │   │   ├── SearchController.java       # Advanced search
    │   │   ├── SetupController.java        # Setup page controller
    │   │   └── StudentController.java      # Student operations
    │   ├── model/
    │   │   ├── Mark.java                   # Marks entity
    │   │   ├── Student.java                # Student entity
    │   │   ├── Subject.java                # Subject entity
    │   │   └── User.java                   # User entity
    │   ├── repository/
    │   │   ├── MarkRepository.java         # Marks data access
    │   │   ├── StudentRepository.java      # Student data access
    │   │   ├── SubjectRepository.java      # Subject data access
    │   │   └── UserRepository.java         # User data access
    │   └── service/
    │       ├── AcademicService.java        # Academic business logic
    │       ├── AdminService.java           # Admin business logic
    │       ├── AnalyticsService.java       # Analytics computations
    │       ├── FacultyService.java         # Faculty business logic
    │       ├── SearchService.java          # Search implementation
    │       ├── StudentService.java         # Student business logic
    │       └── UserService.java            # User management
    └── resources/
        ├── application.properties          # App configuration
        └── templates/
            └── setup.html                  # Database setup page
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/students` | List all students |
| POST | `/api/admin/students` | Add student |
| PUT | `/api/admin/students/{id}` | Update student |
| DELETE | `/api/admin/students/{id}` | Delete student |
| GET | `/api/admin/faculty` | List all faculty |
| POST | `/api/admin/faculty` | Add faculty |
| PUT | `/api/admin/users/{id}` | Update user |
| DELETE | `/api/admin/users/{id}` | Delete user |
| GET | `/api/admin/subjects` | List all subjects |
| POST | `/api/admin/subjects` | Add subject |

### Faculty
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/faculty/students` | List students |
| POST | `/api/faculty/marks` | Submit marks |

### Student
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/student/profile/{userId}` | Get student profile |
| GET | `/api/student/results/{studentId}` | Get student results |

### Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/top-performers` | Top performing students |
| GET | `/api/analytics/risk-students` | At-risk students |
| GET | `/api/analytics/department-performance` | Department stats |
| GET | `/api/analytics/weak-subjects` | Weak subjects |
| GET | `/api/analytics/performance-trend/{id}` | Performance trend |

### Search
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/search/students` | Advanced student search |

## How to Run
```bash
# Navigate to backend folder
cd backend

# Build and run with Maven
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/saae-0.0.1-SNAPSHOT.jar
```

The server starts on **port 8081** by default.

## Configuration
Edit `src/main/resources/application.properties` to change:
- Database connection URL, username, password
- Server port
- Hibernate settings
