package schedulemanager.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlanBlock Domain")
class PlanBlockTest {

    @Test
    @DisplayName("Calculează corect durata în minute")
    void getDurationMinutes_returnsCorrectValue() {
        PlanBlock block = new PlanBlock(
                LocalDate.of(2025, 2, 17),
                LocalTime.of(9, 0),
                LocalTime.of(10, 30),
                "Meeting");

        assertEquals(90, block.getDurationMinutes());
    }

    @Test
    @DisplayName("Durata 0 când start/end sunt null")
    void getDurationMinutes_nullTimes_returnsZero() {
        PlanBlock block = new PlanBlock();
        block.setTitle("Empty");

        assertEquals(0, block.getDurationMinutes());
    }
}
