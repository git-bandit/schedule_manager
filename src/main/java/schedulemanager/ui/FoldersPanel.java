package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.TaskFolder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel for displaying and managing task folders in a tree structure.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class FoldersPanel extends JPanel {
    private final ScheduleController controller;
    private JTree folderTree;
    private DefaultTreeModel treeModel;
    private Runnable refreshCallback;
    
    /**
     * Constructs a FoldersPanel.
     * 
     * @param controller the schedule controller
     */
    public FoldersPanel(ScheduleController controller) {
        this.controller = controller;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Folders");
        treeModel = new DefaultTreeModel(root);
        folderTree = new JTree(treeModel);
        folderTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        folderTree.setRootVisible(false);
        
        // Allow deselection by Ctrl+Click or clicking on empty space
        folderTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Ctrl+Click to deselect
                if (e.isControlDown()) {
                    folderTree.clearSelection();
                    return;
                }
                // If clicking on empty space (not on a node), deselect
                int row = folderTree.getRowForLocation(e.getX(), e.getY());
                if (row == -1) {
                    folderTree.clearSelection();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(folderTree);
        scrollPane.setPreferredSize(new Dimension(280, 200));
        // Allow deselection by clicking on empty space in scroll pane
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Only deselect if clicking directly on scroll pane (not on tree)
                if (e.getSource() == scrollPane) {
                    folderTree.clearSelection();
                }
            }
        });
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Folder");
        JButton deleteButton = new JButton("Delete");
        
        addButton.addActionListener(e -> addFolder());
        deleteButton.addActionListener(e -> deleteFolder());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the folder tree from the database.
     */
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
                    root.removeAllChildren();
                    
                    List<TaskFolder> rootFolders = controller.getRootFolders();
                    for (TaskFolder folder : rootFolders) {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
                        root.add(node);
                        loadSubfolders(node, folder.getId());
                    }
                    
                    return null;
                }
                
                @Override
                protected void done() {
                    treeModel.reload();
                }
            }.execute();
        });
    }
    
    /**
     * Recursively loads subfolders into the tree.
     * 
     * @param parentNode the parent tree node
     * @param parentId the parent folder ID
     */
    private void loadSubfolders(DefaultMutableTreeNode parentNode, Long parentId) {
        try {
            List<TaskFolder> subfolders = controller.getSubfolders(parentId);
            for (TaskFolder folder : subfolders) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
                parentNode.add(node);
                loadSubfolders(node, folder.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading subfolders: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows dialog to add a new folder.
     */
    private void addFolder() {
        String name = JOptionPane.showInputDialog(this, "Folder name:", "Add Folder",
            JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
            Long parentId = null;
            if (selectedNode != null && selectedNode.getUserObject() instanceof TaskFolder) {
                parentId = ((TaskFolder) selectedNode.getUserObject()).getId();
            }
            
            TaskFolder folder = new TaskFolder(name.trim());
            folder.setParentFolderId(parentId);
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.createFolder(folder);
                    return null;
                }
                
                @Override
                protected void done() {
                    refresh();
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                }
            }.execute();
        }
    }
    
    /**
     * Deletes the selected folder.
     */
    private void deleteFolder() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof TaskFolder)) {
            JOptionPane.showMessageDialog(this, "Please select a folder to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TaskFolder folder = (TaskFolder) selectedNode.getUserObject();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete folder '" + folder.getName() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deleteFolder(folder.getId());
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get(); // Check for exceptions
                        refresh();
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                    } catch (Exception e) {
                        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                        JOptionPane.showMessageDialog(FoldersPanel.this,
                            "Error deleting folder: " + message,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    /**
     * Gets the selected folder.
     * 
     * @return the selected folder, or null if none selected
     */
    public TaskFolder getSelectedFolder() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof TaskFolder) {
            return (TaskFolder) selectedNode.getUserObject();
        }
        return null;
    }
    
    /**
     * Sets the callback to be invoked when folders are modified.
     * 
     * @param callback the refresh callback
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    /**
     * Gets the folder tree component.
     * 
     * @return the folder tree
     */
    public JTree getFolderTree() {
        return folderTree;
    }
}

