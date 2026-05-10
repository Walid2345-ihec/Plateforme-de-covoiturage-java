package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Group - Représente un groupe de covoiturage créé par un conducteur
 * Le conducteur sélectionne des passagers acceptés pour former un groupe
 * et tous peuvent ensuite communiquer ensemble dans une discussion de groupe.
 */
public class Group {

    private String groupId;
    private String groupName;
    private String conducteurCin;
    private List<String> memberCins;
    private LocalDateTime dateCreation;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Group(String groupId, String groupName, String conducteurCin,
                 List<String> memberCins, LocalDateTime dateCreation) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.conducteurCin = conducteurCin;
        this.memberCins = (memberCins != null) ? new ArrayList<>(memberCins) : new ArrayList<>();
        this.dateCreation = (dateCreation != null) ? dateCreation : LocalDateTime.now();
    }

    public Group(String groupId, String groupName, String conducteurCin, List<String> memberCins) {
        this(groupId, groupName, conducteurCin, memberCins, LocalDateTime.now());
    }

    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getConducteurCin() { return conducteurCin; }
    public List<String> getMemberCins() { return memberCins; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setConducteurCin(String conducteurCin) { this.conducteurCin = conducteurCin; }
    public void setMemberCins(List<String> memberCins) {
        this.memberCins = (memberCins != null) ? new ArrayList<>(memberCins) : new ArrayList<>();
    }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getDateCreationAsString() {
        return dateCreation != null ? dateCreation.format(DATE_FORMATTER) : "";
    }

    public static LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * Retourne la liste des CINs des membres séparés par des virgules.
     * Inclut le conducteur en première position pour faciliter le filtrage.
     */
    public String getMemberCinsAsString() {
        if (memberCins == null || memberCins.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memberCins.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(memberCins.get(i));
        }
        return sb.toString();
    }

    /**
     * Vérifie si un utilisateur (par CIN) appartient au groupe (membre passager ou conducteur).
     */
    public boolean containsUser(String cin) {
        if (cin == null) return false;
        if (cin.equals(conducteurCin)) return true;
        if (memberCins != null) {
            for (String c : memberCins) {
                if (cin.equals(c)) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Group{id='" + groupId + "', name='" + groupName +
                "', driver=" + conducteurCin + ", members=" + memberCins + "}";
    }
}
