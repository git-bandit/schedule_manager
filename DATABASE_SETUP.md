# MySQL Database Setup

The app uses **MySQL only**. Ensure MySQL 8.0+ is installed and running.

## 1. Create the database

```sql
CREATE DATABASE schedule_manager;
```

## 2. Run the application

The app connects to `localhost:3306` by default. Configure if needed via system properties:

| Property | Default |
|----------|---------|
| db.host | localhost |
| db.port | 3306 |
| db.name | schedule_manager |
| db.user | root |
| db.password | (empty) |

Example with password:
```bash
mvn exec:java -Dexec.mainClass="schedulemanager.ui.MainWindow" -Ddb.password=yourpass
```

## 3. For running tests

Create the test database:
```sql
CREATE DATABASE schedule_manager_test;
```

Tests use `schedule_manager_test` to keep test data separate.
