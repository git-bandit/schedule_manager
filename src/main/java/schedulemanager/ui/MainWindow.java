package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for Schedule Manager.
 * 
 * <p>This window contains all the main panels:
 * <ul>
 *   <li>Left: Folders and Tasks panels</li>
 *   <li>Right: Today list, Plan Calendar, and Actual Calendar panels</li>
 *   <li>Bottom: Statistics panel with AI insights</li>
 * </ul>
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class MainWindow extends JFrame {
    private final ScheduleController controller;
    private FoldersPanel foldersPanel;
    private TasksPanel tasksPanel;
    private TodayPanel todayPanel;
    private PlanCalendarPanel planCalendarPanel;
    private ActualCalendarPanel actualCalendarPanel;
    private StatsPanel statsPanel;
    
    /**
     * Constructs the main window.
     */
    public MainWindow() {
        this.controller = new ScheduleController();
        initializeUI();
    }
    
    /**
     * Initializes the UI components and layout.
     */
    private void initializeUI() {
        setTitle("Schedule Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Create panels
        foldersPanel = new FoldersPanel(controller);
        tasksPanel = new TasksPanel(controller);
        todayPanel = new TodayPanel(controller);
        planCalendarPanel = new PlanCalendarPanel(controller);
        actualCalendarPanel = new ActualCalendarPanel(controller);
        statsPanel = new StatsPanel(controller);
        
        // Set up layout
        setLayout(new BorderLayout());
        
        // Left panel: Folders and Tasks
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Task Manager"));
        leftPanel.add(foldersPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(tasksPanel), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        
        // Right panel: Today, Plan, Actual
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel todayContainer = new JPanel(new BorderLayout());
        todayContainer.setBorder(BorderFactory.createTitledBorder("Today"));
        todayContainer.add(new JScrollPane(todayPanel), BorderLayout.CENTER);
        rightPanel.add(todayContainer);
        
        JPanel planContainer = new JPanel(new BorderLayout());
        planContainer.setBorder(BorderFactory.createTitledBorder("Plan Calendar"));
        planContainer.add(new JScrollPane(planCalendarPanel), BorderLayout.CENTER);
        rightPanel.add(planContainer);
        
        JPanel actualContainer = new JPanel(new BorderLayout());
        actualContainer.setBorder(BorderFactory.createTitledBorder("Actual Calendar"));
        actualContainer.add(new JScrollPane(actualCalendarPanel), BorderLayout.CENTER);
        rightPanel.add(actualContainer);
        
        // Add panels to main window
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
        
        // Connect panels
        tasksPanel.setFoldersPanel(foldersPanel);
        
        // Set up refresh callbacks
        foldersPanel.setRefreshCallback(() -> {
            tasksPanel.refresh();
            todayPanel.refresh();
        });
        tasksPanel.setRefreshCallback(() -> {
            todayPanel.refresh();
            planCalendarPanel.refresh();
            actualCalendarPanel.refresh();
        });
        todayPanel.setRefreshCallback(() -> {
            planCalendarPanel.refresh();
            actualCalendarPanel.refresh();
            statsPanel.refresh();
        });
        planCalendarPanel.setRefreshCallback(() -> statsPanel.refresh());
        actualCalendarPanel.setRefreshCallback(() -> statsPanel.refresh());
        
        // Add folder selection listener
        foldersPanel.getFolderTree().addTreeSelectionListener(e -> tasksPanel.refresh());
        
        // Initial refresh - load all data at startup
        foldersPanel.refresh();
        planCalendarPanel.refresh();
        actualCalendarPanel.refresh();
        todayPanel.refresh();
        statsPanel.refresh();
    }
    
    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow().setVisible(true);
        });
    }
}

