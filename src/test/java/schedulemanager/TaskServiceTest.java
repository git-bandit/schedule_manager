package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.Priority;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskFolder;
import schedulemanager.domain.TaskStatus;
import schedulemanager.controller.ScheduleController;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskService / Task Management")
class TaskServiceTest extends BaseIntegrationTest {

    private ScheduleController controller;

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Nested
    @DisplayName("Validare task")
    class Validation {

        @Test
        @DisplayName("Aruncă excepție când titlul este null")
        void createTask_withNullTitle_throwsException() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Test Folder"));
            Task task = new Task(null, folder.getId(), Priority.MEDIUM);

            assertThrows(IllegalArgumentException.class, () -> controller.createTask(task));
        }

        @Test
        @DisplayName("Aruncă excepție când titlul este gol")
        void createTask_withEmptyTitle_throwsException() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Test Folder"));
            Task task = new Task("   ", folder.getId(), Priority.MEDIUM);

            assertThrows(IllegalArgumentException.class, () -> controller.createTask(task));
        }

        @Test
        @DisplayName("Aruncă excepție când folderId este null")
        void createTask_withNullFolderId_throwsException() {
            Task task = new Task("Test Task", null, Priority.MEDIUM);

            assertThrows(IllegalArgumentException.class, () -> controller.createTask(task));
        }
    }

    @Nested
    @DisplayName("CRUD operații")
    class CrudOperations {

        @Test
        @DisplayName("Creare task cu succes")
        void createTask_validTask_returnsCreatedTask() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Work"));
            Task task = new Task("Implement tests", folder.getId(), Priority.HIGH);
            task.setDescription("Write unit tests");

            Task created = controller.createTask(task);

            assertNotNull(created.getId());
            assertEquals("Implement tests", created.getTitle());
            assertEquals(folder.getId(), created.getFolderId());
            assertEquals(Priority.HIGH, created.getPriority());
            assertEquals(TaskStatus.TODO, created.getStatus());
        }

        @Test
        @DisplayName("Obține taskuri după folder")
        void getTasksByFolder_returnsTasksInFolder() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Projects"));
            controller.createTask(new Task("Task 1", folder.getId(), Priority.MEDIUM));
            controller.createTask(new Task("Task 2", folder.getId(), Priority.LOW));

            List<Task> tasks = controller.getTasksByFolder(folder.getId());

            assertEquals(2, tasks.size());
            assertTrue(tasks.stream().anyMatch(t -> "Task 1".equals(t.getTitle())));
            assertTrue(tasks.stream().anyMatch(t -> "Task 2".equals(t.getTitle())));
        }

        @Test
        @DisplayName("Actualizare task")
        void updateTask_modifiesTask() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Updates"));
            Task task = controller.createTask(new Task("Original", folder.getId(), Priority.MEDIUM));
            task.setTitle("Updated Title");
            task.setPriority(Priority.URGENT);

            controller.updateTask(task);

            Task updated = controller.getTasksByFolder(folder.getId()).get(0);
            assertEquals("Updated Title", updated.getTitle());
            assertEquals(Priority.URGENT, updated.getPriority());
        }

        @Test
        @DisplayName("Actualizare status task")
        void updateTaskStatus_changesStatus() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Status"));
            Task task = controller.createTask(new Task("Do Something", folder.getId(), Priority.MEDIUM));
            assertEquals(TaskStatus.TODO, task.getStatus());

            controller.updateTaskStatus(task.getId(), TaskStatus.DOING);

            List<Task> tasks = controller.getTasksByFolder(folder.getId());
            assertEquals(TaskStatus.DOING, tasks.get(0).getStatus());
        }

        @Test
        @DisplayName("Ștergere task")
        void deleteTask_removesTask() throws SQLException {
            TaskFolder folder = controller.createFolder(new TaskFolder("Delete"));
            Task task = controller.createTask(new Task("To Delete", folder.getId(), Priority.LOW));

            controller.deleteTask(task.getId());

            List<Task> tasks = controller.getTasksByFolder(folder.getId());
            assertTrue(tasks.isEmpty());
        }
    }
}
