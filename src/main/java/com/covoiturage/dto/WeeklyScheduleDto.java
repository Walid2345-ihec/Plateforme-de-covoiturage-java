package com.covoiturage.dto;

import lombok.Data;

@Data
public class WeeklyScheduleDto {
    private boolean lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche;
    private String heureDepartLundi, heureRetourLundi;
    private String heureDepartMardi, heureRetourMardi;
    private String heureDepartMercredi, heureRetourMercredi;
    private String heureDepartJeudi, heureRetourJeudi;
    private String heureDepartVendredi, heureRetourVendredi;
    private String heureDepartSamedi, heureRetourSamedi;
    private String heureDepartDimanche, heureRetourDimanche;

    // Source Swing : WeeklySchedulePanel.java -> format MON:09:00-17:00|TUE:09:00-17:00
    public String toScheduleString() {
        StringBuilder sb = new StringBuilder();
        append(sb, lundi, "MON", heureDepartLundi, heureRetourLundi);
        append(sb, mardi, "TUE", heureDepartMardi, heureRetourMardi);
        append(sb, mercredi, "WED", heureDepartMercredi, heureRetourMercredi);
        append(sb, jeudi, "THU", heureDepartJeudi, heureRetourJeudi);
        append(sb, vendredi, "FRI", heureDepartVendredi, heureRetourVendredi);
        append(sb, samedi, "SAT", heureDepartSamedi, heureRetourSamedi);
        append(sb, dimanche, "SUN", heureDepartDimanche, heureRetourDimanche);
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    private void append(StringBuilder sb, boolean active, String day, String depart, String retour) {
        if (active && depart != null && !depart.isBlank() && retour != null && !retour.isBlank()) {
            sb.append(day).append(":").append(depart).append("-").append(retour).append("|");
        }
    }
}
