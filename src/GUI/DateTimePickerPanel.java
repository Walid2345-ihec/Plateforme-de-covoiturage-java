package GUI;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import javax.swing.*;

/**
 * DateTimePickerPanel - A modern date and time picker component for Swing
 * Provides an intuitive interface for selecting a specific date and time
 */
public class DateTimePickerPanel extends JPanel {
    
    private LocalDateTime selectedDateTime;
    private JSpinner dateSpinner;
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private Calendar calendar;
    
    public DateTimePickerPanel() {
        this(LocalDateTime.now());
    }
    
    public DateTimePickerPanel(LocalDateTime initialDateTime) {
        this.selectedDateTime = initialDateTime != null ? initialDateTime : LocalDateTime.now();
        this.calendar = Calendar.getInstance();
        this.calendar.set(
            this.selectedDateTime.getYear(),
            this.selectedDateTime.getMonthValue() - 1,
            this.selectedDateTime.getDayOfMonth(),
            this.selectedDateTime.getHour(),
            this.selectedDateTime.getMinute()
        );
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new GridBagLayout());
        setBackground(StyleUtils.BACKGROUND_COLOR);
        setOpaque(true);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = new JLabel("📅 Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        add(dateLabel, gbc);
        
        // Date Spinner
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel(
            calendar.getTime(),
            null,
            null,
            Calendar.DAY_OF_MONTH
        );
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(150, 35));
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateSpinner.addChangeListener(e -> updateDateTime());
        add(dateSpinner, gbc);
        
        // Hour Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel hourLabel = new JLabel("⏱ Heure:");
        hourLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hourLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        add(hourLabel, gbc);
        
        // Hour Spinner
        gbc.gridx = 1;
        hourSpinner = new JSpinner(new SpinnerNumberModel(
            this.selectedDateTime.getHour(),
            0, 23, 1
        ));
        hourSpinner.setPreferredSize(new Dimension(60, 35));
        hourSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hourSpinner.addChangeListener(e -> updateDateTime());
        add(hourSpinner, gbc);
        
        // Minute Label
        gbc.gridx = 2;
        JLabel minuteLabel = new JLabel("Minutes:");
        minuteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        minuteLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        add(minuteLabel, gbc);
        
        // Minute Spinner
        gbc.gridx = 3;
        minuteSpinner = new JSpinner(new SpinnerNumberModel(
            this.selectedDateTime.getMinute(),
            0, 59, 5
        ));
        minuteSpinner.setPreferredSize(new Dimension(60, 35));
        minuteSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        minuteSpinner.addChangeListener(e -> updateDateTime());
        add(minuteSpinner, gbc);
    }
    
    private void updateDateTime() {
        try {
            // Get date from spinner
            java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateValue);
            
            // Get time from spinners
            int hour = (Integer) hourSpinner.getValue();
            int minute = (Integer) minuteSpinner.getValue();
            
            // Create LocalDateTime
            this.selectedDateTime = LocalDateTime.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                hour,
                minute
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la date/heure: " + e.getMessage());
        }
    }
    
    /**
     * Gets the currently selected date and time
     */
    public LocalDateTime getSelectedDateTime() {
        updateDateTime();
        return this.selectedDateTime;
    }
    
    /**
     * Sets the date and time in the pickers
     */
    public void setSelectedDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.selectedDateTime = dateTime;
            
            // Update date spinner
            Calendar cal = Calendar.getInstance();
            cal.set(
                dateTime.getYear(),
                dateTime.getMonthValue() - 1,
                dateTime.getDayOfMonth()
            );
            dateSpinner.setValue(cal.getTime());
            
            // Update time spinners
            hourSpinner.setValue(dateTime.getHour());
            minuteSpinner.setValue(dateTime.getMinute());
        }
    }
    
    /**
     * Gets a formatted string representation of the selected date and time
     */
    public String getFormattedDateTime() {
        return this.selectedDateTime.format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
    
    /**
     * Validates that the selected date is not in the past
     */
    public boolean isDateTimeInFuture() {
        return this.selectedDateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Validates that the provided date is after this picker's selected date
     */
    public boolean isBeforeDateTime(LocalDateTime other) {
        if (other == null) return false;
        return this.selectedDateTime.isBefore(other);
    }
}
