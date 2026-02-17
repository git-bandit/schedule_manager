package schedulemanager.service;

import schedulemanager.domain.ActualSession;
import schedulemanager.repository.ActivityRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing actual activity sessions with validation.
 * 
 * <p>Provides operations for creating, updating, and deleting actual sessions,
 * including validation to prevent overlapping sessions.
 * 
 */
public class TrackingService {
    private final ActivityRepository activityRepository;
    
    /**
     * Constructs a TrackingService.
     */
    public TrackingService() {
        this.activityRepository = new ActivityRepository();
    }
    
    /**
     * Creates a new actual session with validation (no overlaps allowed).
     * 
     * @param session the session to create
     * @return the created session with generated ID
     * @throws IllegalArgumentException if validation fails (overlap, invalid time range)
     * @throws SQLException if a database error occurs
     */
    public ActualSession createSession(ActualSession session) throws SQLException {
        validateSession(session);
        checkNoOverlaps(session);
        return activityRepository.save(session);
    }
    
    /**
     * Updates an existing actual session with validation.
     * 
     * @param session the session to update
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public void updateSession(ActualSession session) throws SQLException {
        validateSession(session);
        checkNoOverlaps(session, session.getId());
        activityRepository.update(session);
    }
    
    /**
     * Gets all actual sessions for a specific date.
     * 
     * @param date the date
     * @return list of sessions for the date
     * @throws SQLException if a database error occurs
     */
    public List<ActualSession> getSessionsForDate(LocalDate date) throws SQLException {
        return activityRepository.findByDate(date);
    }
    
    /**
     * Gets an actual session by ID.
     * 
     * @param sessionId the session ID
     * @return the session, or null if not found
     * @throws SQLException if a database error occurs
     */
    public ActualSession getSession(Long sessionId) throws SQLException {
        return activityRepository.findById(sessionId);
    }
    
    /**
     * Deletes an actual session.
     * 
     * @param sessionId the session ID
     * @throws SQLException if a database error occurs
     */
    public void deleteSession(Long sessionId) throws SQLException {
        activityRepository.delete(sessionId);
    }
    
    /**
     * Validates an actual session (time range, required fields).
     * 
     * @param session the session to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSession(ActualSession session) {
        if (session.getDate() == null) {
            throw new IllegalArgumentException("Session date is required");
        }
        if (session.getStartTime() == null || session.getEndTime() == null) {
            throw new IllegalArgumentException("Session start and end times are required");
        }
        if (!session.getEndTime().isAfter(session.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (session.getTitle() == null || session.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Session title is required");
        }
    }
    
    /**
     * Checks that the session does not overlap with existing sessions for the same date.
     * 
     * @param session the session to check
     * @throws IllegalArgumentException if overlap is detected
     * @throws SQLException if a database error occurs
     */
    private void checkNoOverlaps(ActualSession session) throws SQLException {
        checkNoOverlaps(session, null);
    }
    
    /**
     * Checks that the session does not overlap with existing sessions (excluding the given ID).
     * 
     * @param session the session to check
     * @param excludeId the ID to exclude from overlap check (for updates)
     * @throws IllegalArgumentException if overlap is detected
     * @throws SQLException if a database error occurs
     */
    private void checkNoOverlaps(ActualSession session, Long excludeId) throws SQLException {
        List<ActualSession> existingSessions = activityRepository.findByDate(session.getDate());
        var start = session.getStartTime();
        var end = session.getEndTime();
        
        for (ActualSession existing : existingSessions) {
            if (excludeId != null && existing.getId().equals(excludeId)) {
                continue;
            }
            if (overlaps(start, end, existing.getStartTime(), existing.getEndTime())) {
                throw new IllegalArgumentException(
                    "Session overlaps with existing session: " + existing.getTitle() +
                    " (" + existing.getStartTime() + " - " + existing.getEndTime() + ")");
            }
        }
    }
    
    /**
     * Checks if two time intervals overlap.
     * 
     * @param start1 start of first interval
     * @param end1 end of first interval
     * @param start2 start of second interval
     * @param end2 end of second interval
     * @return true if intervals overlap
     */
    private boolean overlaps(java.time.LocalTime start1, java.time.LocalTime end1,
                            java.time.LocalTime start2, java.time.LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}

