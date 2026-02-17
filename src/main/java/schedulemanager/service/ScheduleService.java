package schedulemanager.service;

import schedulemanager.domain.PlanBlock;
import schedulemanager.repository.PlanRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service for managing plan blocks (planned time blocks) with validation.
 * 
 * <p>Provides operations for creating, updating, and deleting plan blocks,
 * including validation to prevent overlapping time blocks.
 * 
 */
public class ScheduleService {
    private final PlanRepository planRepository;
    
    /**
     * Constructs a ScheduleService.
     */
    public ScheduleService() {
        this.planRepository = new PlanRepository();
    }
    
    /**
     * Creates a new plan block with validation (no overlaps allowed).
     * 
     * @param block the plan block to create
     * @return the created plan block with generated ID
     * @throws IllegalArgumentException if validation fails (overlap, invalid time range)
     * @throws SQLException if a database error occurs
     */
    public PlanBlock createPlanBlock(PlanBlock block) throws SQLException {
        validatePlanBlock(block);
        checkNoOverlaps(block);
        return planRepository.save(block);
    }
    
    /**
     * Updates an existing plan block with validation.
     * 
     * @param block the plan block to update
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public void updatePlanBlock(PlanBlock block) throws SQLException {
        validatePlanBlock(block);
        checkNoOverlaps(block, block.getId());
        planRepository.update(block);
    }
    
    /**
     * Gets all plan blocks for a specific date.
     * 
     * @param date the date
     * @return list of plan blocks for the date
     * @throws SQLException if a database error occurs
     */
    public List<PlanBlock> getPlanBlocksForDate(LocalDate date) throws SQLException {
        return planRepository.findByDate(date);
    }
    
    /**
     * Gets a plan block by ID.
     * 
     * @param blockId the plan block ID
     * @return the plan block, or null if not found
     * @throws SQLException if a database error occurs
     */
    public PlanBlock getPlanBlock(Long blockId) throws SQLException {
        return planRepository.findById(blockId);
    }
    
    /**
     * Deletes a plan block.
     * 
     * @param blockId the plan block ID
     * @throws SQLException if a database error occurs
     */
    public void deletePlanBlock(Long blockId) throws SQLException {
        planRepository.delete(blockId);
    }
    
    /**
     * Validates a plan block (time range, required fields).
     * 
     * @param block the plan block to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePlanBlock(PlanBlock block) {
        if (block.getDate() == null) {
            throw new IllegalArgumentException("Plan block date is required");
        }
        if (block.getStartTime() == null || block.getEndTime() == null) {
            throw new IllegalArgumentException("Plan block start and end times are required");
        }
        if (!block.getEndTime().isAfter(block.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (block.getTitle() == null || block.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Plan block title is required");
        }
    }
    
    /**
     * Checks that the plan block does not overlap with existing blocks for the same date.
     * 
     * @param block the plan block to check
     * @throws IllegalArgumentException if overlap is detected
     * @throws SQLException if a database error occurs
     */
    private void checkNoOverlaps(PlanBlock block) throws SQLException {
        checkNoOverlaps(block, null);
    }
    
    /**
     * Checks that the plan block does not overlap with existing blocks (excluding the given ID).
     * 
     * @param block the plan block to check
     * @param excludeId the ID to exclude from overlap check (for updates)
     * @throws IllegalArgumentException if overlap is detected
     * @throws SQLException if a database error occurs
     */
    private void checkNoOverlaps(PlanBlock block, Long excludeId) throws SQLException {
        List<PlanBlock> existingBlocks = planRepository.findByDate(block.getDate());
        LocalTime start = block.getStartTime();
        LocalTime end = block.getEndTime();
        
        for (PlanBlock existing : existingBlocks) {
            if (excludeId != null && existing.getId().equals(excludeId)) {
                continue;
            }
            if (overlaps(start, end, existing.getStartTime(), existing.getEndTime())) {
                throw new IllegalArgumentException(
                    "Plan block overlaps with existing block: " + existing.getTitle() +
                    " (" + existing.getStartTime() + " - " + existing.getEndTime() + ")");
            }
        }
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
}

