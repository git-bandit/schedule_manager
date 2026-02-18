/**
 * Domain model classes representing core business entities.
 *
 * <p>Contains the data structures used throughout the application:
 * <ul>
 *   <li>{@link schedulemanager.domain.Task} - A task with title, status, priority</li>
 *   <li>{@link schedulemanager.domain.TaskFolder} - Hierarchical folder for organizing tasks</li>
 *   <li>{@link schedulemanager.domain.TodayTask} - Task selected for a specific day</li>
 *   <li>{@link schedulemanager.domain.PlanBlock} - Planned time block on the calendar</li>
 *   <li>{@link schedulemanager.domain.ActualSession} - Recorded work session</li>
 *   <li>{@link schedulemanager.domain.DailyStatistics} - Planned vs actual statistics</li>
 * </ul>
 *
 * <p>Enumerations:
 * <ul>
 *   <li>{@link schedulemanager.domain.TaskStatus} - TODO, DOING, DONE</li>
 *   <li>{@link schedulemanager.domain.Priority} - LOW, MEDIUM, HIGH, URGENT</li>
 * </ul>
 */
package schedulemanager.domain;
