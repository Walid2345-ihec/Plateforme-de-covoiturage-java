# Import MySQL

`mysql -u root -p < db/schema_import.sql`

- admins: 1 lignes, PK cin
- conducteurs: 5 lignes, PK cin
- conducteur_notifications: 28 lignes, PK notification_id
- conversations: 5 lignes, PK id
- evaluations: 2 lignes, PK evaluation_id
- groups: 3 lignes, PK group_id
- group_messages: 5 lignes, PK message_id
- messages: 17 lignes, PK message_id
- notifications: 30 lignes, PK notification_id
- notifications_admin: 11 lignes, PK notification_id
- passagers: 5 lignes, PK cin
- reclamations: 6 lignes, PK id
- trajets: 6 lignes, PK id