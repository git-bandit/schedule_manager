package schedulemanager.repository;

import schedulemanager.domain.ActualSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing ActualSession entities in the database.
 * 
 * <p>Provides CRUD operations for actual activity sessions (recorded activities).
 * 
 */
public class ActivityRepository {
    private final DatabaseManager dbManager;
    
    /**
     * Constructs an ActivityRepository.
     */
    public ActivityRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new actual session to the database.
     * 
     * @param session the session to save
     * @return the saved session with generated ID
     * @throws SQLException if a database error occurs
     */
    public ActualSession save(ActualSession session) throws SQLException {
        String sql = "INSERT INTO actual_sessions (date, start_time, end_time, title, category, linked_task_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, java.sql.Date.valueOf(session.getDate()));
            pstmt.setObject(2, java.sql.Time.valueOf(session.getStartTime()));
            pstmt.setObject(3, java.sql.Time.valueOf(session.getEndTime()));
            pstmt.setString(4, session.getTitle());
            pstmt.setString(5, session.getCategory());
            if (session.getLinkedTaskId() != null) {
                pstmt.setLong(6, session.getLinkedTaskId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getLong(1));
                }
            }
        }
        return session;
    }
    
    /**
     * Finds an actual session by ID.
     * 
     * @param id the session ID
     * @return the session, or null if not found
     * @throws SQLException if a database error occurs
     */
    public ActualSession findById(Long id) throws SQLException {
        String sql = "SELECT * FROM actual_sessions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToSession(rs);
            }
        }
        return null;
    }
    
    /**
     * Finds all actual sessions for a specific date, ordered by start time.
     * 
     * @param date the date
     * @return list of sessions for the date
     * @throws SQLException if a database error occurs
     */
    public List<ActualSession> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM actual_sessions WHERE date = ? ORDER BY start_time";
        List<ActualSession> sessions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
        }
        return sessions;
    }
    
    /**
     * Updates an existing actual session.
     * 
     * @param session the session to update
     * @throws SQLException if a database error occurs
     */
    public void update(ActualSession session) throws SQLException {
        String sql = "UPDATE actual_sessions SET date = ?, start_time = ?, end_time = ?, " +
                     "title = ?, category = ?, linked_task_id = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(session.getDate()));
            pstmt.setObject(2, java.sql.Time.valueOf(session.getStartTime()));
            pstmt.setObject(3, java.sql.Time.valueOf(session.getEndTime()));
            pstmt.setString(4, session.getTitle());
            pstmt.setString(5, session.getCategory());
            if (session.getLinkedTaskId() != null) {
                pstmt.setLong(6, session.getLinkedTaskId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setLong(7, session.getId());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes an actual session by ID.
     * 
     * @param id the session ID
     * @throws SQLException if a database error occurs
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM actual_sessions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to an ActualSession object.
     * 
     * @param rs the ResultSet
     * @return the ActualSession object
     * @throws SQLException if a database error occurs
     */
    private ActualSession mapRowToSession(ResultSet rs) throws SQLException {
        ActualSession session = new ActualSession();
        session.setId(rs.getLong("id"));
        java.sql.Date sqlDate = rs.getDate("date");
        session.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        java.sql.Time startTime = rs.getTime("start_time");
        session.setStartTime(startTime != null ? startTime.toLocalTime() : null);
        java.sql.Time endTime = rs.getTime("end_time");
        session.setEndTime(endTime != null ? endTime.toLocalTime() : null);
        session.setTitle(rs.getString("title"));
        session.setCategory(rs.getString("category"));
        long linkedTaskId = rs.getLong("linked_task_id");
        if (!rs.wasNull()) {
            session.setLinkedTaskId(linkedTaskId);
        }
        return session;
    }
}

