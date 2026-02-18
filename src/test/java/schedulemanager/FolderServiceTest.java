package schedulemanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskFolder;
import schedulemanager.domain.Priority;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Folder Management")
class FolderServiceTest extends BaseIntegrationTest {

    private ScheduleController controller;

    @BeforeEach
    void setUp() {
        controller = new ScheduleController();
    }

    @Test
    @DisplayName("Creare folder rădăcină")
    void createFolder_rootFolder_success() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("My Folder"));

        assertNotNull(folder.getId());
        assertEquals("My Folder", folder.getName());
        assertNull(folder.getParentFolderId());

        List<TaskFolder> roots = controller.getRootFolders();
        assertTrue(roots.stream().anyMatch(f -> "My Folder".equals(f.getName())));
    }

    @Test
    @DisplayName("Creare subfolder")
    void createFolder_subfolder_success() throws SQLException {
        TaskFolder parent = controller.createFolder(new TaskFolder("Parent"));
        TaskFolder child = new TaskFolder("Child");
        child.setParentFolderId(parent.getId());

        TaskFolder created = controller.createFolder(child);

        assertNotNull(created.getId());
        assertEquals(parent.getId(), created.getParentFolderId());

        List<TaskFolder> subfolders = controller.getSubfolders(parent.getId());
        assertEquals(1, subfolders.size());
        assertEquals("Child", subfolders.get(0).getName());
    }

    @Test
    @DisplayName("Nu poate șterge folder cu taskuri")
    void deleteFolder_withTasks_throwsException() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("With Tasks"));
        controller.createTask(new Task("Task", folder.getId(), Priority.MEDIUM));

        assertThrows(IllegalStateException.class, () -> controller.deleteFolder(folder.getId()));
    }

    @Test
    @DisplayName("Nu poate șterge folder cu subfoldere")
    void deleteFolder_withSubfolders_throwsException() throws SQLException {
        TaskFolder parent = controller.createFolder(new TaskFolder("Parent"));
        TaskFolder child = new TaskFolder("Child");
        child.setParentFolderId(parent.getId());
        controller.createFolder(child);

        assertThrows(IllegalStateException.class, () -> controller.deleteFolder(parent.getId()));
    }

    @Test
    @DisplayName("Șterge folder gol")
    void deleteFolder_emptyFolder_success() throws SQLException {
        TaskFolder folder = controller.createFolder(new TaskFolder("Empty"));

        controller.deleteFolder(folder.getId());

        List<TaskFolder> roots = controller.getRootFolders();
        assertFalse(roots.stream().anyMatch(f -> f.getId().equals(folder.getId())));
    }
}
