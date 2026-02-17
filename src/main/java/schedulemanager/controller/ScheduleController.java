package schedulemanager.controller;

import schedulemanager.domain.*;
import schedulemanager.integration.AiApiClient;
import schedulemanager.repository.TaskFolderRepository;
import schedulemanager.repository.TodayRepository;
import schedulemanager.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller that coordinates between the UI and service layers.
 * 
 * <p>This controller handles user actions from the UI and delegates to
 * appropriate services, ensuring proper separation of concerns.
 * 
 */
public class ScheduleController {
    private final TaskService taskService;
    private final TaskFolderRepository folderRepository;
    private final TodayRepository todayRepository;
    private final ScheduleService scheduleService;
    private final TrackingService trackingService;
    private final StatsService statsService;
    private final AiApiClient aiApiClient;
    
    /**
     * Constructs a ScheduleController.
     */
    public ScheduleController() {
        this.taskService = new TaskService();
        this.folderRepository = new TaskFolderRepository();
        this.todayRepository = new TodayRepository();
        this.scheduleService = new ScheduleService();
        this.trackingService = new TrackingService();
        this.statsService = new StatsService();
        this.aiApiClient = new AiApiClient();
    }
    
    // Task Management
    
    /**
     * Creates a new task.
     * 
     * @param task the task to create
     * @return the created task
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if validation fails
     */
    public Task createTask(Task task) throws SQLException {
        return taskService.createTask(task);
    }
    
    /**
     * Updates a task.
     * 
     * @param task the task to update
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if validation fails
     */
    public void updateTask(Task task) throws SQLException {
        taskService.updateTask(task);
    }
    
    /**
     * Updates task status.
     * 
     * @param taskId the task ID
     * @param status the new status
     * @throws SQLException if a database error occurs
     */
    public void updateTaskStatus(Long taskId, TaskStatus status) throws SQLException {
        taskService.updateTaskStatus(taskId, status);
    }
    
    /**
     * Gets all tasks in a folder.
     * 
     * @param folderId the folder ID
     * @return list of tasks
     * @throws SQLException if a database error occurs
     */
    public List<Task> getTasksByFolder(Long folderId) throws SQLException {
        return taskService.getTasksByFolder(folderId);
    }
    
    /**
     * Deletes a task.
     * 
     * @param taskId the task ID
     * @throws SQLException if a database error occurs
     */
    public void deleteTask(Long taskId) throws SQLException {
        taskService.deleteTask(taskId);
    }
    
    // Folder Management
    
    /**
     * Creates a new folder.
     * 
     * @param folder the folder to create
     * @return the created folder
     * @throws SQLException if a database error occurs
     */
    public TaskFolder createFolder(TaskFolder folder) throws SQLException {
        return folderRepository.save(folder);
    }
    
    /**
     * Gets all root folders.
     * 
     * @return list of root folders
     * @throws SQLException if a database error occurs
     */
    public List<TaskFolder> getRootFolders() throws SQLException {
        return folderRepository.findRootFolders();
    }
    
    /**
     * Gets subfolders of a parent folder.
     * 
     * @param parentId the parent folder ID
     * @return list of subfolders
     * @throws SQLException if a database error occurs
     */
    public List<TaskFolder> getSubfolders(Long parentId) throws SQLException {
        return folderRepository.findByParentId(parentId);
    }
    
    /**
     * Deletes a folder. Checks if folder has tasks or subfolders first.
     * 
     * @param folderId the folder ID to delete
     * @throws SQLException if a database error occurs
     * @throws IllegalStateException if folder contains tasks or subfolders
     */
    public void deleteFolder(Long folderId) throws SQLException {
        // Check if folder has tasks
        List<Task> tasks = taskService.getTasksByFolder(folderId);
        if (!tasks.isEmpty()) {
            throw new IllegalStateException("Cannot delete folder: it contains " + tasks.size() + " task(s). Please delete or move tasks first.");
        }
        
        // Check if folder has subfolders
        List<TaskFolder> subfolders = folderRepository.findByParentId(folderId);
        if (!subfolders.isEmpty()) {
            throw new IllegalStateException("Cannot delete folder: it contains " + subfolders.size() + " subfolder(s). Please delete or move subfolders first.");
        }
        
        // Safe to delete
        folderRepository.delete(folderId);
    }
    
    // Today List Management
    
    /**
     * Adds a task to the Today list for a date.
     * 
     * @param taskId the task ID
     * @param date the date
     * @throws SQLException if a database error occurs
     * @throws IllegalStateException if task is already in Today list
     */
    public void addTaskToToday(Long taskId, LocalDate date) throws SQLException {
        // Check if task is already in Today list
        if (todayRepository.isTaskInToday(taskId, date)) {
            throw new IllegalStateException("Task is already in Today list for this date.");
        }
        todayRepository.addTask(taskId, date);
    }
    
    /**
     * Removes a task from the Today list for a date.
     * 
     * @param taskId the task ID
     * @param date the date
     * @throws SQLException if a database error occurs
     */
    public void removeTaskFromToday(Long taskId, LocalDate date) throws SQLException {
        todayRepository.removeTask(taskId, date);
    }
    
    /**
     * Gets all tasks in the Today list for a date.
     * 
     * @param date the date
     * @return list of tasks (with full task details)
     * @throws SQLException if a database error occurs
     */
    public List<Task> getTodayTasks(LocalDate date) throws SQLException {
        List<TodayTask> todayTasks = todayRepository.findByDate(date);
        return todayTasks.stream()
            .map(tt -> {
                try {
                    return taskService.getTask(tt.getTaskId());
                } catch (SQLException e) {
                    return null;
                }
            })
            .filter(task -> task != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Updates the order of tasks in the Today list.
     * 
     * @param date the date
     * @param taskIds ordered list of task IDs
     * @throws SQLException if a database error occurs
     */
    public void updateTodayOrder(LocalDate date, List<Long> taskIds) throws SQLException {
        todayRepository.updateOrder(date, taskIds);
    }
    
    // Plan Calendar Management
    
    /**
     * Creates a plan block.
     * 
     * @param block the plan block to create
     * @return the created plan block
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if validation fails (overlap, invalid time)
     */
    public PlanBlock createPlanBlock(PlanBlock block) throws SQLException {
        return scheduleService.createPlanBlock(block);
    }
    
    /**
     * Gets all plan blocks for a date.
     * 
     * @param date the date
     * @return list of plan blocks
     * @throws SQLException if a database error occurs
     */
    public List<PlanBlock> getPlanBlocks(LocalDate date) throws SQLException {
        return scheduleService.getPlanBlocksForDate(date);
    }
    
    /**
     * Deletes a plan block.
     * 
     * @param blockId the plan block ID
     * @throws SQLException if a database error occurs
     */
    public void deletePlanBlock(Long blockId) throws SQLException {
        scheduleService.deletePlanBlock(blockId);
    }
    
    // Actual Calendar Management
    
    /**
     * Creates an actual session.
     * 
     * @param session the session to create
     * @return the created session
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if validation fails (overlap, invalid time)
     */
    public ActualSession createSession(ActualSession session) throws SQLException {
        return trackingService.createSession(session);
    }
    
    /**
     * Gets all actual sessions for a date.
     * 
     * @param date the date
     * @return list of sessions
     * @throws SQLException if a database error occurs
     */
    public List<ActualSession> getSessions(LocalDate date) throws SQLException {
        return trackingService.getSessionsForDate(date);
    }
    
    /**
     * Deletes an actual session.
     * 
     * @param sessionId the session ID
     * @throws SQLException if a database error occurs
     */
    public void deleteSession(Long sessionId) throws SQLException {
        trackingService.deleteSession(sessionId);
    }
    
    // Statistics
    
    /**
     * Computes daily statistics for a date.
     * 
     * @param date the date
     * @return daily statistics
     * @throws SQLException if a database error occurs
     */
    public DailyStatistics getDailyStats(LocalDate date) throws SQLException {
        return statsService.computeDailyStats(date);
    }
    
    /**
     * Computes task-to-task statistics for a date.
     * 
     * @param date the date
     * @return map of task ID to task statistics
     * @throws SQLException if a database error occurs
     */
    public Map<Long, StatsService.TaskStats> getTaskStats(LocalDate date) throws SQLException {
        return statsService.computeTaskStats(date);
    }
    
    // AI Insights
    
    /**
     * Generates AI insights and recommendations for a date.
     * 
     * @param date the date
     * @return insights and recommendations as a string
     */
    public String generateInsights(LocalDate date) {
        return aiApiClient.generateInsights(date, statsService);
    }
}

