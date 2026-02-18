/**
 * Data access layer for database operations.
 *
 * <p>Repositories handle all SQL operations and map database rows to domain objects.
 * Uses MySQL database.
 *
 * <p>Key classes:
 * <ul>
 *   <li>{@link schedulemanager.repository.DatabaseManager} - Connection management and schema initialization</li>
 *   <li>{@link schedulemanager.repository.TaskRepository} - CRUD for tasks</li>
 *   <li>{@link schedulemanager.repository.TaskFolderRepository} - CRUD for folders</li>
 *   <li>{@link schedulemanager.repository.TodayRepository} - Today list mappings</li>
 *   <li>{@link schedulemanager.repository.PlanRepository} - Plan blocks</li>
 *   <li>{@link schedulemanager.repository.ActivityRepository} - Actual sessions</li>
 * </ul>
 */
package schedulemanager.repository;
