package schedulemanager.ui;

import schedulemanager.domain.ActualSession;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Dialog for creating or editing an actual session.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class ActualSessionDialog extends JDialog {
    private ActualSession session;
    private boolean confirmed = false;
    
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField titleField;
    private JTextField categoryField;
    
    /**
     * Constructs an ActualSessionDialog.
     * 
     * @param parent the parent component
     * @param session the session to edit (null for new session)
     */
    public ActualSessionDialog(JComponent parent, ActualSession session) {
        super((Frame) SwingUtilities.getWindowAncestor(parent),
              session == null ? "Add Session" : "Edit Session", true);
        this.session = session;
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
        
        // Start Time
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(10);
        formPanel.add(startTimeField, gbc);
        
        // End Time
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        endTimeField = new JTextField(10);
        formPanel.add(endTimeField, gbc);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(20);
        formPanel.add(categoryField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            if (validateAndCreateSession()) {
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load existing session data
        if (session != null) {
            loadSessionData();
        }
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    /**
     * Loads session data into the form fields.
     */
    private void loadSessionData() {
        startTimeField.setText(session.getStartTime().toString());
        endTimeField.setText(session.getEndTime().toString());
        titleField.setText(session.getTitle());
        if (session.getCategory() != null) {
            categoryField.setText(session.getCategory());
        }
    }
    
    /**
     * Validates the form and creates/updates the session.
     * 
     * @return true if validation passed
     */
    private boolean validateAndCreateSession() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        LocalTime startTime;
        LocalTime endTime;
        
        try {
            startTime = LocalTime.parse(startTimeField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid start time format. Use HH:mm.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            endTime = LocalTime.parse(endTimeField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid end time format. Use HH:mm.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!endTime.isAfter(startTime)) {
            JOptionPane.showMessageDialog(this, "End time must be after start time.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (session == null) {
            session = new ActualSession();
        }
        
        session.setDate(LocalDate.now());
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setTitle(titleField.getText().trim());
        
        String category = categoryField.getText().trim();
        session.setCategory(category.isEmpty() ? null : category);
        
        return true;
    }
    
    /**
     * Gets the session created/edited in this dialog.
     * 
     * @return the session
     */
    public ActualSession getSession() {
        return session;
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

