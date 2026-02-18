package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.ActualSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tracking Service - Actual Sessions")
class TrackingServiceTest extends BaseIntegrationTest {

    private ScheduleController controller;
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 2, 17);

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Test
    @DisplayName("Creare sesiune actuală")
    void createSession_validSession_success() throws SQLException {
        ActualSession session = new ActualSession(TEST_DATE,
                LocalTime.of(14, 0), LocalTime.of(15, 30), "Development");

        ActualSession created = controller.createSession(session);

        assertNotNull(created.getId());
        assertEquals("Development", created.getTitle());
        assertEquals(90, created.getDurationMinutes());

        List<ActualSession> sessions = controller.getSessions(TEST_DATE);
        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Șterge sesiune")
    void deleteSession_removesSession() throws SQLException {
        ActualSession session = controller.createSession(new ActualSession(TEST_DATE,
                LocalTime.of(16, 0), LocalTime.of(17, 0), "Meeting"));

        controller.deleteSession(session.getId());

        List<ActualSession> sessions = controller.getSessions(TEST_DATE);
        assertTrue(sessions.isEmpty());
    }

    @Test
    @DisplayName("Sesiuni pentru date diferite sunt separate")
    void getSessions_differentDates_returnsCorrectSessions() throws SQLException {
        controller.createSession(new ActualSession(TEST_DATE,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Today"));
        LocalDate otherDate = LocalDate.of(2025, 2, 18);
        controller.createSession(new ActualSession(otherDate,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Tomorrow"));

        List<ActualSession> todaySessions = controller.getSessions(TEST_DATE);
        List<ActualSession> tomorrowSessions = controller.getSessions(otherDate);

        assertEquals(1, todaySessions.size());
        assertEquals(1, tomorrowSessions.size());
        assertEquals("Today", todaySessions.get(0).getTitle());
        assertEquals("Tomorrow", tomorrowSessions.get(0).getTitle());
    }
}
