package schedulemanager.domain;

import java.time.LocalDate;

/**
 * Represents daily statistics comparing planned vs actual activities.
 * 
 * <p>This class contains calculated metrics for a specific day, including
 * quantitative accuracy, temporal accuracy, and other insights.
 * 
 */
public class DailyStatistics {
    private LocalDate date;
    private int plannedMinutes;
    private int actualMinutes;
    private int overlapMinutes;
    private double quantitativeAccuracy;
    private double temporalAccuracy;
    
    /**
     * Default constructor.
     */
    public DailyStatistics() {
    }
    
    /**
     * Constructs DailyStatistics for a specific date.
     * 
     * @param date the date for which statistics are calculated
     */
    public DailyStatistics(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Gets the date for which statistics are calculated.
     * 
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date for which statistics are calculated.
     * 
     * @param date the date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Gets the total planned minutes for the day.
     * 
     * @return the planned minutes
     */
    public int getPlannedMinutes() {
        return plannedMinutes;
    }
    
    /**
     * Sets the total planned minutes for the day.
     * 
     * @param plannedMinutes the planned minutes
     */
    public void setPlannedMinutes(int plannedMinutes) {
        this.plannedMinutes = plannedMinutes;
    }
    
    /**
     * Gets the total actual minutes for the day.
     * 
     * @return the actual minutes
     */
    public int getActualMinutes() {
        return actualMinutes;
    }
    
    /**
     * Sets the total actual minutes for the day.
     * 
     * @param actualMinutes the actual minutes
     */
    public void setActualMinutes(int actualMinutes) {
        this.actualMinutes = actualMinutes;
    }
    
    /**
     * Gets the total overlap minutes (intersection of planned and actual time).
     * 
     * @return the overlap minutes
     */
    public int getOverlapMinutes() {
        return overlapMinutes;
    }
    
    /**
     * Sets the total overlap minutes.
     * 
     * @param overlapMinutes the overlap minutes
     */
    public void setOverlapMinutes(int overlapMinutes) {
        this.overlapMinutes = overlapMinutes;
    }
    
    /**
     * Gets the quantitative accuracy (0.0 to 1.0).
     * 
     * <p>Quantitative accuracy measures how much of the planned time
     * was completed in total volume, regardless of when it occurred.
     * 
     * @return the quantitative accuracy (0.0 to 1.0)
     */
    public double getQuantitativeAccuracy() {
        return quantitativeAccuracy;
    }
    
    /**
     * Sets the quantitative accuracy.
     * 
     * @param quantitativeAccuracy the quantitative accuracy (0.0 to 1.0)
     */
    public void setQuantitativeAccuracy(double quantitativeAccuracy) {
        this.quantitativeAccuracy = quantitativeAccuracy;
    }
    
    /**
     * Gets the temporal accuracy (0.0 to 1.0).
     * 
     * <p>Temporal accuracy measures how much of the planned time
     * was completed within the planned time windows (overlap).
     * 
     * @return the temporal accuracy (0.0 to 1.0)
     */
    public double getTemporalAccuracy() {
        return temporalAccuracy;
    }
    
    /**
     * Sets the temporal accuracy.
     * 
     * @param temporalAccuracy the temporal accuracy (0.0 to 1.0)
     */
    public void setTemporalAccuracy(double temporalAccuracy) {
        this.temporalAccuracy = temporalAccuracy;
    }
}

