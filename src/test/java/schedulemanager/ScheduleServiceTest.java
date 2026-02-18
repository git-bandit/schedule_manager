package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.PlanBlock;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Schedule Service - Plan Blocks")
class ScheduleServiceTest extends BaseIntegrationTest {

    private ScheduleController controller;
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 2, 17);

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Test
    @DisplayName("Creare plan block valid")
    void createPlanBlock_validBlock_success() throws SQLException {
        PlanBlock block = new PlanBlock(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");

        PlanBlock created = controller.createPlanBlock(block);

        assertNotNull(created.getId());
        assertEquals("Meeting", created.getTitle());
        assertEquals(60, created.getDurationMinutes());

        List<PlanBlock> blocks = controller.getPlanBlocks(TEST_DATE);
        assertEquals(1, blocks.size());
    }

    @Test
    @DisplayName("Aruncă excepție pentru end time înainte de start time")
    void createPlanBlock_invalidTimeRange_throwsException() {
        PlanBlock block = new PlanBlock(TEST_DATE,
                LocalTime.of(10, 0), LocalTime.of(9, 0), "Invalid");

        assertThrows(IllegalArgumentException.class, () -> controller.createPlanBlock(block));
    }

    @Test
    @DisplayName("Aruncă excepție pentru titlu gol")
    void createPlanBlock_emptyTitle_throwsException() {
        PlanBlock block = new PlanBlock(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "   ");

        assertThrows(IllegalArgumentException.class, () -> controller.createPlanBlock(block));
    }

    @Test
    @DisplayName("Aruncă excepție pentru blocuri suprapuse")
    void createPlanBlock_overlappingBlocks_throwsException() throws SQLException {
        controller.createPlanBlock(new PlanBlock(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "First"));

        PlanBlock overlapping = new PlanBlock(TEST_DATE,
                LocalTime.of(9, 30), LocalTime.of(10, 30), "Overlap");

        assertThrows(IllegalArgumentException.class, () -> controller.createPlanBlock(overlapping));
    }

    @Test
    @DisplayName("Permite blocuri adiacente (fără suprapunere)")
    void createPlanBlock_adjacentBlocks_success() throws SQLException {
        controller.createPlanBlock(new PlanBlock(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "First"));
        PlanBlock second = controller.createPlanBlock(new PlanBlock(TEST_DATE,
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Second"));

        assertNotNull(second.getId());
        List<PlanBlock> blocks = controller.getPlanBlocks(TEST_DATE);
        assertEquals(2, blocks.size());
    }

    @Test
    @DisplayName("Șterge plan block")
    void deletePlanBlock_removesBlock() throws SQLException {
        PlanBlock block = controller.createPlanBlock(new PlanBlock(TEST_DATE,
                LocalTime.of(14, 0), LocalTime.of(15, 0), "To Delete"));

        controller.deletePlanBlock(block.getId());

        List<PlanBlock> blocks = controller.getPlanBlocks(TEST_DATE);
        assertTrue(blocks.isEmpty());
    }
}
