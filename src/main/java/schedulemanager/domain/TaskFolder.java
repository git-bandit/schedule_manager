package schedulemanager.domain;

/**
 * Represents a folder in the hierarchical task organization structure.
 * 
 * <p>Folders can contain other folders (subfolders) and tasks, creating
 * a tree structure for organizing tasks.
 * 
 */
public class TaskFolder {
    private Long id;
    private String name;
    private Long parentFolderId;
    
    /**
     * Default constructor.
     */
    public TaskFolder() {
    }
    
    /**
     * Constructs a TaskFolder with the specified name.
     * 
     * @param name the name of the folder
     */
    public TaskFolder(String name) {
        this.name = name;
    }
    
    /**
     * Constructs a TaskFolder with all fields.
     * 
     * @param id the unique identifier
     * @param name the name of the folder
     * @param parentFolderId the ID of the parent folder (null for root folders)
     */
    public TaskFolder(Long id, String name, Long parentFolderId) {
        this.id = id;
        this.name = name;
        this.parentFolderId = parentFolderId;
    }
    
    /**
     * Gets the unique identifier of this folder.
     * 
     * @return the folder ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of this folder.
     * 
     * @param id the folder ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the name of this folder.
     * 
     * @return the folder name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this folder.
     * 
     * @param name the folder name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the ID of the parent folder.
     * 
     * @return the parent folder ID, or null if this is a root folder
     */
    public Long getParentFolderId() {
        return parentFolderId;
    }
    
    /**
     * Sets the ID of the parent folder.
     * 
     * @param parentFolderId the parent folder ID, or null for root folders
     */
    public void setParentFolderId(Long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

