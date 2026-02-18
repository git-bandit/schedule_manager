package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskFolder;
import schedulemanager.domain.Priority;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Today List Management")
class TodayListTest extends BaseIntegrationTest {

    private ScheduleController controller;
    private static final LocalDate TODAY = LocalDate.of(2025, 2, 17);

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Test
    @DisplayName("Adaugă task la Today")
    void addTaskToToday_success() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("Work"));
        Task task = controller.createTask(new Task("Finish report", folder.getId(), Priority.HIGH));

        controller.addTaskToToday(task.getId(), TODAY);

        List<Task> todayTasks = controller.getTodayTasks(TODAY);
        assertEquals(1, todayTasks.size());
        assertEquals("Finish report", todayTasks.get(0).getTitle());
    }

    @Test
    @DisplayName("Aruncă excepție când task e deja în Today")
    void addTaskToToday_alreadyInToday_throwsException() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("Work"));
        Task task = controller.createTask(new Task("Task", folder.getId(), Priority.MEDIUM));
        controller.addTaskToToday(task.getId(), TODAY);

        assertThrows(IllegalStateException.class,
                () -> controller.addTaskToToday(task.getId(), TODAY));
    }

    @Test
    @DisplayName("Elimină task din Today")
    void removeTaskFromToday_success() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("Work"));
        Task task = controller.createTask(new Task("Remove me", folder.getId(), Priority.LOW));
        controller.addTaskToToday(task.getId(), TODAY);

        controller.removeTaskFromToday(task.getId(), TODAY);

        List<Task> todayTasks = controller.getTodayTasks(TODAY);
        assertTrue(todayTasks.isEmpty());
    }

    @Test
    @DisplayName("Today list gol pentru dată fără taskuri")
    void getTodayTasks_emptyDate_returnsEmptyList() throws SQLException {
        List<Task> todayTasks = controller.getTodayTasks(LocalDate.of(2020, 1, 1));
        assertTrue(todayTasks.isEmpty());
    }
}
