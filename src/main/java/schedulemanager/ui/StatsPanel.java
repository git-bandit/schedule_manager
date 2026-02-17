package schedulemanager.ui;

import schedulemanager.controller.ScheduleController;
import schedulemanager.domain.DailyStatistics;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Panel for displaying statistics and AI insights.
 * 
 * @author Schedule Manager
 * @version 1.0
 */
public class StatsPanel extends JPanel {
    private final ScheduleController controller;
    private JLabel quantAccuracyLabel;
    private JLabel tempAccuracyLabel;
    private JLabel plannedMinutesLabel;
    private JLabel actualMinutesLabel;
    private JTextArea insightsArea;
    
    /**
     * Constructs a StatsPanel.
     * 
     * @param controller the schedule controller
     */
    public StatsPanel(ScheduleController controller) {
        this.controller = controller;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Statistics & Insights"));
        setPreferredSize(new Dimension(0, 200));
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        quantAccuracyLabel = new JLabel("Quantitative Accuracy: -");
        tempAccuracyLabel = new JLabel("Temporal Accuracy: -");
        plannedMinutesLabel = new JLabel("Planned Minutes: -");
        actualMinutesLabel = new JLabel("Actual Minutes: -");
        
        statsPanel.add(quantAccuracyLabel);
        statsPanel.add(tempAccuracyLabel);
        statsPanel.add(plannedMinutesLabel);
        statsPanel.add(actualMinutesLabel);
        
        // Insights panel
        JPanel insightsPanel = new JPanel(new BorderLayout());
        insightsPanel.setBorder(BorderFactory.createTitledBorder("AI Insights"));
        
        insightsArea = new JTextArea(5, 50);
        insightsArea.setEditable(false);
        insightsArea.setWrapStyleWord(true);
        insightsArea.setLineWrap(true);
        JScrollPane insightsScroll = new JScrollPane(insightsArea);
        
        JButton generateButton = new JButton("Generate Insights");
        generateButton.addActionListener(e -> generateInsights());
        
        insightsPanel.add(insightsScroll, BorderLayout.CENTER);
        insightsPanel.add(generateButton, BorderLayout.SOUTH);
        
        add(statsPanel, BorderLayout.WEST);
        add(insightsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Refreshes the statistics display.
     */
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<DailyStatistics, Void>() {
                @Override
                protected DailyStatistics doInBackground() throws Exception {
                    return controller.getDailyStats(LocalDate.now());
                }
                
                @Override
                protected void done() {
                    try {
                        DailyStatistics stats = get();
                        updateStatsDisplay(stats);
                    } catch (Exception e) {
                        quantAccuracyLabel.setText("Quantitative Accuracy: Error");
                        tempAccuracyLabel.setText("Temporal Accuracy: Error");
                        plannedMinutesLabel.setText("Planned Minutes: Error");
                        actualMinutesLabel.setText("Actual Minutes: Error");
                    }
                }
            }.execute();
        });
    }
    
    /**
     * Updates the statistics display labels.
     * 
     * @param stats the daily statistics
     */
    private void updateStatsDisplay(DailyStatistics stats) {
        quantAccuracyLabel.setText(String.format("Quantitative Accuracy: %.2f%%",
            stats.getQuantitativeAccuracy() * 100));
        tempAccuracyLabel.setText(String.format("Temporal Accuracy: %.2f%%",
            stats.getTemporalAccuracy() * 100));
        plannedMinutesLabel.setText("Planned Minutes: " + stats.getPlannedMinutes());
        actualMinutesLabel.setText("Actual Minutes: " + stats.getActualMinutes());
    }
    
    /**
     * Generates AI insights for the current day.
     */
    private void generateInsights() {
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return controller.generateInsights(LocalDate.now());
                }
                
                @Override
                protected void done() {
                    try {
                        String insights = get();
                        insightsArea.setText(insights);
                    } catch (Exception e) {
                        insightsArea.setText("Error generating insights: " + e.getMessage());
                    }
                }
            }.execute();
        });
    }
}

