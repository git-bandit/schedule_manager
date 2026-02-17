package schedulemanager.service;

import schedulemanager.domain.Task;
import schedulemanager.domain.TaskStatus;
import schedulemanager.repository.TaskRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing tasks and business logic related to tasks.
 * 
 * <p>Provides high-level operations for task management, including
 * validation and coordination with repositories.
 * 
 */
public class TaskService {
    private final TaskRepository taskRepository;
    
    /**
     * Constructs a TaskService.
     */
    public TaskService() {
        this.taskRepository = new TaskRepository();
    }
    
    /**
     * Creates a new task with validation.
     * 
     * @param task the task to create
     * @return the created task with generated ID
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public Task createTask(Task task) throws SQLException {
        validateTask(task);
        return taskRepository.save(task);
    }
    
    /**
     * Updates an existing task with validation.
     * 
     * @param task the task to update
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public void updateTask(Task task) throws SQLException {
        validateTask(task);
        taskRepository.update(task);
    }
    
    /**
     * Updates the status of a task.
     * 
     * @param taskId the task ID
     * @param status the new status
     * @throws SQLException if a database error occurs
     */
    public void updateTaskStatus(Long taskId, TaskStatus status) throws SQLException {
        taskRepository.updateStatus(taskId, status);
    }
    
    /**
     * Gets a task by ID.
     * 
     * @param taskId the task ID
     * @return the task, or null if not found
     * @throws SQLException if a database error occurs
     */
    public Task getTask(Long taskId) throws SQLException {
        return taskRepository.findById(taskId);
    }
    
    /**
     * Gets all tasks in a folder.
     * 
     * @param folderId the folder ID
     * @return list of tasks in the folder
     * @throws SQLException if a database error occurs
     */
    public List<Task> getTasksByFolder(Long folderId) throws SQLException {
        return taskRepository.findByFolderId(folderId);
    }
    
    /**
     * Deletes a task.
     * 
     * @param taskId the task ID
     * @throws SQLException if a database error occurs
     */
    public void deleteTask(Long taskId) throws SQLException {
        taskRepository.delete(taskId);
    }
    
    /**
     * Validates a task before saving.
     * 
     * @param task the task to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (task.getFolderId() == null) {
            throw new IllegalArgumentException("Task folder is required");
        }
        if (task.getStatus() == null) {
            throw new IllegalArgumentException("Task status is required");
        }
        if (task.getPriority() == null) {
            throw new IllegalArgumentException("Task priority is required");
        }
    }
}

