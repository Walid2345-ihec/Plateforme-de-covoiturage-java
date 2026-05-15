CREATE DATABASE IF NOT EXISTS covoiturage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE covoiturage;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins` (
  `cin` VARCHAR(64) NOT NULL,
  `nom` VARCHAR(255),
  `prenom` VARCHAR(255),
  `tel` VARCHAR(64),
  `annee_univ` INT,
  `adresse` TEXT,
  `mail` VARCHAR(255),
  `password_hash` VARCHAR(255),
  `role` VARCHAR(255),
  `date_creation` DATETIME,
  PRIMARY KEY (`cin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `admins` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `role`, `date_creation`) VALUES ('99999999', 'Admin', 'Principal', '00000000', 2024, '—', 'admin@gmail.com', 'admin123', 'SUPER_ADMIN', '2026-05-10 14:30:00');

DROP TABLE IF EXISTS `conducteurs`;
CREATE TABLE `conducteurs` (
  `cin` VARCHAR(64) NOT NULL,
  `nom` VARCHAR(255),
  `prenom` VARCHAR(255),
  `tel` VARCHAR(64),
  `annee_univ` INT,
  `adresse` TEXT,
  `mail` VARCHAR(255),
  `password_hash` VARCHAR(255),
  `nom_voiture` VARCHAR(255),
  `marque_voiture` VARCHAR(255),
  `matricule` VARCHAR(255),
  `places_disponibles` INT,
  `weekly_schedule` TEXT,
  `moyenne_evaluation` DECIMAL(10,2),
  `carte` VARCHAR(255),
  `banned` BOOLEAN,
  PRIMARY KEY (`cin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `conducteurs` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `nom_voiture`, `marque_voiture`, `matricule`, `places_disponibles`, `weekly_schedule`, `moyenne_evaluation`, `carte`, `banned`) VALUES ('15033214', 'Achour', 'Hsan', '27658629', 2024, '10 rue imam el ghazali,khaireddine la goulette', 'achourhassen28@gmail.com', 'f289cb57deb0d1cacb1b28ccd5009f3028a807b8b218eaeed1bb7a507b2b4af7', 'polo 7', 'wolvsvagen', '225TU7541', 3, 'MON:09:00-17:00|TUE:09:00-17:00|WED:09:00-17:00|THU:09:00-17:00|FRI:09:00-17:00', 0.00, 'jaune', 0);
INSERT INTO `conducteurs` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `nom_voiture`, `marque_voiture`, `matricule`, `places_disponibles`, `weekly_schedule`, `moyenne_evaluation`, `carte`, `banned`) VALUES ('25814763', 'Krayem', 'Naim', '25814763', 2024, '14 rue nefaa battikh ,rades', 'naim@ucar.tn', '4856b5f8ef566da76a03d6a2c695252072a92366f3e48e2b7dbe0ed2521bab7f', 'A5', 'audi', '147TU0357', 3, 'MON:08:00-16:00|TUE:08:00-16:00|WED:08:00-16:00|THU:08:00-16:00|FRI:08:00-16:00', 0.00, 'verte', 0);
INSERT INTO `conducteurs` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `nom_voiture`, `marque_voiture`, `matricule`, `places_disponibles`, `weekly_schedule`, `moyenne_evaluation`, `carte`, `banned`) VALUES ('11111111', 'Nasraoui', 'Fadi', '14725836', 2024, '15 rue fadhlaoui,rades', 'fadi@gmail.com', '29d2368a9110aab745b1fa3590382bcda2288ea51617b93c8f74dcccddabfa1b', 'A5', 'audi', '147TU9874', 3, 'MON:10:00-18:00|TUE:10:00-18:00|WED:10:00-18:00|THU:10:00-18:00|FRI:10:00-18:00|SAT:10:00-16:00', 0.00, 'verte', 0);
INSERT INTO `conducteurs` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `nom_voiture`, `marque_voiture`, `matricule`, `places_disponibles`, `weekly_schedule`, `moyenne_evaluation`, `carte`, `banned`) VALUES ('15032608', 'Se', 'Walid', '26987548', 2025, 'dd', 'walidsahebettaba@gmail.com', '7be13c98c221aa782b5c7a3753b0a16d2607ea49ee9d8e6b2a1fd1d37e48a3eb', 'gg', 'gg', '215TU3263', 3, 'MON:-||||||', 4.50, 'rouge', 0);
INSERT INTO `conducteurs` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `nom_voiture`, `marque_voiture`, `matricule`, `places_disponibles`, `weekly_schedule`, `moyenne_evaluation`, `carte`, `banned`) VALUES ('12345678', 'Loud', 'Sami', '13245678', 2023, 'sds s s', 'www@gmail.com', '7be13c98c221aa782b5c7a3753b0a16d2607ea49ee9d8e6b2a1fd1d37e48a3eb', 'C5', 'Citroen', '136TU2623', 2, 'MON:09:00-17:00|TUE:09:00-17:00|WED:09:00-17:00||||', 0.00, 'jaune', 0);

DROP TABLE IF EXISTS `conducteur_notifications`;
CREATE TABLE `conducteur_notifications` (
  `notification_id` VARCHAR(255) NOT NULL,
  `conducteur_id` VARCHAR(64),
  `passager_id` VARCHAR(64),
  `trajet_id` VARCHAR(255),
  `type` VARCHAR(255),
  `message` TEXT,
  `date_creation` DATETIME,
  `est_lue` BOOLEAN,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774971624939_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-03-31 16:40:24', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774971237336_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'REFUS', 'Refusé par tlili mokhtar pour le trajet kram → IHEC', '2026-03-31 16:33:57', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774970672241_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'REFUS', 'Refusé pour le trajet kram → IHEC', '2026-03-31 16:24:32', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775147594702_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'SUPPRESSION', 'Supprimé du trajet par tlili mokhtar (kram → IHEC)', '2026-04-02 17:33:14', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775150246954_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-02 18:17:26', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775151302204_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-02 18:35:02', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775430701937_41233051', '41233051', '47586912', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-06 00:11:41', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496482652_15032608', '12121212', '15032608', '15032608_La marsa_IHEC', 'DEMANDE', 'Nouvelle demande de dcc dd pour le trajet La marsa → IHEC', '2026-05-11 11:48:02', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496502311_15032608', '12121212', '15032608', NULL, 'MESSAGE', '📨 Message de dcc dd: bonjourrrr, najem nerkeb m3ak', '2026-05-11 11:48:22', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496579190_15032608', '13131313', '15032608', '15032608_La marsa_IHEC', 'DEMANDE', 'Nouvelle demande de ddf dds pour le trajet La marsa → IHEC', '2026-05-11 11:49:39', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496744462_15032608_GRPMSG', '12121212', '15032608', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] dcc dd: okk', '2026-05-11 11:52:24', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496760224_15032608_EVAL', '12121212', '15032608', NULL, 'EVALUATION', '⭐ dcc dd vous a évalué ★★★★☆ : thneya behya', '2026-05-11 11:52:40', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496812286_15032608_EVAL', '13131313', '15032608', NULL, 'EVALUATION', '⭐ ddf dds vous a évalué ★★★★★ : ddd', '2026-05-11 11:53:32', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778526234540_15032608', '13131313', '15032608', '15032608_s_s', 'DEMANDE', 'Nouvelle demande de ddf dds pour le trajet s → s', '2026-05-11 20:03:54', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778526273751_15032608', '13131313', '15032608', '15032608_La marsa_IHEC', 'ANNULATION', 'Annulation de réservation par ddf dds pour le trajet La marsa → IHEC', '2026-05-11 20:04:33', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778526293728_15032608', '13131313', '15032608', NULL, 'MESSAGE', '📨 Message de ddf dds: bch nerkbou lila?', '2026-05-11 20:04:53', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_CONV_1778613561749_15032608', '99999999', '15032608', NULL, 'MESSAGE', 'L''administrateur a ouvert une conversation avec vous.', '2026-05-12 20:19:21', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_MSG_1778613564161_15032608', '99999999', '15032608', NULL, 'MESSAGE', 'Message de l''administrateur: sd', '2026-05-12 20:19:24', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_MSG_1778671492436_15032608', '99999999', '15032608', NULL, 'MESSAGE', 'Message de l''administrateur: yaaatek asba', '2026-05-13 12:24:52', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778671559371_15032608', '12121212', '15032608', '15032608_s_s', 'DEMANDE', 'Nouvelle demande de dcc dd pour le trajet s → s', '2026-05-13 12:25:59', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775431482789_47586912', '47586912', '41233051', NULL, 'MESSAGE', '📨 Message de jaleli meriem: bonjour', '2026-04-06 00:24:42', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775431490068_47586912', '47586912', '41233051', NULL, 'MESSAGE', '📨 Message de jaleli meriem: je suis prés', '2026-04-06 00:24:50', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769584159_12345678', '12121212', '12345678', '12345678_Marsa_IHEC', 'DEMANDE', 'Nouvelle demande de dcc dd pour le trajet Marsa → IHEC', '2026-05-14 15:39:44', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769588845_12345678', '12121212', '12345678', '12345678_Marsa_IHEC', 'DEMANDE', 'Nouvelle demande de dcc dd pour le trajet Marsa → IHEC', '2026-05-14 15:39:48', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769681276_12345678', '13131313', '12345678', '12345678_Marsa_IHEC', 'DEMANDE', 'Nouvelle demande de ddf dds pour le trajet Marsa → IHEC', '2026-05-14 15:41:21', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_MSG_1778770040644_12345678', '99999999', '12345678', NULL, 'MESSAGE', 'Message de l''administrateur: appuyez sur le bttn', '2026-05-14 15:47:20', 1);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_CONV_1778610395555_15033214', '99999999', '15033214', NULL, 'MESSAGE', 'L''administrateur a ouvert une conversation avec vous.', '2026-05-12 19:26:35', 0);
INSERT INTO `conducteur_notifications` (`notification_id`, `conducteur_id`, `passager_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_MSG_1778610418991_15033214', '99999999', '15033214', NULL, 'MESSAGE', 'Message de l''administrateur: bonjour, m3adch tekel ou enti tsou9', '2026-05-12 19:26:58', 0);

DROP TABLE IF EXISTS `conversations`;
CREATE TABLE `conversations` (
  `id` VARCHAR(255) NOT NULL,
  `user_id` VARCHAR(64),
  `admin_id` VARCHAR(64),
  `triggered_by` VARCHAR(255),
  `created_at` DATETIME,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `conversations` (`id`, `user_id`, `admin_id`, `triggered_by`, `created_at`) VALUES ('CONV_1778525448270_15032608', '15032608', '99999999', 'reclamation', '2026-05-11 19:50:48');
INSERT INTO `conversations` (`id`, `user_id`, `admin_id`, `triggered_by`, `created_at`) VALUES ('CONV_1778525801660_13131313', '13131313', '99999999', 'help', '2026-05-11 19:56:41');
INSERT INTO `conversations` (`id`, `user_id`, `admin_id`, `triggered_by`, `created_at`) VALUES ('CONV_1778610395549_15033214', '15033214', '99999999', 'admin', '2026-05-12 19:26:35');
INSERT INTO `conversations` (`id`, `user_id`, `admin_id`, `triggered_by`, `created_at`) VALUES ('CONV_1778671622532_12121212', '12121212', '99999999', 'reclamation', '2026-05-13 12:27:02');
INSERT INTO `conversations` (`id`, `user_id`, `admin_id`, `triggered_by`, `created_at`) VALUES ('CONV_1778769444281_12345678', '12345678', '99999999', 'reclamation', '2026-05-14 15:37:24');

DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations` (
  `evaluation_id` VARCHAR(255) NOT NULL,
  `passager_cin` VARCHAR(64),
  `passager_name` VARCHAR(255),
  `conducteur_cin` VARCHAR(64),
  `trajet_id` VARCHAR(255),
  `rating` INT,
  `comment` TEXT,
  `date_creation` DATETIME,
  PRIMARY KEY (`evaluation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `evaluations` (`evaluation_id`, `passager_cin`, `passager_name`, `conducteur_cin`, `trajet_id`, `rating`, `comment`, `date_creation`) VALUES ('EVAL_1778496760223_12121212', '12121212', 'dcc dd', '15032608', '15032608_La marsa_IHEC', 4, 'thneya behya', '2026-05-11 11:52:40');
INSERT INTO `evaluations` (`evaluation_id`, `passager_cin`, `passager_name`, `conducteur_cin`, `trajet_id`, `rating`, `comment`, `date_creation`) VALUES ('EVAL_1778496812280_13131313', '13131313', 'ddf dds', '15032608', '15032608_La marsa_IHEC', 5, 'ddd', '2026-05-11 11:53:32');

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `group_id` VARCHAR(255) NOT NULL,
  `group_name` VARCHAR(255),
  `conducteur_cin` VARCHAR(64),
  `member_cins` TEXT,
  `date_creation` DATETIME,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `groups` (`group_id`, `group_name`, `conducteur_cin`, `member_cins`, `date_creation`) VALUES ('GRP_1778496676848_15032608', 'ihec cocovoit', '15032608', '12121212,13131313', '2026-05-11 11:51:16');
INSERT INTO `groups` (`group_id`, `group_name`, `conducteur_cin`, `member_cins`, `date_creation`) VALUES ('GRP_1778769759922_12345678', 'Covoiturage marsa->ihec', '12345678', '13131313', '2026-05-14 15:42:39');
INSERT INTO `groups` (`group_id`, `group_name`, `conducteur_cin`, `member_cins`, `date_creation`) VALUES ('GRP_1778769900303_12345678', 'cocovoit', '12345678', '12121212,13131313', '2026-05-14 15:45:00');

DROP TABLE IF EXISTS `group_messages`;
CREATE TABLE `group_messages` (
  `message_id` VARCHAR(255) NOT NULL,
  `group_id` VARCHAR(255),
  `sender_cin` VARCHAR(64),
  `sender_name` VARCHAR(255),
  `content` TEXT,
  `timestamp` DATETIME,
  `is_deleted` BOOLEAN,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `group_messages` (`message_id`, `group_id`, `sender_cin`, `sender_name`, `content`, `timestamp`, `is_deleted`) VALUES ('GMSG_1778496694606_15032608', 'GRP_1778496676848_15032608', '15032608', 'Se Walid', 'behi neselkol, trajet kol lundi', '2026-05-11 11:51:34', 0);
INSERT INTO `group_messages` (`message_id`, `group_id`, `sender_cin`, `sender_name`, `content`, `timestamp`, `is_deleted`) VALUES ('GMSG_1778496744462_12121212', 'GRP_1778496676848_15032608', '12121212', 'dcc dd', 'okk', '2026-05-11 11:52:24', 0);
INSERT INTO `group_messages` (`message_id`, `group_id`, `sender_cin`, `sender_name`, `content`, `timestamp`, `is_deleted`) VALUES ('GMSG_1778525571519_15032608', 'GRP_1778496676848_15032608', '15032608', 'Se Walid', 'sahitek', '2026-05-11 19:52:51', 0);
INSERT INTO `group_messages` (`message_id`, `group_id`, `sender_cin`, `sender_name`, `content`, `timestamp`, `is_deleted`) VALUES ('GMSG_1778769777356_12345678', 'GRP_1778769759922_12345678', '12345678', 'Loud Sami', 'bonjour, soyez prêts demain', '2026-05-14 15:42:57', 0);
INSERT INTO `group_messages` (`message_id`, `group_id`, `sender_cin`, `sender_name`, `content`, `timestamp`, `is_deleted`) VALUES ('GMSG_1778769913899_12345678', 'GRP_1778769900303_12345678', '12345678', 'Loud Sami', 'bonjour soyez à l''heure', '2026-05-14 15:45:13', 0);

DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `message_id` VARCHAR(255) NOT NULL,
  `sender_cin` VARCHAR(64),
  `sender_name` VARCHAR(255),
  `recipient_cin` VARCHAR(64),
  `recipient_name` VARCHAR(255),
  `content` TEXT,
  `timestamp` DATETIME,
  `is_deleted` BOOLEAN,
  `trajet_id` TEXT,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('c9026179-8137-4b65-b58e-bdb15235f755', '12121212', 'dcc dd', '15032608', 'Se Walid', 'bonjourrrr, najem nerkeb m3ak', '2026-05-11 11:48:22', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('93398555-51be-46a4-b5d7-7d0ca560b63b', '15032608', 'Se Walid', '12121212', 'dcc dd', 'jawek ahla jaw', '2026-05-11 11:50:27', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('c38c808c-9ed2-4f9f-a6c4-99e479e5c6d4', '15032608', 'Se Walid', '99999999', 'Admin Principal', 'i wanna know how to create trajet', '2026-05-11 19:51:07', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('227eec43-e5c2-4137-8560-86379207390f', '99999999', 'Admin Principal', '15032608', 'Se Walid', 'ok', '2026-05-11 19:51:38', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('00aaf0b9-7cf1-44ad-a55f-694548e7a669', '99999999', 'Admin Principal', '15032608', 'Se Walid', 'hh', '2026-05-11 19:51:52', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('d52fe38b-43b9-466d-a1ba-3a833cbc07f5', '99999999', 'Admin Principal', '15032608', 'Se Walid', 'go to create', '2026-05-11 19:51:58', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('cf764ade-477f-4d7d-b99d-704c604698ed', '15032608', 'Se Walid', '99999999', 'Admin Principal', 'Message supprimé', '2026-05-11 19:52:28', 1, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('86a6d406-570e-4174-8987-d763d81ef5c4', '15032608', 'Se Walid', '99999999', 'Admin Principal', 'Message supprimé', '2026-05-11 19:55:35', 1, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('b5677d5c-506c-46c6-8c3a-970b6ae7d576', '13131313', 'ddf dds', '99999999', 'Admin Principal', 'sra9li flousi', '2026-05-11 19:56:47', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('dfda6032-7238-4503-b69b-f7bb3e698232', '99999999', 'Admin Principal', '13131313', 'ddf dds', 'bch nbaniweh', '2026-05-11 19:57:20', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('c092408a-b2a6-4502-919b-44c9f60ceffa', '13131313', 'ddf dds', '15032608', 'Se Walid', 'bch nerkbou lila?', '2026-05-11 20:04:53', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('17cdfe0a-8ee5-4da6-bad7-4522c173dff1', '99999999', 'Admin Principal', '15033214', 'Achour Hsan', 'bonjour, m3adch tekel ou enti tsou9', '2026-05-12 19:26:58', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('837961ac-b91d-41aa-90f4-81c9d288e3f6', '99999999', 'Admin Principal', '15032608', 'Se Walid', 'Message supprimé', '2026-05-12 20:19:24', 1, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('09a2e213-29ec-4a5c-8921-8555dca2e6f1', '15032608', 'Se Walid', '99999999', 'Admin Principal', 'Message supprimé', '2026-05-13 12:23:54', 1, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('5bbe77e6-5346-4a1a-b87b-4d411be08db5', '99999999', 'Admin Principal', '15032608', 'Se Walid', 'Message supprimé', '2026-05-13 12:24:52', 1, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('502f6501-c8f0-4b7e-98da-b091926d4a48', '12345678', 'Loud Sami', '99999999', 'Admin Principal', 'bonjour comment report', '2026-05-14 15:46:13', 0, NULL);
INSERT INTO `messages` (`message_id`, `sender_cin`, `sender_name`, `recipient_cin`, `recipient_name`, `content`, `timestamp`, `is_deleted`, `trajet_id`) VALUES ('3da9257e-8c80-435f-935c-432e26f02570', '99999999', 'Admin Principal', '12345678', 'Loud Sami', 'appuyez sur le bttn', '2026-05-14 15:47:20', 0, NULL);

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `notification_id` VARCHAR(255) NOT NULL,
  `passager_id` VARCHAR(64),
  `conducteur_id` VARCHAR(64),
  `trajet_id` VARCHAR(255),
  `type` VARCHAR(255),
  `message` TEXT,
  `date_creation` DATETIME,
  `est_lue` BOOLEAN,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775431482789_47586912', '41233051', '47586912', NULL, 'MESSAGE', '📨 Message de jaleli meriem: bonjour', '2026-04-06 00:24:42', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775431490068_47586912', '41233051', '47586912', NULL, 'MESSAGE', '📨 Message de jaleli meriem: je suis prés', '2026-04-06 00:24:50', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496614904_12121212', '12121212', '15032608', '15032608_La marsa_IHEC', 'ACCEPTATION', 'Accepté par Se Walid pour le trajet La marsa → IHEC', '2026-05-11 11:50:14', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496627462_12121212', '12121212', '15032608', NULL, 'MESSAGE', '📨 Message de Se Walid: jawek ahla jaw', '2026-05-11 11:50:27', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496676848_12121212_GRP', '12121212', '15032608', 'GRP_1778496676848_15032608', 'GROUPE', '👥 Vous avez été ajouté au groupe « ihec cocovoit » par Se Walid', '2026-05-11 11:51:16', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496694607_12121212_GRPMSG', '12121212', '15032608', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] Se Walid: behi neselkol, trajet kol lundi', '2026-05-11 11:51:34', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778525571519_12121212_GRPMSG', '12121212', '15032608', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] Se Walid: sahitek', '2026-05-11 19:52:51', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778671705052_12121212', '12121212', '15032608', '15032608_s_s', 'ACCEPTATION', 'Accepté par Se Walid pour le trajet s → s', '2026-05-13 12:28:25', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769741162_12121212', '12121212', '12345678', '12345678_Marsa_IHEC', 'ACCEPTATION', 'Accepté par Loud Sami pour le trajet Marsa → IHEC', '2026-05-14 15:42:21', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769900303_12121212_GRP', '12121212', '12345678', 'GRP_1778769900303_12345678', 'GROUPE', '👥 Vous avez été ajouté au groupe « cocovoit » par Loud Sami', '2026-05-14 15:45:00', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769913899_12121212_GRPMSG', '12121212', '12345678', 'GRP_1778769900303_12345678', 'MESSAGE_GROUPE', '💬 [cocovoit] Loud Sami: bonjour soyez à l''heure', '2026-05-14 15:45:13', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496618603_13131313', '13131313', '15032608', '15032608_La marsa_IHEC', 'ACCEPTATION', 'Accepté par Se Walid pour le trajet La marsa → IHEC', '2026-05-11 11:50:18', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496676849_13131313_GRP', '13131313', '15032608', 'GRP_1778496676848_15032608', 'GROUPE', '👥 Vous avez été ajouté au groupe « ihec cocovoit » par Se Walid', '2026-05-11 11:51:16', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496694607_13131313_GRPMSG', '13131313', '15032608', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] Se Walid: behi neselkol, trajet kol lundi', '2026-05-11 11:51:34', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778496744462_13131313_GRPMSG', '13131313', '12121212', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] dcc dd: okk', '2026-05-11 11:52:24', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778525571520_13131313_GRPMSG', '13131313', '15032608', 'GRP_1778496676848_15032608', 'MESSAGE_GROUPE', '💬 [ihec cocovoit] Se Walid: sahitek', '2026-05-11 19:52:51', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_ADMIN_MSG_1778525840798_13131313', '13131313', '99999999', NULL, 'MESSAGE', 'Message de l''administrateur: bch nbaniweh', '2026-05-11 19:57:20', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778526350943_13131313', '13131313', '15032608', '15032608_s_s', 'ACCEPTATION', 'Accepté par Se Walid pour le trajet s → s', '2026-05-11 20:05:50', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769744079_13131313', '13131313', '12345678', '12345678_Marsa_IHEC', 'ACCEPTATION', 'Accepté par Loud Sami pour le trajet Marsa → IHEC', '2026-05-14 15:42:24', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769759923_13131313_GRP', '13131313', '12345678', 'GRP_1778769759922_12345678', 'GROUPE', '👥 Vous avez été ajouté au groupe « Covoiturage marsa->ihec » par Loud Sami', '2026-05-14 15:42:39', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769777356_13131313_GRPMSG', '13131313', '12345678', 'GRP_1778769759922_12345678', 'MESSAGE_GROUPE', '💬 [Covoiturage marsa->ihec] Loud Sami: bonjour, soyez prêts demain', '2026-05-14 15:42:57', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769900303_13131313_GRP', '13131313', '12345678', 'GRP_1778769900303_12345678', 'GROUPE', '👥 Vous avez été ajouté au groupe « cocovoit » par Loud Sami', '2026-05-14 15:45:00', 0);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1778769913899_13131313_GRPMSG', '13131313', '12345678', 'GRP_1778769900303_12345678', 'MESSAGE_GROUPE', '💬 [cocovoit] Loud Sami: bonjour soyez à l''heure', '2026-05-14 15:45:13', 0);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774971624939_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-03-31 16:40:24', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774971237336_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'REFUS', 'Refusé par tlili mokhtar pour le trajet kram → IHEC', '2026-03-31 16:33:57', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1774970672241_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'REFUS', 'Refusé pour le trajet kram → IHEC', '2026-03-31 16:24:32', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775147594702_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'SUPPRESSION', 'Supprimé du trajet par tlili mokhtar (kram → IHEC)', '2026-04-02 17:33:14', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775150246954_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-02 18:17:26', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775151302204_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-02 18:35:02', 1);
INSERT INTO `notifications` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('NOTIF_1775430701937_41233051', '47586912', '41233051', '41233051_kram_IHEC', 'ACCEPTATION', 'Accepté par tlili mokhtar pour le trajet kram → IHEC', '2026-04-06 00:11:41', 1);

DROP TABLE IF EXISTS `notifications_admin`;
CREATE TABLE `notifications_admin` (
  `notification_id` VARCHAR(255) NOT NULL,
  `passager_id` VARCHAR(255),
  `conducteur_id` VARCHAR(255),
  `trajet_id` TEXT,
  `type` VARCHAR(255),
  `message` TEXT,
  `date_creation` DATETIME,
  `est_lue` BOOLEAN,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778611382647', 'ADMIN', 'ADMIN', NULL, 'reclamation', 'Réclamation de Se Walid (conducteur) contre dcc dd (passager) — Réservation #15032608_La-marsa_IHEC_12121212 — le 2026-05-12 19:43:02', '2026-05-12 19:43:02', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778613508944', 'ADMIN', 'ADMIN', NULL, 'reclamation', 'Réclamation de Se Walid (conducteur) contre ddf dds (passager) — Réservation #15032608_s_s_13131313 — le 2026-05-12 20:18:28 — Motif: Annulation de dernière minute sans avertissement', '2026-05-12 20:18:28', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778671434181', 'ADMIN', 'ADMIN', NULL, 'MESSAGE', 'Message de Se Walid (CIN: 15032608): a9wa jaabou9', '2026-05-13 12:23:54', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778671622529', 'ADMIN', 'ADMIN', NULL, 'reclamation', 'Réclamation de dcc dd (passager) contre Se Walid (conducteur) — Réservation #15032608_s_s_12121212 — le 2026-05-13 12:27:02 — Motif: Conducteur absent au point de départ', '2026-05-13 12:27:02', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778769430041', 'ADMIN', 'ADMIN', NULL, 'REGISTRATION', 'Nouvel utilisateur (Conducteur) inscrit: Loud Sami (CIN: 12345678)', '2026-05-14 15:37:10', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778769444283', 'ADMIN', 'ADMIN', NULL, 'HELP', 'Aide demandée par Loud Sami (CIN: 12345678)', '2026-05-14 15:37:24', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778769448503', 'ADMIN', 'ADMIN', NULL, 'HELP', 'Aide demandée par Loud Sami (CIN: 12345678)', '2026-05-14 15:37:28', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778769960333', 'ADMIN', 'ADMIN', NULL, 'HELP', 'Aide demandée par Loud Sami (CIN: 12345678)', '2026-05-14 15:46:00', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778769973704', 'ADMIN', 'ADMIN', NULL, 'MESSAGE', 'Message de Loud Sami (CIN: 12345678): bonjour comment report', '2026-05-14 15:46:13', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778770070394', 'ADMIN', 'ADMIN', NULL, 'reclamation', 'Réclamation de Loud Sami (conducteur) contre dcc dd (passager) — Réservation #12345678_Marsa_IHEC_12121212 — le 2026-05-14 15:47:50 — Motif: Dégâts causés au véhicule', '2026-05-14 15:47:50', 1);
INSERT INTO `notifications_admin` (`notification_id`, `passager_id`, `conducteur_id`, `trajet_id`, `type`, `message`, `date_creation`, `est_lue`) VALUES ('ADMIN_NOTIF_1778770123995', 'ADMIN', 'ADMIN', NULL, 'reclamation', 'Réclamation de dcc dd (passager) contre Loud Sami (conducteur) — Réservation #12345678_Marsa_IHEC_12121212 — le 2026-05-14 15:48:43 — Motif: Véhicule non conforme à l''annonce', '2026-05-14 15:48:43', 1);

DROP TABLE IF EXISTS `passagers`;
CREATE TABLE `passagers` (
  `cin` VARCHAR(64) NOT NULL,
  `nom` VARCHAR(255),
  `prenom` VARCHAR(255),
  `tel` VARCHAR(64),
  `annee_univ` INT,
  `adresse` TEXT,
  `mail` VARCHAR(255),
  `password_hash` VARCHAR(255),
  `cherche_covoit` BOOLEAN,
  `carte` VARCHAR(255),
  `banned` BOOLEAN,
  PRIMARY KEY (`cin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `passagers` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `cherche_covoit`, `carte`, `banned`) VALUES ('15033211', 'Jebali', 'Mohamed', '29874326', 2025, '33 rue aboulkaccem echabi khaireddine la goulette', 'jebali.2025@ihec.ucar.tn', 'f48d7f8a28f641132a3e0cec02921fa5e37916ab2967035ac97ca8c685658c36', 0, 'verte', 0);
INSERT INTO `passagers` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `cherche_covoit`, `carte`, `banned`) VALUES ('14725869', 'Chelly', 'Achref', '14725869', 2024, '15 rue ebnou b attouta khaireddine la goulette', 'achref@gmail.com', '287710b6066bdf360b11dfb7e37de4453b9b240d1be50daaf40928265ba37b25', 1, 'verte', 0);
INSERT INTO `passagers` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `cherche_covoit`, `carte`, `banned`) VALUES ('47586912', 'jaleli', 'meriem', '45678912', 2025, 'kram', 'meriem@gmail.com', '2a8ac4bd5335607f455958d780149d32b9624223be9f145715161f7214f8aeb7', 0, 'verte', 0);
INSERT INTO `passagers` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `cherche_covoit`, `carte`, `banned`) VALUES ('12121212', 'dcc', 'dd', '12121212', 2022, 'ff', 'dd@gmail.com', '7be13c98c221aa782b5c7a3753b0a16d2607ea49ee9d8e6b2a1fd1d37e48a3eb', 0, 'jaune', 0);
INSERT INTO `passagers` (`cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `password_hash`, `cherche_covoit`, `carte`, `banned`) VALUES ('13131313', 'ddf', 'dds', '13131313', 2022, 'ff', 'd@gmail.com', '7be13c98c221aa782b5c7a3753b0a16d2607ea49ee9d8e6b2a1fd1d37e48a3eb', 0, 'verte', 0);

DROP TABLE IF EXISTS `reclamations`;
CREATE TABLE `reclamations` (
  `id` VARCHAR(255) NOT NULL,
  `reservation_id` VARCHAR(255),
  `complainant_id` VARCHAR(64),
  `complainant_role` VARCHAR(255),
  `accused_id` VARCHAR(64),
  `accused_role` VARCHAR(255),
  `preset` VARCHAR(255),
  `message` TEXT,
  `created_at` DATETIME,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778611382645_15032608', '15032608_La-marsa_IHEC_12121212', '15032608', 'conducteur', '12121212', 'passager', NULL, 'Réclamation de Se Walid (conducteur) contre dcc dd (passager) — Réservation #15032608_La-marsa_IHEC_12121212 — le 2026-05-12 19:43:02', '2026-05-12 19:43:02');
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778613508941_15032608', '15032608_s_s_13131313', '15032608', 'conducteur', '13131313', 'passager', 'ANNULATION_TARDIVE', 'Annulation de dernière minute sans avertissement', '2026-05-12 20:18:28');
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778671622529_12121212', '15032608_s_s_12121212', '12121212', 'passager', '15032608', 'conducteur', 'CONDUCTEUR_ABSENT', 'Conducteur absent au point de départ', '2026-05-13 12:27:02');
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778671751618_15032608', '15032608_s_s_12121212', '15032608', 'conducteur', '12121212', 'passager', NULL, 'khraaaaaaaaaaaaaaa', '2026-05-13 12:29:11');
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778770070394_12345678', '12345678_Marsa_IHEC_12121212', '12345678', 'conducteur', '12121212', 'passager', 'DEGATS_VEHICULE', 'Dégâts causés au véhicule', '2026-05-14 15:47:50');
INSERT INTO `reclamations` (`id`, `reservation_id`, `complainant_id`, `complainant_role`, `accused_id`, `accused_role`, `preset`, `message`, `created_at`) VALUES ('RECL_1778770123995_12121212', '12345678_Marsa_IHEC_12121212', '12121212', 'passager', '12345678', 'conducteur', 'VEHICULE_NON_CONFORME', 'Véhicule non conforme à l''annonce', '2026-05-14 15:48:43');

DROP TABLE IF EXISTS `trajets`;
CREATE TABLE `trajets` (
  `id` BIGINT AUTO_INCREMENT NOT NULL,
  `depart` VARCHAR(255) NOT NULL,
  `arrivee` VARCHAR(255),
  `duree_minutes` INT,
  `status` VARCHAR(255),
  `prix` DECIMAL(10,2),
  `conducteur_cin` VARCHAR(64),
  `passager_cin` VARCHAR(64),
  `max_places` INT,
  `accepted_cins` VARCHAR(255),
  `pending_cins` TEXT,
  `start_date_time` TEXT,
  `end_date_time` TEXT,
  `weekly_schedule` TEXT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 'khaireddine', 'IHEC', 20, 'IN_PROGRESS', 3.0, '15033214', '15033211', 4, '15033211', NULL, NULL, NULL, NULL);
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 'rades melyen', 'IHEC', 35, 'PENDING', 5.0, '11111111', NULL, 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 'kram', 'IHEC', 15, 'IN_PROGRESS', 3.0, NULL, '47586912', 4, '47586912', NULL, NULL, NULL, 'MON:7:30-17:00|TUE:7:30-17:00|WED:7:30-14:00|THU:7:30-12:00|FRI:7:30-16:00||');
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 'La marsa', 'IHEC', 10, 'FINISHED', 3.0, '15032608', '12121212', 4, '12121212', NULL, NULL, NULL, 'MON:8:00-15:00||||||');
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 's', 's', 30, 'IN_PROGRESS', 5.0, '15032608', '13131313', 4, '13131313,12121212', NULL, NULL, NULL, 'MON:-||||||');
INSERT INTO `trajets` (`id`, `depart`, `arrivee`, `duree_minutes`, `status`, `prix`, `conducteur_cin`, `passager_cin`, `max_places`, `accepted_cins`, `pending_cins`, `start_date_time`, `end_date_time`, `weekly_schedule`) VALUES (NULL, 'Marsa', 'IHEC', 10, 'IN_PROGRESS', 20.0, '12345678', '12121212', 4, '12121212,13131313', NULL, NULL, NULL, 'MON:09:00-17:00|TUE:09:00-17:00|WED:09:00-17:00||||');

SET FOREIGN_KEY_CHECKS=1;