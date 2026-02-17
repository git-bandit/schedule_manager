package schedulemanager.domain;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a planned time block in the Plan Calendar.
 * 
 * <p>A plan block defines when the user intends to perform an activity.
 * It contains a time range (startTime to endTime), a title/label, category,
 * and optionally links to a task.
 * 
 */
public class PlanBlock {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String category;
    private Long linkedTaskId;
    
    /**
     * Default constructor.
     */
    public PlanBlock() {
    }
    
    /**
     * Constructs a PlanBlock with the specified date and time range.
     * 
     * @param date the date of the plan block
     * @param startTime the start time
     * @param endTime the end time
     * @param title the title/label of the activity
     */
    public PlanBlock(LocalDate date, LocalTime startTime, LocalTime endTime, String title) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }
    
    /**
     * Gets the unique identifier of this plan block.
     * 
     * @return the plan block ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of this plan block.
     * 
     * @param id the plan block ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the date of this plan block.
     * 
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date of this plan block.
     * 
     * @param date the date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Gets the start time of this plan block.
     * 
     * @return the start time
     */
    public LocalTime getStartTime() {
        return startTime;
    }
    
    /**
     * Sets the start time of this plan block.
     * 
     * @param startTime the start time
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Gets the end time of this plan block.
     * 
     * @return the end time
     */
    public LocalTime getEndTime() {
        return endTime;
    }
    
    /**
     * Sets the end time of this plan block.
     * 
     * @param endTime the end time
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Gets the title/label of this plan block.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title/label of this plan block.
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the category of this plan block.
     * 
     * @return the category, or null if not set
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Sets the category of this plan block.
     * 
     * @param category the category, or null to remove
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * Gets the ID of the linked task.
     * 
     * @return the task ID, or null if not linked to a task
     */
    public Long getLinkedTaskId() {
        return linkedTaskId;
    }
    
    /**
     * Sets the ID of the linked task.
     * 
     * @param linkedTaskId the task ID, or null to unlink
     */
    public void setLinkedTaskId(Long linkedTaskId) {
        this.linkedTaskId = linkedTaskId;
    }
    
    /**
     * Calculates the duration of this plan block in minutes.
     * 
     * @return the duration in minutes
     */
    public int getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }
}

