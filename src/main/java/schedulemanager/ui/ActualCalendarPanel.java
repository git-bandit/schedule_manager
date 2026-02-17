package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.ActualSession;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for displaying and managing actual activity sessions.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class ActualCalendarPanel extends JPanel {
    private final ScheduleController controller;
    private JList<ActualSession> sessionList;
    private DefaultListModel<ActualSession> listModel;
    private Runnable refreshCallback;
    
    /**
     * Constructs an ActualCalendarPanel.
     * 
     * @param controller the schedule controller
     */
    public ActualCalendarPanel(ScheduleController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        sessionList = new JList<>(listModel);
        sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionList.setCellRenderer(new SessionCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(sessionList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Session");
        JButton deleteButton = new JButton("Delete");
        
        addButton.addActionListener(e -> addSession());
        deleteButton.addActionListener(e -> deleteSession());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the sessions list from the database.
     */
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<List<ActualSession>, Void>() {
                @Override
                protected List<ActualSession> doInBackground() throws Exception {
                    return controller.getSessions(LocalDate.now());
                }
                
                @Override
                protected void done() {
                    try {
                        listModel.clear();
                        List<ActualSession> sessions = get();
                        for (ActualSession session : sessions) {
                            listModel.addElement(session);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ActualCalendarPanel.this,
                            "Error loading sessions: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
    
    /**
     * Shows dialog to add a new actual session.
     */
    private void addSession() {
        ActualSessionDialog dialog = new ActualSessionDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            ActualSession session = dialog.getSession();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.createSession(session);
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
                        JOptionPane.showMessageDialog(ActualCalendarPanel.this,
                            "Error creating session: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    /**
     * Deletes the selected session.
     */
    private void deleteSession() {
        ActualSession selected = sessionList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a session to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete session '" + selected.getTitle() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deleteSession(selected.getId());
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
     * Sets the callback to be invoked when sessions are modified.
     * 
     * @param callback the refresh callback
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    /**
     * Custom cell renderer for session list items.
     */
    private static class SessionCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ActualSession) {
                ActualSession session = (ActualSession) value;
                setText(String.format("%s - %s: %s",
                    session.getStartTime(), session.getEndTime(), session.getTitle()));
            }
            return this;
        }
    }
}

