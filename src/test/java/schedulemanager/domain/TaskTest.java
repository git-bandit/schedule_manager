package schedulemanager.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Domain")
class TaskTest {

    @Test
    @DisplayName("Constructor seteză status și priority implicite")
    void defaultConstructor_setsDefaults() {
        Task task = new Task();

        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    @DisplayName("Constructor cu parametri")
    void constructor_withParams_setsFields() {
        Task task = new Task("Implement feature", 1L, Priority.HIGH);

        assertEquals("Implement feature", task.getTitle());
        assertEquals(1L, task.getFolderId());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(TaskStatus.TODO, task.getStatus());
    }
}
