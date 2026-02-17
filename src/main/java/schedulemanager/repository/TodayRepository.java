package schedulemanager.repository;

import schedulemanager.domain.TodayTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing TodayTask mappings in the database.
 * 
 * <p>TodayTask represents the selection of tasks for a specific day.
 * This repository handles adding/removing tasks from the Today list
 * and managing their display order.
 * 
 */
public class TodayRepository {
    private final DatabaseManager dbManager;
    
    /**
     * Constructs a TodayRepository.
     */
    public TodayRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Adds a task to the Today list for a specific date.
     * 
     * @param taskId the task ID
     * @param date the date
     * @return the created TodayTask
     * @throws SQLException if a database error occurs
     */
    public TodayTask addTask(Long taskId, LocalDate date) throws SQLException {
        // Get max order for this date
        int maxOrder = getMaxOrderForDate(date);
        
        String sql = "INSERT INTO today_tasks (task_id, date, display_order) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, taskId);
            pstmt.setObject(2, java.sql.Date.valueOf(date));
            pstmt.setInt(3, maxOrder + 1);
            pstmt.executeUpdate();
            
            TodayTask todayTask = new TodayTask(taskId, date);
            todayTask.setDisplayOrder(maxOrder + 1);
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    todayTask.setId(rs.getLong(1));
                }
            }
            return todayTask;
        }
    }
    
    /**
     * Removes a task from the Today list for a specific date.
     * 
     * @param taskId the task ID
     * @param date the date
     * @throws SQLException if a database error occurs
     */
    public void removeTask(Long taskId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM today_tasks WHERE task_id = ? AND date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, taskId);
            pstmt.setObject(2, java.sql.Date.valueOf(date));
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Gets all tasks for a specific date, ordered by display_order.
     * 
     * @param date the date
     * @return list of TodayTask mappings
     * @throws SQLException if a database error occurs
     */
    public List<TodayTask> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM today_tasks WHERE date = ? ORDER BY display_order";
        List<TodayTask> tasks = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRowToTodayTask(rs));
            }
        }
        return tasks;
    }
    
    /**
     * Updates the display order of tasks for a date.
     * 
     * @param date the date
     * @param taskIds ordered list of task IDs
     * @throws SQLException if a database error occurs
     */
    public void updateOrder(LocalDate date, List<Long> taskIds) throws SQLException {
        String sql = "UPDATE today_tasks SET display_order = ? WHERE task_id = ? AND date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < taskIds.size(); i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setLong(2, taskIds.get(i));
                pstmt.setObject(3, java.sql.Date.valueOf(date));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    /**
     * Checks if a task is in the Today list for a specific date.
     * 
     * @param taskId the task ID
     * @param date the date
     * @return true if the task is in Today list
     * @throws SQLException if a database error occurs
     */
    public boolean isTaskInToday(Long taskId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM today_tasks WHERE task_id = ? AND date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, taskId);
            pstmt.setObject(2, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Gets the maximum display order for a date.
     * 
     * @param date the date
     * @return the maximum order, or 0 if no tasks exist
     * @throws SQLException if a database error occurs
     */
    private int getMaxOrderForDate(LocalDate date) throws SQLException {
        String sql = "SELECT MAX(display_order) FROM today_tasks WHERE date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int value = rs.getInt(1);
                if (!rs.wasNull()) {
                    return value;
                }
            }
        }
        return 0;
    }
    
    /**
     * Maps a ResultSet row to a TodayTask object.
     * 
     * @param rs the ResultSet
     * @return the TodayTask object
     * @throws SQLException if a database error occurs
     */
    private TodayTask mapRowToTodayTask(ResultSet rs) throws SQLException {
        TodayTask todayTask = new TodayTask();
        todayTask.setId(rs.getLong("id"));
        todayTask.setTaskId(rs.getLong("task_id"));
        java.sql.Date sqlDate = rs.getDate("date");
        todayTask.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        todayTask.setDisplayOrder(rs.getInt("display_order"));
        return todayTask;
    }
}

