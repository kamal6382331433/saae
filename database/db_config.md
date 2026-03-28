# SAAE Database Configuration
# ===========================

## Database Details
- **Database Engine**: MySQL 8.x
- **Database Name**: `saae_db`
- **Default Port**: `3306`

## Local Development Connection
```
Host:     localhost
Port:     3306
Database: saae_db
Username: root
Password: kamal123
```

## JDBC Connection URL
```
jdbc:mysql://localhost:3306/saae_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
```

## Hibernate Settings
| Property | Value |
|----------|-------|
| `ddl-auto` | `update` (auto-creates/updates tables) |
| `show-sql` | `true` |
| `dialect` | `org.hibernate.dialect.MySQLDialect` |

## Files in this folder
| File | Description |
|------|-------------|
| `schema.sql` | Complete database schema (tables, constraints, indexes) |
| `seed_data.sql` | Default users, sample subjects, students, and marks |
| `db_config.md` | This configuration reference file |

## Default Login Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Faculty | `faculty` | `faculty123` |
| Student | `student` | `student123` |

## Entity Relationship
```
users (1) -----> (1) students
subjects (1) ---> (N) marks
students (1) ---> (N) marks
```

## Notes
- Hibernate `ddl-auto=update` will automatically create tables on first run
- Passwords are stored using BCrypt hashing
- The `subjects` table has legacy columns (`name`, `code`, `credits`) for backward compatibility
