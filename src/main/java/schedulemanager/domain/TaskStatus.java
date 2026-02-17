package schedulemanager.domain;

/**
 * Enumeration representing the status of a task.
 * 
 * <p>Tasks can be in one of three states:
 * <ul>
 *   <li>TODO - Task is planned but not started</li>
 *   <li>DOING - Task is currently in progress</li>
 *   <li>DONE - Task has been completed</li>
 * </ul>
 * 
 */
public enum TaskStatus {
    TODO,
    DOING,
    DONE
}

