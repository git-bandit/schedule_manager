package schedulemanager.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task in the task management system.
 * 
 * <p>A task has required attributes (title, folder, status, color/tag, priority)
 * and optional attributes (deadline, estimate, description). Tasks can be
 * linked to plan blocks and actual sessions for time tracking.
 * 
 */
public class Task {
    private Long id;
    private String title;
    private Long folderId;
    private TaskStatus status;
    private String colorTag;
    private Priority priority;
    private LocalDate deadline;
    private Integer estimateMinutes;
    private String description;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor.
     */
    public Task() {
        this.status = TaskStatus.TODO;
        this.priority = Priority.MEDIUM;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructs a Task with required fields.
     * 
     * @param title the task title
     * @param folderId the ID of the folder containing this task
     * @param priority the task priority
     */
    public Task(String title, Long folderId, Priority priority) {
        this();
        this.title = title;
        this.folderId = folderId;
        this.priority = priority;
    }
    
    /**
     * Gets the unique identifier of this task.
     * 
     * @return the task ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of this task.
     * 
     * @param id the task ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the title of this task.
     * 
     * @return the task title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of this task.
     * 
     * @param title the task title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the ID of the folder containing this task.
     * 
     * @return the folder ID
     */
    public Long getFolderId() {
        return folderId;
    }
    
    /**
     * Sets the ID of the folder containing this task.
     * 
     * @param folderId the folder ID
     */
    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }
    
    /**
     * Gets the current status of this task.
     * 
     * @return the task status
     */
    public TaskStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the current status of this task.
     * 
     * @param status the task status
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the color/tag associated with this task.
     * 
     * @return the color tag
     */
    public String getColorTag() {
        return colorTag;
    }
    
    /**
     * Sets the color/tag associated with this task.
     * 
     * @param colorTag the color tag
     */
    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }
    
    /**
     * Gets the priority of this task.
     * 
     * @return the task priority
     */
    public Priority getPriority() {
        return priority;
    }
    
    /**
     * Sets the priority of this task.
     * 
     * @param priority the task priority
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    /**
     * Gets the deadline for this task.
     * 
     * @return the deadline, or null if not set
     */
    public LocalDate getDeadline() {
        return deadline;
    }
    
    /**
     * Sets the deadline for this task.
     * 
     * @param deadline the deadline, or null to remove
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    /**
     * Gets the estimated time to complete this task in minutes.
     * 
     * @return the estimate in minutes, or null if not set
     */
    public Integer getEstimateMinutes() {
        return estimateMinutes;
    }
    
    /**
     * Sets the estimated time to complete this task in minutes.
     * 
     * @param estimateMinutes the estimate in minutes, or null to remove
     */
    public void setEstimateMinutes(Integer estimateMinutes) {
        this.estimateMinutes = estimateMinutes;
    }
    
    /**
     * Gets the description of this task.
     * 
     * @return the description, or null if not set
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of this task.
     * 
     * @param description the description, or null to remove
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the creation timestamp of this task.
     * 
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp of this task.
     * 
     * @param createdAt the creation date and time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return title;
    }
}

