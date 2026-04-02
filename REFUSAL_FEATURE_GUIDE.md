# Guide de Fonctionnalité : Refus de Demandes de Passagers

## 📋 Vue d'ensemble
Cette nouvelle fonctionnalité permet aux conducteurs de refuser les demandes de passagers avec une confirmation visuelle et d'envoyer automatiquement une notification d'inacceptation au passager.

## ✨ Fonctionnalités Principales

### 1. Bouton "Refuse" dans l'Interface
- **Localisation** : Page "Demandes Reçues" du conducteur
- **Position** : À côté des boutons "Accepter", "Voir Plus" et "Actualiser"
- **Couleur** : Rouge corail (Colors.ACCENT_CORAL)
- **Action** : Ouvre un dialogue de confirmation

### 2. Dialogue de Confirmation
Quand le conducteur clique sur "Refuse":
- Une fenêtre modale s'affiche avec le message: **"Êtes-vous sûr de refuser ce passager ?"**
- Affiche les informations du passager:
  - Nom et Prénom
  - Trajet (Départ → Arrivée)
- Contient une note: *"Une notification d'inacceptation sera envoyée au passager"*

### 3. Options de Confirmation
Le dialogue propose deux boutons:

#### 🗑️ Bouton "Supprimer"
- Supprime la demande du passager de la liste
- Envoie automatiquement une notification au passager
- Rafraîchit les tables (demandes et tableau de bord)
- Affiche un message de succès

#### ❌ Bouton "Annuler"
- Ferme le dialogue sans effectuer aucune action
- Aucune modification n'est apportée

## 🔧 Modifications de Codes

### 1. Classe `Passager.java` (Models)
```java
// Nouvelles propriétés
private Vector<String> notifications = new Vector<>();

// Nouvelles méthodes
public void addNotification(String notificationMessage)
public String getLastNotification()
public boolean hasNotifications()
public void clearNotifications()
public Vector<String> getNotifications()
```

### 2. Classe `Gestion_covoiturage.java` (Services)
```java
// Nouvelle méthode publique
public boolean refuser_passager_pour_trajet(Trajet t, String cinPassager)
```
Cette méthode:
- Supprime le passager de la liste des demandes du trajet
- Crée une notification d'inacceptation pour le passager
- Met à jour les mappings de demandes
- Remet le passager en recherche de covoiturage

### 3. Classe `EnhancedDriverPanel.java` (GUI)
```java
// Nouveau bouton dans createDemandesView()
ModernUIComponents.RoundedButton refuseBtn = new ModernUIComponents.RoundedButton(
    "Refuse", Colors.ACCENT_CORAL);

// Nouvelle méthode privée
private void refusePassenger()
private void showRefuseConfirmationDialog(Passager passager, Trajet trajet)
```

## 📊 Flux d'Exécution

```
Conducteur clique sur "Refuse"
    ↓
Dialogue de confirmation s'affiche
    ↓
Conducteur choisit:
    ├─→ "Supprimer" 
    │    ├─→ Appel refuser_passager_pour_trajet()
    │    ├─→ Notification créée
    │    ├─→ Tables rafraîchies
    │    ├─→ Message de succès
    │    └─→ Dialogue fermé
    │
    └─→ "Annuler"
         └─→ Dialogue fermé (aucune action)
```

## 📝 Notification du Passager

Quand une demande est refusée, le passager reçoit une notification:

**Message de notification :**
```
"Votre demande pour le trajet [Départ] → [Arrivée] a été refusée par le conducteur."
```

**Accès aux notifications :**
- Les notifications sont stockées dans la classe `Passager`
- Le passager peut consulter ses notifications en visitant sa page "Mes Notifications"
- Les notifications persistent jusqu'à ce qu'elles soient supprimées

## 🎨 Détails Visuels

### Dialogue de Confirmation
- Taille: 500x350 pixels
- Icône: ⚠️ (avertissement)
- Titre: "Confirmation de Refus"
- Composants:
  - Icône d'avertissement (48px)
  - Question en gras
  - Informations du passager et du trajet (police SMALL)
  - Note en italique (couleur ACCENT_CORAL)
  - Boutons "Supprimer" (rouge) et "Annuler" (gris)

### Bouton "Refuse"
- Taille: 130x42 pixels
- Couleur: ACCENT_CORAL (rouge)
- Pos: À côté du bouton "Actualiser"

## 🧪 Test de la Fonctionnalité

1. **Connectez-vous** en tant que conducteur
2. **Créez un trajet** (si vous n'en n'avez pas)
3. **Allez à "Demandes Reçues"**
4. **Sélectionnez une demande** et cliquez sur "Refuse"
5. **Confirmez** en cliquant "Supprimer"
6. **Vérifiez** que:
   - La demande disparaît de la table
   - Le tableau de bord se met à jour
   - Le passager reçoit une notification

## 🔐 Sécurité et Intégrité des Données

- Les CINs sont masqués pour la confidentialité
- Les notifications sont stockées au niveau du passager
- La suppression de demande met à jour tous les mappings
- Les places du conducteur ne sont pas affectées (puisque le passager n'a pas été accepté)

## 📱 Intégration avec les Notifications Existantes

Le système de notifications du passager peut être consulté par:
- L'interface utilisateur du passager (à implémenter)
- Les API de récupération de notifications
- Les fichiers CSV de sauvegarde

## 🚀 Déploiement

Pour utiliser cette nouvelle fonctionnalité:
1. Recompiler le projet: `compile.bat`
2. Lancer l'application: `run.bat`
3. Connectez-vous en tant que conducteur
4. Accédez à la page "Demandes Reçues"
5. Utilisez le nouveau bouton "Refuse"

---

**Version:** 1.0  
**Date:** 31 Mars 2026  
**Auteur:** Système d'IA  
**Status:** ✅ Fonctionnel et Compilé
