package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.PlanBlock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for displaying and managing plan blocks (planned time blocks).
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class PlanCalendarPanel extends JPanel {
    private final ScheduleController controller;
    private JList<PlanBlock> planList;
    private DefaultListModel<PlanBlock> listModel;
    private Runnable refreshCallback;
    
    /**
     * Constructs a PlanCalendarPanel.
     * 
     * @param controller the schedule controller
     */
    public PlanCalendarPanel(ScheduleController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        planList = new JList<>(listModel);
        planList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planList.setCellRenderer(new PlanBlockCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(planList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Block");
        JButton deleteButton = new JButton("Delete");
        
        addButton.addActionListener(e -> addPlanBlock());
        deleteButton.addActionListener(e -> deletePlanBlock());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the plan blocks list from the database.
     */
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<List<PlanBlock>, Void>() {
                @Override
                protected List<PlanBlock> doInBackground() throws Exception {
                    return controller.getPlanBlocks(LocalDate.now());
                }
                
                @Override
                protected void done() {
                    try {
                        listModel.clear();
                        List<PlanBlock> blocks = get();
                        for (PlanBlock block : blocks) {
                            listModel.addElement(block);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PlanCalendarPanel.this,
                            "Error loading plan blocks: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
    
    /**
     * Shows dialog to add a new plan block.
     */
    private void addPlanBlock() {
        PlanBlockDialog dialog = new PlanBlockDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            PlanBlock block = dialog.getPlanBlock();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.createPlanBlock(block);
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
                        JOptionPane.showMessageDialog(PlanCalendarPanel.this,
                            "Error creating plan block: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    /**
     * Deletes the selected plan block.
     */
    private void deletePlanBlock() {
        PlanBlock selected = planList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a plan block to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete plan block '" + selected.getTitle() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deletePlanBlock(selected.getId());
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
     * Sets the callback to be invoked when plan blocks are modified.
     * 
     * @param callback the refresh callback
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    /**
     * Custom cell renderer for plan block list items.
     */
    private static class PlanBlockCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PlanBlock) {
                PlanBlock block = (PlanBlock) value;
                setText(String.format("%s - %s: %s",
                    block.getStartTime(), block.getEndTime(), block.getTitle()));
            }
            return this;
        }
    }
}

