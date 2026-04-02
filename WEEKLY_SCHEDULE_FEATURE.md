# Fonctionnalité: Agenda Hebdomadaire pour les Trajets

## 📋 Vue d'ensemble
Cette mise à jour ajoute la capacité aux conducteurs de gérer un **agenda hebdomadaire** complet pour leurs trajets. Chaque conducteur peut maintenant :
- Sélectionner les **jours de la semaine** disponibles pour le covoiturage
- Spécifier les **heures exactes de départ et retour** pour chaque jour

## 🔄 Modifications effectuées

### 1. **Base de données (CSV)**
- **conducteurs.csv**: Ajout d'une colonne `WeeklySchedule` pour stocker l'agenda hebdomadaire du conducteur
- **trajets.csv**: Ajout d'une colonne `WeeklySchedule` pour stocker les jours/heures disponibles pour chaque trajet

**Format du WeeklySchedule:**
```
MON:09:00-17:00|TUE:10:00-18:00|WED:09:00-17:00|...
```

### 2. **Classe Trajet.java**
- Champs existants utilisés:
  - `weeklySchedule`: Stocke le format "MON:09:00-17:00|TUE:10:00-18:00|..."
  - `startDateTime`: Date/heure de départ spécifique
  - `endDateTime`: Date/heure de retour spécifique

### 3. **Classe Conducteur.java**
- Champ existant utilisé:
  - `weeklySchedule`: L'agenda personnel du conducteur

### 4. **Panel Graphique EnhancedDriverPanel.java**
Modifications dans `createNewTrajetView()`:
- Ajout du **WeeklySchedulePanel** à l'interface de création de trajet
- Les conducteurs voient maintenant une interface complète avec:
  1. **Champs de base** (départ, arrivée, durée, prix)
  2. **Date/Heure de départ et retour** (via DateTimePickerPanel)
  3. **Agenda hebdomadaire** (via WeeklySchedulePanel)

### 5. **Service CSV (CSVDatabase.java)**
- **saveConducteurs()**: Ajout de la sauvegarde du champ `WeeklySchedule` pour les conducteurs
- **loadConducteurs()**: Chargement du champ `WeeklySchedule` avec rétrocompatibilité
- **saveTrajets()**: Ajout de la sauvegarde du champ `WeeklySchedule` pour les trajets
- **loadTrajets()**: Chargement du champ `WeeklySchedule` avec rétrocompatibilité

## 🎯 Comment utiliser

### Créer un trajet avec agenda hebdomadaire:
1. Accédez à **"Nouveau Trajet"** dans le panel conducteur
2. Remplissez les informations de base (départ, arrivée, etc.)
3. Dans la section **"📅 Horaires de la Semaine"**:
   - Sélectionnez les jours disponibles (lundi, mardi, etc.)
   - Entrez les heures de départ et retour pour chaque jour
4. Cliquez sur **"Créer le Trajet"**

### Format des données:
- **Jours**: Utilise les codes ISO (MON, TUE, WED, THU, FRI, SAT, SUN)
- **Heures**: Format 24h (HH:mm) - ex: "09:00", "17:00"
- **Séparateurs**: Utilise "|" pour séparer les jours

### Exemple de schedule:
```
MON:09:00-17:00|WED:10:00-18:00|FRI:09:00-17:00|SAT:14:00-20:00
```
Cela signifie:
- Lundi: 9h00 - 17h00
- Mercredi: 10h00 - 18h00
- Vendredi: 9h00 - 17h00
- Samedi: 14h00 - 20h00

## ✅ Rétrocompatibilité
Le code conserve la **rétrocompatibilité complète**:
- Les fichiers CSV existants sans la colonne `WeeklySchedule` sont chargés correctement
- Le champ est traité comme vide si non présent
- À la prochaine sauvegarde, les nouvelles colonnes seront ajoutées automatiquement

## 🛠️ Architecture technique

### Classes impliquées:
1. **Models/Trajet.java** - Stockage des données de trajet
2. **Models/Conducteur.java** - Stockage des données de conducteur
3. **GUI/WeeklySchedulePanel.java** - Interface pour l'agenda (existant)
4. **GUI/EnhancedDriverPanel.java** - Panel conducteur modifié
5. **Services/CSVDatabase.java** - Sauvegarde/chargement CSV modifié

### Flux de données:
```
Interface (WeeklySchedulePanel)
    ↓
getScheduleAsString() → "MON:09:00-17:00|..."
    ↓
Trajet.setWeeklySchedule()
    ↓
SaveTrajets() → CSV
    ↓
LoadTrajets() ← CSV
    ↓
Interface affiche les données
```

## 📝 Notes importantes
1. La validation de la sélection du schedule est obligatoire (au moins 1 jour doit être sélectionné)
2. Les heures de départ doivent être avant les heures de retour
3. Les données sont sauvegardées en UTF-8 pour supporter les caractères français
4. Aucune base de données externe requise - tout fonctionne avec des CSV

## 🐛 Debug/Test
- Compilé et testé: ✅ Pas d'erreurs de compilation
- Rétrocompatibilité: ✅ Code gère les anciens formats CSV
- Validation des données: ✅ Intégrée à la création de trajet

## 📦 Prochaines étapes (optionnel)
- Afficher le schedule dans la liste des trajets
- Permettre la modification du schedule après création
- Ajouter des statistiques d'utilisation du schedule
- Interface pour les passagers pour voir les horaires disponibles
