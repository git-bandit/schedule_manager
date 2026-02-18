/**
 * Business logic layer (application services).
 *
 * <p>Services contain validation rules, calculations, and coordinate between
 * multiple repositories. They are called by the controller.
 *
 * <p>Key classes:
 * <ul>
 *   <li>{@link schedulemanager.service.TaskService} - Task creation, validation, folder queries</li>
 *   <li>{@link schedulemanager.service.ScheduleService} - Plan blocks and overlap validation</li>
 *   <li>{@link schedulemanager.service.TrackingService} - Actual session management</li>
 *   <li>{@link schedulemanager.service.StatsService} - Statistics and accuracy metrics</li>
 * </ul>
 */
package schedulemanager.service;
