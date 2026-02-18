package schedulemanager;

import schedulemanager.repository.DatabaseManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class for integration tests. Uses MySQL database {@code schedule_manager_test}.
 * Create the database before running tests: {@code CREATE DATABASE schedule_manager_test;}
 */
abstract class BaseIntegrationTest {

    @BeforeAll
    static void setupDatabase() {
        System.setProperty("db.name", "schedule_manager_test");
        DatabaseManager.resetInstanceForTesting();
    }

    @AfterAll
    static void teardownDatabase() {
        DatabaseManager.resetInstanceForTesting();
    }
}
