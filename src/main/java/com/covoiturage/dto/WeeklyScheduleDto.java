package com.covoiturage.dto;

import java.time.LocalTime;

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
        return toScheduleString(null, null);
    }

    public String toScheduleString(LocalTime defaultDepart, LocalTime defaultRetour) {
        StringBuilder sb = new StringBuilder();
        String fallbackDepart = defaultDepart == null ? firstText(heureDepartLundi, heureDepartMardi, heureDepartMercredi, heureDepartJeudi, heureDepartVendredi, heureDepartSamedi, heureDepartDimanche) : defaultDepart.toString();
        String fallbackRetour = defaultRetour == null ? firstText(heureRetourLundi, heureRetourMardi, heureRetourMercredi, heureRetourJeudi, heureRetourVendredi, heureRetourSamedi, heureRetourDimanche) : defaultRetour.toString();
        append(sb, lundi, "MON", heureDepartLundi, heureRetourLundi, fallbackDepart, fallbackRetour);
        append(sb, mardi, "TUE", heureDepartMardi, heureRetourMardi, fallbackDepart, fallbackRetour);
        append(sb, mercredi, "WED", heureDepartMercredi, heureRetourMercredi, fallbackDepart, fallbackRetour);
        append(sb, jeudi, "THU", heureDepartJeudi, heureRetourJeudi, fallbackDepart, fallbackRetour);
        append(sb, vendredi, "FRI", heureDepartVendredi, heureRetourVendredi, fallbackDepart, fallbackRetour);
        append(sb, samedi, "SAT", heureDepartSamedi, heureRetourSamedi, fallbackDepart, fallbackRetour);
        append(sb, dimanche, "SUN", heureDepartDimanche, heureRetourDimanche, fallbackDepart, fallbackRetour);
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    private void append(StringBuilder sb, boolean active, String day, String depart, String retour, String fallbackDepart, String fallbackRetour) {
        if (!active) {
            return;
        }
        String actualDepart = hasText(depart) ? depart : fallbackDepart;
        String actualRetour = hasText(retour) ? retour : fallbackRetour;
        if (hasText(actualDepart) && hasText(actualRetour)) {
            sb.append(day).append(":").append(actualDepart).append("-").append(actualRetour).append("|");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return "";
    }
}
