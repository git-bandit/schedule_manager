package schedulemanager.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connection and initialization.
 * 
 * <p>Supports SQLite (default) and MySQL. Use system property {@code db.type}
 * to choose: "mysql" (default) or "sqlite".
 * 
 * <p>SQLite requires no setup - a local file is created automatically.
 * 
 * <p>For MySQL, connection can be configured via:
 * <ul>
 *   <li>db.host (default: localhost)</li>
 *   <li>db.port (default: 3306)</li>
 *   <li>db.name (default: schedule_manager)</li>
 *   <li>db.user (default: root)</li>
 *   <li>db.password (default: empty)</li>
 * </ul>
 */
public class DatabaseManager {
    private static final String DB_TYPE = System.getProperty("db.type", "mysql").toLowerCase();
    
    // MySQL config
    private static final String DB_HOST = System.getProperty("db.host", "localhost");
    private static final String DB_PORT = System.getProperty("db.port", "3306");
    private static final String DB_NAME = System.getProperty("db.name", "schedule_manager");
    private static final String DB_USER = System.getProperty("db.user", "root");
    private static final String DB_PASSWORD = System.getProperty("db.password", "");
    
    private static final String SQLITE_URL = "jdbc:sqlite:schedule_manager.db";
    private static final String MYSQL_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    private static DatabaseManager instance;
    private final String connectionUrl;
    private final String dbUser;
    private final String dbPassword;
    private final boolean isSqlite;
    
    /**
     * Private constructor for singleton pattern.
     */
    private DatabaseManager() {
        if ("mysql".equals(DB_TYPE)) {
            this.connectionUrl = MYSQL_URL;
            this.dbUser = DB_USER;
            this.dbPassword = DB_PASSWORD;
            this.isSqlite = false;
            System.out.println("Using MySQL database: " + DB_NAME);
        } else {
            this.connectionUrl = SQLITE_URL;
            this.dbUser = null;
            this.dbPassword = null;
            this.isSqlite = true;
            System.out.println("Using SQLite database: schedule_manager.db");
        }
        initializeDatabase();
    }
    
    /**
     * Gets the singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Gets a database connection.
     */
    public Connection getConnection() throws SQLException {
        if (isSqlite) {
            return DriverManager.getConnection(connectionUrl);
        } else {
            return DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
        }
    }
    
    /**
     * Returns true if using SQLite (for any SQLite-specific handling).
     */
    public boolean isSqlite() {
        return isSqlite;
    }
    
    /**
     * Initializes the database schema.
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            if (isSqlite) {
                executeSqliteSchema(stmt);
            } else {
                executeMysqlSchema(stmt);
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void executeSqliteSchema(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS task_folders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                parent_folder_id INTEGER,
                FOREIGN KEY (parent_folder_id) REFERENCES task_folders(id)
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                folder_id INTEGER NOT NULL,
                status TEXT NOT NULL CHECK(status IN ('TODO', 'DOING', 'DONE')),
                color_tag TEXT,
                priority TEXT NOT NULL CHECK(priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
                deadline TEXT,
                estimate_minutes INTEGER,
                description TEXT,
                created_at TEXT NOT NULL,
                FOREIGN KEY (folder_id) REFERENCES task_folders(id)
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS today_tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                task_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                display_order INTEGER,
                FOREIGN KEY (task_id) REFERENCES tasks(id),
                UNIQUE(task_id, date)
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS plan_blocks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                title TEXT NOT NULL,
                category TEXT,
                linked_task_id INTEGER,
                FOREIGN KEY (linked_task_id) REFERENCES tasks(id)
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS actual_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                title TEXT NOT NULL,
                category TEXT,
                linked_task_id INTEGER,
                FOREIGN KEY (linked_task_id) REFERENCES tasks(id)
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS daily_statistics (
                date TEXT PRIMARY KEY,
                planned_minutes INTEGER NOT NULL,
                actual_minutes INTEGER NOT NULL,
                overlap_minutes INTEGER NOT NULL,
                quantitative_accuracy REAL NOT NULL,
                temporal_accuracy REAL NOT NULL
            )
        """);
    }
    
    private void executeMysqlSchema(Statement stmt) throws SQLException {
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
    }
}
