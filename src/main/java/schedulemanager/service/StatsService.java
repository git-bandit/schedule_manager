package schedulemanager.service;

import schedulemanager.domain.ActualSession;
import schedulemanager.domain.DailyStatistics;
import schedulemanager.domain.PlanBlock;
import schedulemanager.repository.ActivityRepository;
import schedulemanager.repository.PlanRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating statistics comparing planned vs actual activities.
 * 
 * <p>Provides methods to calculate quantitative accuracy, temporal accuracy,
 * and task-to-task comparisons.
 * 
 */
public class StatsService {
    private final PlanRepository planRepository;
    private final ActivityRepository activityRepository;
    
    /**
     * Constructs a StatsService.
     */
    public StatsService() {
        this.planRepository = new PlanRepository();
        this.activityRepository = new ActivityRepository();
    }
    
    /**
     * Computes daily statistics for a specific date.
     * 
     * <p>Calculates:
     * <ul>
     *   <li>Total planned minutes</li>
     *   <li>Total actual minutes</li>
     *   <li>Overlap minutes (intersection of planned and actual time)</li>
     *   <li>Quantitative accuracy (0.0 to 1.0)</li>
     *   <li>Temporal accuracy (0.0 to 1.0)</li>
     * </ul>
     * 
     * @param date the date for which to compute statistics
     * @return the daily statistics
     * @throws SQLException if a database error occurs
     */
    public DailyStatistics computeDailyStats(LocalDate date) throws SQLException {
        List<PlanBlock> planBlocks = planRepository.findByDate(date);
        List<ActualSession> sessions = activityRepository.findByDate(date);
        
        DailyStatistics stats = new DailyStatistics(date);
        
        // Calculate total planned and actual minutes
        int plannedMinutes = planBlocks.stream()
            .mapToInt(PlanBlock::getDurationMinutes)
            .sum();
        int actualMinutes = sessions.stream()
            .mapToInt(ActualSession::getDurationMinutes)
            .sum();
        
        stats.setPlannedMinutes(plannedMinutes);
        stats.setActualMinutes(actualMinutes);
        
        // Calculate overlap (intersection of planned and actual time)
        int overlapMinutes = calculateOverlapMinutes(planBlocks, sessions);
        stats.setOverlapMinutes(overlapMinutes);
        
        // Calculate quantitative accuracy
        double quantAccuracy = 0.0;
        if (plannedMinutes > 0) {
            quantAccuracy = Math.min(actualMinutes, plannedMinutes) / (double) plannedMinutes;
        }
        stats.setQuantitativeAccuracy(quantAccuracy);
        
        // Calculate temporal accuracy
        double tempAccuracy = 0.0;
        if (plannedMinutes > 0) {
            tempAccuracy = overlapMinutes / (double) plannedMinutes;
        }
        stats.setTemporalAccuracy(tempAccuracy);
        
        return stats;
    }
    
    /**
     * Computes task-to-task statistics for a specific date.
     * 
     * <p>For each task that has planned blocks or actual sessions, calculates:
     * <ul>
     *   <li>Planned minutes for the task</li>
     *   <li>Actual minutes for the task</li>
     *   <li>Temporal deviation (overlap minutes for the task)</li>
     * </ul>
     * 
     * @param date the date for which to compute statistics
     * @return map of task ID to task statistics (planned minutes, actual minutes, overlap minutes)
     * @throws SQLException if a database error occurs
     */
    public Map<Long, TaskStats> computeTaskStats(LocalDate date) throws SQLException {
        List<PlanBlock> planBlocks = planRepository.findByDate(date);
        List<ActualSession> sessions = activityRepository.findByDate(date);
        
        Map<Long, TaskStats> taskStatsMap = new HashMap<>();
        
        // Process plan blocks
        for (PlanBlock block : planBlocks) {
            if (block.getLinkedTaskId() != null) {
                TaskStats stats = taskStatsMap.computeIfAbsent(
                    block.getLinkedTaskId(), k -> new TaskStats());
                stats.plannedMinutes += block.getDurationMinutes();
                stats.plannedBlocks.add(block);
            }
        }
        
        // Process actual sessions
        for (ActualSession session : sessions) {
            if (session.getLinkedTaskId() != null) {
                TaskStats stats = taskStatsMap.computeIfAbsent(
                    session.getLinkedTaskId(), k -> new TaskStats());
                stats.actualMinutes += session.getDurationMinutes();
                stats.actualSessions.add(session);
            }
        }
        
        // Calculate overlap for each task
        for (Map.Entry<Long, TaskStats> entry : taskStatsMap.entrySet()) {
            TaskStats stats = entry.getValue();
            stats.overlapMinutes = calculateOverlapMinutes(
                stats.plannedBlocks, stats.actualSessions);
        }
        
        return taskStatsMap;
    }
    
    /**
     * Calculates the total overlap minutes between plan blocks and actual sessions.
     * 
     * @param planBlocks list of plan blocks
     * @param sessions list of actual sessions
     * @return total overlap minutes
     */
    private int calculateOverlapMinutes(List<PlanBlock> planBlocks, List<ActualSession> sessions) {
        int totalOverlap = 0;
        
        for (PlanBlock block : planBlocks) {
            for (ActualSession session : sessions) {
                // Only calculate overlap if they're on the same date (should be, but check anyway)
                if (block.getDate().equals(session.getDate())) {
                    int overlap = calculateIntervalOverlap(
                        block.getStartTime(), block.getEndTime(),
                        session.getStartTime(), session.getEndTime());
                    totalOverlap += overlap;
                }
            }
        }
        
        return totalOverlap;
    }
    
    /**
     * Calculates the overlap in minutes between two time intervals.
     * 
     * @param start1 start of first interval
     * @param end1 end of first interval
     * @param start2 start of second interval
     * @param end2 end of second interval
     * @return overlap in minutes, or 0 if no overlap
     */
    private int calculateIntervalOverlap(LocalTime start1, LocalTime end1,
                                         LocalTime start2, LocalTime end2) {
        if (!overlaps(start1, end1, start2, end2)) {
            return 0;
        }
        
        LocalTime overlapStart = start1.isAfter(start2) ? start1 : start2;
        LocalTime overlapEnd = end1.isBefore(end2) ? end1 : end2;
        
        return (int) java.time.Duration.between(overlapStart, overlapEnd).toMinutes();
    }
    
    /**
     * Checks if two time intervals overlap.
     * 
     * @param start1 start of first interval
     * @param end1 end of first interval
     * @param start2 start of second interval
     * @param end2 end of second interval
     * @return true if intervals overlap
     */
    private boolean overlaps(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    /**
     * Inner class to hold task statistics.
     */
    public static class TaskStats {
        public int plannedMinutes = 0;
        public int actualMinutes = 0;
        public int overlapMinutes = 0;
        public List<PlanBlock> plannedBlocks = new ArrayList<>();
        public List<ActualSession> actualSessions = new ArrayList<>();
    }
}

