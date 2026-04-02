package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * Weekly Schedule Panel for managing departure and return times for each day
 */
public class WeeklySchedulePanel extends JPanel {
    private static final String[] DAYS = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    private static final String[] DAY_CODES = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    
    private final Map<String, DaySchedule> daySchedules = new HashMap<>();
    private final JPanel daysPanel;
    
    public WeeklySchedulePanel() {
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("📅 Horaires de la Semaine");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.TEXT_DARK);
        add(titleLabel, BorderLayout.NORTH);
        
        // Days panel with scroll
        daysPanel = new JPanel();
        daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS));
        daysPanel.setBackground(Colors.SURFACE);
        daysPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Create day schedule rows
        for (int i = 0; i < DAYS.length; i++) {
            DaySchedule daySchedule = new DaySchedule(DAYS[i], DAY_CODES[i]);
            daySchedules.put(DAY_CODES[i], daySchedule);
            daysPanel.add(daySchedule);
            daysPanel.add(Box.createVerticalStrut(10));
        }
        
        daysPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(daysPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Colors.SURFACE);
        scrollPane.getViewport().setBackground(Colors.SURFACE);
        ModernUIComponents.applyModernScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Get schedule for all days
     * Returns a map with day codes as keys and "HH:mm-HH:mm" as values
     */
    public Map<String, String> getSchedule() {
        Map<String, String> schedule = new HashMap<>();
        for (Map.Entry<String, DaySchedule> entry : daySchedules.entrySet()) {
            String dayCode = entry.getKey();
            DaySchedule daySchedule = entry.getValue();
            if (daySchedule.isEnabled()) {
                String departTime = daySchedule.getDepartureTime();
                String returnTime = daySchedule.getReturnTime();
                schedule.put(dayCode, departTime + "-" + returnTime);
            }
        }
        return schedule;
    }
    
    /**
     * Set schedule from map
     */
    public void setSchedule(Map<String, String> schedule) {
        if (schedule == null) return;
        
        for (Map.Entry<String, String> entry : schedule.entrySet()) {
            String dayCode = entry.getKey();
            String timeRange = entry.getValue();
            
            DaySchedule daySchedule = daySchedules.get(dayCode);
            if (daySchedule != null && timeRange != null && !timeRange.isEmpty()) {
                String[] times = timeRange.split("-");
                if (times.length == 2) {
                    daySchedule.setEnabled(true);
                    daySchedule.setDepartureTime(times[0].trim());
                    daySchedule.setReturnTime(times[1].trim());
                }
            }
        }
    }
    
    /**
     * Get schedule as string (for CSV storage)
     */
    public String getScheduleAsString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String dayCode : DAY_CODES) {
            if (!first) sb.append("|");
            DaySchedule daySchedule = daySchedules.get(dayCode);
            if (daySchedule.isEnabled()) {
                sb.append(dayCode).append(":")
                  .append(daySchedule.getDepartureTime()).append("-")
                  .append(daySchedule.getReturnTime());
            }
            first = false;
        }
        return sb.toString();
    }
    
    /**
     * Parse schedule from string (for CSV loading)
     */
    public void setScheduleFromString(String scheduleString) {
        if (scheduleString == null || scheduleString.isEmpty()) return;
        
        String[] dayEntries = scheduleString.split("\\|");
        for (String entry : dayEntries) {
            if (entry.contains(":")) {
                String[] parts = entry.split(":");
                String dayCode = parts[0].trim();
                String times = parts[1];
                String[] timeRange = times.split("-");
                
                if (timeRange.length == 2) {
                    DaySchedule daySchedule = daySchedules.get(dayCode);
                    if (daySchedule != null) {
                        daySchedule.setEnabled(true);
                        daySchedule.setDepartureTime(timeRange[0].trim());
                        daySchedule.setReturnTime(timeRange[1].trim());
                    }
                }
            }
        }
    }
    
    /**
     * Clear all schedules
     */
    public void clearSchedule() {
        for (DaySchedule daySchedule : daySchedules.values()) {
            daySchedule.setEnabled(false);
            daySchedule.setDepartureTime("09:00");
            daySchedule.setReturnTime("17:00");
        }
    }
    
    // ==================== Inner Class: Day Schedule ====================
    
    private static class DaySchedule extends JPanel {
        private final String dayName;
        private final String dayCode;
        private final JCheckBox enableCheckBox;
        private final ModernUIComponents.ModernTextField departureField;
        private final ModernUIComponents.ModernTextField returnField;
        
        public DaySchedule(String dayName, String dayCode) {
            this.dayName = dayName;
            this.dayCode = dayCode;
            
            setLayout(new GridBagLayout());
            setBackground(Colors.SURFACE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200, 100)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Day name and checkbox
            gbc.gridx = 0;
            gbc.weightx = 0.1;
            enableCheckBox = new JCheckBox(dayName);
            enableCheckBox.setBackground(Colors.SURFACE);
            enableCheckBox.setFont(Fonts.BODY_BOLD);
            enableCheckBox.setSelected(false);
            enableCheckBox.addActionListener(e -> updateFields());
            add(enableCheckBox, gbc);
            
            // Departure time
            gbc.gridx = 1;
            gbc.weightx = 0.3;
            JLabel departLabel = new JLabel("Départ:");
            departLabel.setForeground(Colors.TEXT_DARK);
            departLabel.setFont(Fonts.BODY);
            add(departLabel, gbc);
            
            gbc.gridx = 2;
            gbc.weightx = 0.2;
            departureField = new ModernUIComponents.ModernTextField("09:00");
            departureField.setPreferredSize(new Dimension(80, 35));
            departureField.setEnabled(false);
            add(departureField, gbc);
            
            // Return time
            gbc.gridx = 3;
            gbc.weightx = 0.3;
            JLabel returnLabel = new JLabel("Retour:");
            returnLabel.setForeground(Colors.TEXT_DARK);
            returnLabel.setFont(Fonts.BODY);
            add(returnLabel, gbc);
            
            gbc.gridx = 4;
            gbc.weightx = 0.2;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            returnField = new ModernUIComponents.ModernTextField("17:00");
            returnField.setPreferredSize(new Dimension(80, 35));
            returnField.setEnabled(false);
            add(returnField, gbc);
        }
        
        private void updateFields() {
            boolean enabled = enableCheckBox.isSelected();
            departureField.setEnabled(enabled);
            returnField.setEnabled(enabled);
        }
        
        @Override
        public boolean isEnabled() {
            return enableCheckBox.isSelected();
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            enableCheckBox.setSelected(enabled);
            updateFields();
        }
        
        public String getDepartureTime() {
            return departureField.getText().trim();
        }
        
        public void setDepartureTime(String time) {
            departureField.setText(time);
        }
        
        public String getReturnTime() {
            return returnField.getText().trim();
        }
        
        public void setReturnTime(String time) {
            returnField.setText(time);
        }
    }
}
