# Database Setup

## Default: SQLite (no setup required)

The app uses **SQLite by default**. A local file `schedule_manager.db` is created automatically in the working directory. No installation or configuration needed—just run the app.

## Optional: MySQL

To use MySQL instead, ensure MySQL 8.0+ is installed and running, then:

1. Create the database:
   ```sql
   CREATE DATABASE schedule_manager;
   ```

2. Run with MySQL:
   ```
   -Ddb.type=mysql
   ```

3. Optionally configure connection:
   ```
   -Ddb.host=localhost
   -Ddb.port=3306
   -Ddb.name=schedule_manager
   -Ddb.user=your_username
   -Ddb.password=your_password
   ```

Example:
```bash
mvn exec:java -Dexec.mainClass="schedulemanager.ui.MainWindow" -Ddb.type=mysql -Ddb.user=myuser -Ddb.password=mypass
```
