package schedulemanager.repository;

import schedulemanager.domain.Task;
import schedulemanager.domain.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Task entities in the database.
 * 
 * <p>Provides CRUD operations and queries for tasks.
 * 
 */
public class TaskRepository {
    private final DatabaseManager dbManager;
    
    /**
     * Constructs a TaskRepository.
     */
    public TaskRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new task to the database.
     * 
     * @param task the task to save
     * @return the saved task with generated ID
     * @throws SQLException if a database error occurs
     */
    public Task save(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, folder_id, status, color_tag, priority, " +
                     "deadline, estimate_minutes, description, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setLong(2, task.getFolderId());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setString(4, task.getColorTag());
            pstmt.setString(5, task.getPriority().name());
            if (task.getDeadline() != null) {
                pstmt.setObject(6, java.sql.Date.valueOf(task.getDeadline()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }
            pstmt.setObject(7, task.getEstimateMinutes(), Types.INTEGER);
            pstmt.setString(8, task.getDescription());
            pstmt.setObject(9, java.sql.Timestamp.valueOf(task.getCreatedAt()));
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }
        }
        return task;
    }
    
    /**
     * Finds a task by ID.
     * 
     * @param id the task ID
     * @return the task, or null if not found
     * @throws SQLException if a database error occurs
     */
    public Task findById(Long id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToTask(rs);
            }
        }
        return null;
    }
    
    /**
     * Finds all tasks in a folder.
     * 
     * @param folderId the folder ID
     * @return list of tasks in the folder
     * @throws SQLException if a database error occurs
     */
    public List<Task> findByFolderId(Long folderId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE folder_id = ? ORDER BY created_at DESC";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, folderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
        }
        return tasks;
    }
    
    /**
     * Updates an existing task.
     * 
     * @param task the task to update
     * @throws SQLException if a database error occurs
     */
    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, folder_id = ?, status = ?, color_tag = ?, " +
                     "priority = ?, deadline = ?, estimate_minutes = ?, description = ? " +
                     "WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setLong(2, task.getFolderId());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setString(4, task.getColorTag());
            pstmt.setString(5, task.getPriority().name());
            if (task.getDeadline() != null) {
                pstmt.setObject(6, java.sql.Date.valueOf(task.getDeadline()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }
            pstmt.setObject(7, task.getEstimateMinutes(), Types.INTEGER);
            pstmt.setString(8, task.getDescription());
            pstmt.setLong(9, task.getId());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Updates only the status of a task.
     * 
     * @param taskId the task ID
     * @param status the new status
     * @throws SQLException if a database error occurs
     */
    public void updateStatus(Long taskId, TaskStatus status) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setLong(2, taskId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a task by ID.
     * 
     * @param id the task ID
     * @throws SQLException if a database error occurs
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to a Task object.
     * 
     * @param rs the ResultSet
     * @return the Task object
     * @throws SQLException if a database error occurs
     */
    private Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setFolderId(rs.getLong("folder_id"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setColorTag(rs.getString("color_tag"));
        task.setPriority(schedulemanager.domain.Priority.valueOf(rs.getString("priority")));
        java.sql.Date deadlineDate = rs.getDate("deadline");
        if (deadlineDate != null) {
            task.setDeadline(deadlineDate.toLocalDate());
        }
        int estimate = rs.getInt("estimate_minutes");
        if (!rs.wasNull()) {
            task.setEstimateMinutes(estimate);
        }
        task.setDescription(rs.getString("description"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            task.setCreatedAt(createdAt.toLocalDateTime());
        }
        return task;
    }
}

