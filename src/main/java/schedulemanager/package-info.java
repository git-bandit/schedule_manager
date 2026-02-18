/**
 * Schedule Manager - Application for time management and task tracking.
 *
 * <p>This application allows users to:
 * <ul>
 *   <li>Organize tasks in folders</li>
 *   <li>Plan daily time blocks</li>
 *   <li>Track actual work sessions</li>
 *   <li>Compare planned vs actual time</li>
 *   <li>Get AI-powered insights and recommendations</li>
 * </ul>
 *
 * <p>The architecture follows a layered structure:
 * <ul>
 *   <li>{@link schedulemanager.ui} - User interface (Swing)</li>
 *   <li>{@link schedulemanager.controller} - Application flow coordination</li>
 *   <li>{@link schedulemanager.service} - Business logic</li>
 *   <li>{@link schedulemanager.repository} - Database access</li>
 *   <li>{@link schedulemanager.domain} - Data models</li>
 *   <li>{@link schedulemanager.integration} - External API clients</li>
 * </ul>
 *
 * @see schedulemanager.ui.MainWindow Main application entry point
 */
package schedulemanager;
