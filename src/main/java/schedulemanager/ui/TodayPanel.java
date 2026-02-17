package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for displaying and managing the Today list (tasks selected for today).
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class TodayPanel extends JPanel {
    private final ScheduleController controller;
    private JList<Task> todayList;
    private DefaultListModel<Task> listModel;
    private Runnable refreshCallback;
    
    /**
     * Constructs a TodayPanel.
     * 
     * @param controller the schedule controller
     */
    public TodayPanel(ScheduleController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        todayList = new JList<>(listModel);
        todayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todayList.setCellRenderer(new TodayListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(todayList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton removeButton = new JButton("Remove");
        JButton todoButton = new JButton("TODO");
        JButton doingButton = new JButton("DOING");
        JButton doneButton = new JButton("DONE");
        
        removeButton.addActionListener(e -> removeFromToday());
        todoButton.addActionListener(e -> updateStatus(TaskStatus.TODO));
        doingButton.addActionListener(e -> updateStatus(TaskStatus.DOING));
        doneButton.addActionListener(e -> updateStatus(TaskStatus.DONE));
        
        buttonPanel.add(removeButton);
        buttonPanel.add(todoButton);
        buttonPanel.add(doingButton);
        buttonPanel.add(doneButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the Today list from the database.
     */
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<List<Task>, Void>() {
                @Override
                protected List<Task> doInBackground() throws Exception {
                    return controller.getTodayTasks(LocalDate.now());
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
                        JOptionPane.showMessageDialog(TodayPanel.this,
                            "Error loading today tasks: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
    
    /**
     * Removes the selected task from Today list.
     */
    private void removeFromToday() {
        Task selected = todayList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to remove.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.removeTaskFromToday(selected.getId(), LocalDate.now());
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
    
    /**
     * Updates the status of the selected task.
     * 
     * @param status the new status
     */
    private void updateStatus(TaskStatus status) {
        Task selected = todayList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.updateTaskStatus(selected.getId(), status);
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
    
    /**
     * Sets the callback to be invoked when Today list is modified.
     * 
     * @param callback the refresh callback
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    /**
     * Custom cell renderer for Today list items.
     */
    private static class TodayListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task) {
                Task task = (Task) value;
                setText(task.getTitle() + " [" + task.getStatus() + "]");
                // Visual indication based on status
                if (task.getStatus() == TaskStatus.DONE) {
                    setForeground(Color.GRAY);
                } else if (task.getStatus() == TaskStatus.DOING) {
                    setForeground(Color.BLUE);
                }
            }
            return this;
        }
    }
}

