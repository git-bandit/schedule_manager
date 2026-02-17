package schedulemanager.repository;

import schedulemanager.domain.PlanBlock;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing PlanBlock entities in the database.
 * 
 * <p>Provides CRUD operations for plan blocks (planned time blocks).
 * 
 */
public class PlanRepository {
    private final DatabaseManager dbManager;
    
    /**
     * Constructs a PlanRepository.
     */
    public PlanRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new plan block to the database.
     * 
     * @param block the plan block to save
     * @return the saved plan block with generated ID
     * @throws SQLException if a database error occurs
     */
    public PlanBlock save(PlanBlock block) throws SQLException {
        String sql = "INSERT INTO plan_blocks (date, start_time, end_time, title, category, linked_task_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, java.sql.Date.valueOf(block.getDate()));
            pstmt.setObject(2, java.sql.Time.valueOf(block.getStartTime()));
            pstmt.setObject(3, java.sql.Time.valueOf(block.getEndTime()));
            pstmt.setString(4, block.getTitle());
            pstmt.setString(5, block.getCategory());
            if (block.getLinkedTaskId() != null) {
                pstmt.setLong(6, block.getLinkedTaskId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    block.setId(rs.getLong(1));
                }
            }
        }
        return block;
    }
    
    /**
     * Finds a plan block by ID.
     * 
     * @param id the plan block ID
     * @return the plan block, or null if not found
     * @throws SQLException if a database error occurs
     */
    public PlanBlock findById(Long id) throws SQLException {
        String sql = "SELECT * FROM plan_blocks WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToPlanBlock(rs);
            }
        }
        return null;
    }
    
    /**
     * Finds all plan blocks for a specific date, ordered by start time.
     * 
     * @param date the date
     * @return list of plan blocks for the date
     * @throws SQLException if a database error occurs
     */
    public List<PlanBlock> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM plan_blocks WHERE date = ? ORDER BY start_time";
        List<PlanBlock> blocks = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                blocks.add(mapRowToPlanBlock(rs));
            }
        }
        return blocks;
    }
    
    /**
     * Updates an existing plan block.
     * 
     * @param block the plan block to update
     * @throws SQLException if a database error occurs
     */
    public void update(PlanBlock block) throws SQLException {
        String sql = "UPDATE plan_blocks SET date = ?, start_time = ?, end_time = ?, " +
                     "title = ?, category = ?, linked_task_id = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(block.getDate()));
            pstmt.setObject(2, java.sql.Time.valueOf(block.getStartTime()));
            pstmt.setObject(3, java.sql.Time.valueOf(block.getEndTime()));
            pstmt.setString(4, block.getTitle());
            pstmt.setString(5, block.getCategory());
            if (block.getLinkedTaskId() != null) {
                pstmt.setLong(6, block.getLinkedTaskId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setLong(7, block.getId());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a plan block by ID.
     * 
     * @param id the plan block ID
     * @throws SQLException if a database error occurs
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM plan_blocks WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to a PlanBlock object.
     * 
     * @param rs the ResultSet
     * @return the PlanBlock object
     * @throws SQLException if a database error occurs
     */
    private PlanBlock mapRowToPlanBlock(ResultSet rs) throws SQLException {
        PlanBlock block = new PlanBlock();
        block.setId(rs.getLong("id"));
        java.sql.Date sqlDate = rs.getDate("date");
        block.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        java.sql.Time startTime = rs.getTime("start_time");
        block.setStartTime(startTime != null ? startTime.toLocalTime() : null);
        java.sql.Time endTime = rs.getTime("end_time");
        block.setEndTime(endTime != null ? endTime.toLocalTime() : null);
        block.setTitle(rs.getString("title"));
        block.setCategory(rs.getString("category"));
        long linkedTaskId = rs.getLong("linked_task_id");
        if (!rs.wasNull()) {
            block.setLinkedTaskId(linkedTaskId);
        }
        return block;
    }
}

