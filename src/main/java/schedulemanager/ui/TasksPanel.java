package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.Priority;
import schedulemanager.domain.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for displaying and managing tasks in the selected folder.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class TasksPanel extends JPanel {
    private final ScheduleController controller;
    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;
    private FoldersPanel foldersPanel;
    private Runnable refreshCallback;
    
    /**
     * Constructs a TasksPanel.
     * 
     * @param controller the schedule controller
     */
    public TasksPanel(ScheduleController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton addToTodayButton = new JButton("Add to Today");
        
        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        addToTodayButton.addActionListener(e -> addToToday());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addToTodayButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets the folders panel reference (for getting selected folder).
     * 
     * @param foldersPanel the folders panel
     */
    public void setFoldersPanel(FoldersPanel foldersPanel) {
        this.foldersPanel = foldersPanel;
    }
    
    /**
     * Refreshes the task list based on the selected folder.
     */
    public void refresh() {
        if (foldersPanel == null) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<List<Task>, Void>() {
                @Override
                protected List<Task> doInBackground() throws Exception {
                    var selectedFolder = foldersPanel.getSelectedFolder();
                    if (selectedFolder != null) {
                        return controller.getTasksByFolder(selectedFolder.getId());
                    }
                    return List.of();
                }
                
                @Override
                protected void done() {
                    try {
                        listModel.clear();
                        List<Task> tasks = get();
                        for (Task task : tasks) {
                            listModel.addElement(task);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TasksPanel.this,
                            "Error loading tasks: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
    
    /**
     * Shows dialog to add a new task.
     */
    private void addTask() {
        var selectedFolder = foldersPanel != null ? foldersPanel.getSelectedFolder() : null;
        if (selectedFolder == null) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.",
                "No Folder Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TaskDialog dialog = new TaskDialog(this, selectedFolder.getId(), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Task task = dialog.getTask();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.createTask(task);
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
     * Shows dialog to edit the selected task.
     */
    private void editTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TaskDialog dialog = new TaskDialog(this, selected.getFolderId(), selected);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Task task = dialog.getTask();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.updateTask(task);
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
     * Deletes the selected task.
     */
    private void deleteTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete task '" + selected.getTitle() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deleteTask(selected.getId());
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
     * Adds the selected task to Today list.
     */
    private void addToToday() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to add to Today.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.addTaskToToday(selected.getId(), LocalDate.now());
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    // Explicitly refresh Today panel - the callback should handle this
                    // but we ensure it happens
                    SwingUtilities.invokeLater(() -> {
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                    });
                } catch (Exception e) {
                    String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(TasksPanel.this,
                        "Error adding task to Today: " + message,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Sets the callback to be invoked when tasks are modified.
     * 
     * @param callback the refresh callback
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    /**
     * Custom cell renderer for task list items.
     */
    private static class TaskListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task) {
                Task task = (Task) value;
                setText(task.getTitle() + " [" + task.getStatus() + "]");
                // Color based on priority
                if (task.getPriority() == Priority.URGENT) {
                    setForeground(Color.RED);
                } else if (task.getPriority() == Priority.HIGH) {
                    setForeground(Color.ORANGE);
                }
            }
            return this;
        }
    }
}

