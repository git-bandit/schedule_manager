package schedulemanager.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connection and initialization for MySQL.
 *
 * <p>Connection can be configured via system properties:
 * <ul>
 *   <li>db.host (default: localhost)</li>
 *   <li>db.port (default: 3306)</li>
 *   <li>db.name (default: schedule_manager)</li>
 *   <li>db.user (default: root)</li>
 *   <li>db.password (default: empty)</li>
 * </ul>
 */
public class DatabaseManager {
    private static final String DB_HOST = System.getProperty("db.host", "localhost");
    private static final String DB_PORT = System.getProperty("db.port", "3306");
    private static final String DB_NAME = System.getProperty("db.name", "schedule_manager");
    private static final String DB_USER = System.getProperty("db.user", "root");
    private static final String DB_PASSWORD = System.getProperty("db.password", "");

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static DatabaseManager instance;

    private DatabaseManager() {
        System.out.println("Using MySQL database: " + DB_NAME);
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Resets the singleton instance. For testing only.
     */
    public static synchronized void resetInstanceForTesting() {
        instance = null;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS task_folders (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    parent_folder_id BIGINT,
                    FOREIGN KEY (parent_folder_id) REFERENCES task_folders(id)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    folder_id BIGINT NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    color_tag VARCHAR(50),
                    priority VARCHAR(20) NOT NULL,
                    deadline DATE,
                    estimate_minutes INT,
                    description TEXT,
                    created_at DATETIME NOT NULL,
                    FOREIGN KEY (folder_id) REFERENCES task_folders(id),
                    CHECK (status IN ('TODO', 'DOING', 'DONE')),
                    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'))
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS today_tasks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    task_id BIGINT NOT NULL,
                    date DATE NOT NULL,
                    display_order INT,
                    FOREIGN KEY (task_id) REFERENCES tasks(id),
                    UNIQUE(task_id, date)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS plan_blocks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    date DATE NOT NULL,
                    start_time TIME NOT NULL,
                    end_time TIME NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    category VARCHAR(100),
                    linked_task_id BIGINT,
                    FOREIGN KEY (linked_task_id) REFERENCES tasks(id)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS actual_sessions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    date DATE NOT NULL,
                    start_time TIME NOT NULL,
                    end_time TIME NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    category VARCHAR(100),
                    linked_task_id BIGINT,
                    FOREIGN KEY (linked_task_id) REFERENCES tasks(id)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS daily_statistics (
                    date DATE PRIMARY KEY,
                    planned_minutes INT NOT NULL,
                    actual_minutes INT NOT NULL,
                    overlap_minutes INT NOT NULL,
                    quantitative_accuracy DOUBLE NOT NULL,
                    temporal_accuracy DOUBLE NOT NULL
                )
            """);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
