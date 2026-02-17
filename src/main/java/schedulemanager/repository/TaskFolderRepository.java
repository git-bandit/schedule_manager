package schedulemanager.repository;

import schedulemanager.domain.TaskFolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing TaskFolder entities in the database.
 * 
 * <p>Provides CRUD operations and hierarchical queries for task folders.
 * 
 */
public class TaskFolderRepository {
    private final DatabaseManager dbManager;
    
    /**
     * Constructs a TaskFolderRepository.
     */
    public TaskFolderRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new task folder to the database.
     * 
     * @param folder the folder to save
     * @return the saved folder with generated ID
     * @throws SQLException if a database error occurs
     */
    public TaskFolder save(TaskFolder folder) throws SQLException {
        String sql = "INSERT INTO task_folders (name, parent_folder_id) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, folder.getName());
            if (folder.getParentFolderId() != null) {
                pstmt.setLong(2, folder.getParentFolderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    folder.setId(rs.getLong(1));
                }
            }
        }
        return folder;
    }
    
    /**
     * Finds a task folder by ID.
     * 
     * @param id the folder ID
     * @return the folder, or null if not found
     * @throws SQLException if a database error occurs
     */
    public TaskFolder findById(Long id) throws SQLException {
        String sql = "SELECT * FROM task_folders WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToFolder(rs);
            }
        }
        return null;
    }
    
    /**
     * Finds all root folders (folders without a parent).
     * 
     * @return list of root folders
     * @throws SQLException if a database error occurs
     */
    public List<TaskFolder> findRootFolders() throws SQLException {
        String sql = "SELECT * FROM task_folders WHERE parent_folder_id IS NULL ORDER BY name";
        List<TaskFolder> folders = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                folders.add(mapRowToFolder(rs));
            }
        }
        return folders;
    }
    
    /**
     * Finds all subfolders of a given parent folder.
     * 
     * @param parentId the parent folder ID
     * @return list of subfolders
     * @throws SQLException if a database error occurs
     */
    public List<TaskFolder> findByParentId(Long parentId) throws SQLException {
        String sql = "SELECT * FROM task_folders WHERE parent_folder_id = ? ORDER BY name";
        List<TaskFolder> folders = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, parentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                folders.add(mapRowToFolder(rs));
            }
        }
        return folders;
    }
    
    /**
     * Finds all folders.
     * 
     * @return list of all folders
     * @throws SQLException if a database error occurs
     */
    public List<TaskFolder> findAll() throws SQLException {
        String sql = "SELECT * FROM task_folders ORDER BY name";
        List<TaskFolder> folders = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                folders.add(mapRowToFolder(rs));
            }
        }
        return folders;
    }
    
    /**
     * Updates an existing task folder.
     * 
     * @param folder the folder to update
     * @throws SQLException if a database error occurs
     */
    public void update(TaskFolder folder) throws SQLException {
        String sql = "UPDATE task_folders SET name = ?, parent_folder_id = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, folder.getName());
            if (folder.getParentFolderId() != null) {
                pstmt.setLong(2, folder.getParentFolderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setLong(3, folder.getId());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a task folder by ID.
     * 
     * @param id the folder ID
     * @throws SQLException if a database error occurs
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM task_folders WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to a TaskFolder object.
     * 
     * @param rs the ResultSet
     * @return the TaskFolder object
     * @throws SQLException if a database error occurs
     */
    private TaskFolder mapRowToFolder(ResultSet rs) throws SQLException {
        TaskFolder folder = new TaskFolder();
        folder.setId(rs.getLong("id"));
        folder.setName(rs.getString("name"));
        long parentId = rs.getLong("parent_folder_id");
        if (!rs.wasNull()) {
            folder.setParentFolderId(parentId);
        }
        return folder;
    }
}

