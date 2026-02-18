package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.ActualSession;
import schedulemanager.domain.DailyStatistics;
import schedulemanager.domain.PlanBlock;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskFolder;
import schedulemanager.domain.Priority;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Statistics Service")
class StatsServiceTest extends BaseIntegrationTest {

    private ScheduleController controller;
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 2, 17);

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Test
    @DisplayName("Statistici zilnice - zilnic fără date")
    void computeDailyStats_emptyDay_returnsZeroStats() throws SQLException {
        DailyStatistics stats = controller.getDailyStats(TEST_DATE);

        assertEquals(TEST_DATE, stats.getDate());
        assertEquals(0, stats.getPlannedMinutes());
        assertEquals(0, stats.getActualMinutes());
        assertEquals(0, stats.getOverlapMinutes());
        assertEquals(0.0, stats.getQuantitativeAccuracy());
        assertEquals(0.0, stats.getTemporalAccuracy());
    }

    @Test
    @DisplayName("Statistici zilnice - plan și actual cu suprapunere")
    void computeDailyStats_withOverlap_calculatesCorrectly() throws SQLException {
        // Plan: 9:00-11:00 (120 min)
        controller.createPlanBlock(new PlanBlock(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(11, 0), "Planned Work"));

        // Actual: 9:30-10:30 (60 min), overlap 9:30-10:30 = 60 min
        controller.createSession(new ActualSession(TEST_DATE,
                LocalTime.of(9, 30), LocalTime.of(10, 30), "Actual Work"));

        DailyStatistics stats = controller.getDailyStats(TEST_DATE);

        assertEquals(120, stats.getPlannedMinutes());
        assertEquals(60, stats.getActualMinutes());
        assertEquals(60, stats.getOverlapMinutes());
        assertEquals(0.5, stats.getQuantitativeAccuracy(), 0.001);  // min(60,120)/120
        assertEquals(0.5, stats.getTemporalAccuracy(), 0.001);      // 60/120
    }

    @Test
    @DisplayName("Task stats - plan și actual legate de task")
    void computeTaskStats_withLinkedTask() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("Projects"));
        Task task = controller.createTask(new Task("Coding", folder.getId(), Priority.HIGH));

        PlanBlock block = new PlanBlock(TEST_DATE,
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Code");
        block.setLinkedTaskId(task.getId());
        controller.createPlanBlock(block);

        ActualSession session = new ActualSession(TEST_DATE,
                LocalTime.of(10, 30), LocalTime.of(11, 30), "Coded");
        session.setLinkedTaskId(task.getId());
        controller.createSession(session);

        Map<Long, schedulemanager.service.StatsService.TaskStats> taskStats =
                controller.getTaskStats(TEST_DATE);

        assertTrue(taskStats.containsKey(task.getId()));
        schedulemanager.service.StatsService.TaskStats stats = taskStats.get(task.getId());
        assertEquals(120, stats.plannedMinutes);
        assertEquals(60, stats.actualMinutes);
        assertEquals(60, stats.overlapMinutes);  // 10:30-11:30 overlap
    }
}
