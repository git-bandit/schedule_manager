package schedulemanager.domain;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an actual activity session recorded in the Actual Calendar.
 * 
 * <p>An actual session records what the user actually did during a time period.
 * Unlike plan blocks, actual sessions can be split into multiple sessions
 * for the same task. Each session contains a time range, title/label, category,
 * and optionally links to a task.
 * 
 */
public class ActualSession {
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
    public ActualSession() {
    }
    
    /**
     * Constructs an ActualSession with the specified date and time range.
     * 
     * @param date the date of the session
     * @param startTime the start time
     * @param endTime the end time
     * @param title the title/label of the activity
     */
    public ActualSession(LocalDate date, LocalTime startTime, LocalTime endTime, String title) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }
    
    /**
     * Gets the unique identifier of this actual session.
     * 
     * @return the session ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of this actual session.
     * 
     * @param id the session ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the date of this actual session.
     * 
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date of this actual session.
     * 
     * @param date the date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Gets the start time of this actual session.
     * 
     * @return the start time
     */
    public LocalTime getStartTime() {
        return startTime;
    }
    
    /**
     * Sets the start time of this actual session.
     * 
     * @param startTime the start time
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Gets the end time of this actual session.
     * 
     * @return the end time
     */
    public LocalTime getEndTime() {
        return endTime;
    }
    
    /**
     * Sets the end time of this actual session.
     * 
     * @param endTime the end time
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Gets the title/label of this actual session.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title/label of this actual session.
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the category of this actual session.
     * 
     * @return the category, or null if not set
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Sets the category of this actual session.
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
     * Calculates the duration of this actual session in minutes.
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

