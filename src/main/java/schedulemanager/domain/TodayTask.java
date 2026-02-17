package schedulemanager.domain;

import java.time.LocalDate;

/**
 * Represents a task selected for "Today" list.
 * 
 * <p>TodayTask is a mapping between a task and a specific date, indicating
 * that the task should be worked on that day. The task status and other
 * attributes are shared with the original Task entity (not duplicated).
 *
 */
public class TodayTask {
    private Long id;
    private Long taskId;
    private LocalDate date;
    private Integer displayOrder;
    
    /**
     * Default constructor.
     */
    public TodayTask() {
    }
    
    /**
     * Constructs a TodayTask linking a task to a date.
     * 
     * @param taskId the ID of the task
     * @param date the date for which this task is selected
     */
    public TodayTask(Long taskId, LocalDate date) {
        this.taskId = taskId;
        this.date = date;
    }
    
    /**
     * Gets the unique identifier of this today task mapping.
     * 
     * @return the today task ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of this today task mapping.
     * 
     * @param id the today task ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the ID of the linked task.
     * 
     * @return the task ID
     */
    public Long getTaskId() {
        return taskId;
    }
    
    /**
     * Sets the ID of the linked task.
     * 
     * @param taskId the task ID
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    /**
     * Gets the date for which this task is selected.
     * 
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date for which this task is selected.
     * 
     * @param date the date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Gets the display order of this task in the Today list.
     * 
     * @return the display order, or null if not set
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    /**
     * Sets the display order of this task in the Today list.
     * 
     * @param displayOrder the display order
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}

