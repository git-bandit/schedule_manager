package schedulemanager.ui;

import schedulemanager.domain.Priority;
import schedulemanager.domain.Task;
import schedulemanager.domain.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dialog for creating or editing a task.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class TaskDialog extends JDialog {
    private Task task;
    private final Long folderId;
    private boolean confirmed = false;
    
    private JTextField titleField;
    private JComboBox<Priority> priorityCombo;
    private JComboBox<TaskStatus> statusCombo;
    private JTextField colorTagField;
    private JTextField deadlineField;
    private JSpinner estimateSpinner;
    private JTextArea descriptionArea;
    
    /**
     * Constructs a TaskDialog.
     * 
     * @param parent the parent component
     * @param folderId the folder ID for the task
     * @param task the task to edit (null for new task)
     */
    public TaskDialog(JComponent parent, Long folderId, Task task) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), 
              task == null ? "Add Task" : "Edit Task", true);
        this.folderId = folderId;
        this.task = task;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(Priority.values());
        formPanel.add(priorityCombo, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(TaskStatus.values());
        formPanel.add(statusCombo, gbc);
        
        // Color Tag
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Color Tag:"), gbc);
        gbc.gridx = 1;
        colorTagField = new JTextField(20);
        formPanel.add(colorTagField, gbc);
        
        // Deadline
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        deadlineField = new JTextField(20);
        formPanel.add(deadlineField, gbc);
        
        // Estimate
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Estimate (minutes):"), gbc);
        gbc.gridx = 1;
        estimateSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 15));
        formPanel.add(estimateSpinner, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridy = 7;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            if (validateAndCreateTask()) {
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load existing task data
        if (task != null) {
            loadTaskData();
        }
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    /**
     * Loads task data into the form fields.
     */
    private void loadTaskData() {
        titleField.setText(task.getTitle());
        priorityCombo.setSelectedItem(task.getPriority());
        statusCombo.setSelectedItem(task.getStatus());
        if (task.getColorTag() != null) {
            colorTagField.setText(task.getColorTag());
        }
        if (task.getDeadline() != null) {
            deadlineField.setText(task.getDeadline().toString());
        }
        if (task.getEstimateMinutes() != null) {
            estimateSpinner.setValue(task.getEstimateMinutes());
        }
        if (task.getDescription() != null) {
            descriptionArea.setText(task.getDescription());
        }
    }
    
    /**
     * Validates the form and creates/updates the task.
     * 
     * @return true if validation passed
     */
    private boolean validateAndCreateTask() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (task == null) {
            task = new Task();
        }
        
        task.setTitle(titleField.getText().trim());
        task.setFolderId(folderId);
        task.setPriority((Priority) priorityCombo.getSelectedItem());
        task.setStatus((TaskStatus) statusCombo.getSelectedItem());
        
        String colorTag = colorTagField.getText().trim();
        task.setColorTag(colorTag.isEmpty() ? null : colorTag);
        
        String deadlineStr = deadlineField.getText().trim();
        if (!deadlineStr.isEmpty()) {
            try {
                task.setDeadline(LocalDate.parse(deadlineStr));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            task.setDeadline(null);
        }
        
        task.setEstimateMinutes((Integer) estimateSpinner.getValue());
        
        String description = descriptionArea.getText().trim();
        task.setDescription(description.isEmpty() ? null : description);
        
        return true;
    }
    
    /**
     * Gets the task created/edited in this dialog.
     * 
     * @return the task
     */
    public Task getTask() {
        return task;
    }
    
    /**
     * Checks if the dialog was confirmed (OK clicked).
     * 
     * @return true if confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}

