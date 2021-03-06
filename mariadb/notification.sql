CREATE TABLE `push_api` (
 `api_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_key` varchar(50) DEFAULT NULL,
 `name` varchar(100) DEFAULT NULL,
 `hash_password_client` varchar(100) DEFAULT NULL,
 `hash_password_pusher` varchar(100) DEFAULT NULL,
 `blocked` tinyint(1) DEFAULT '0',
 `time_create` datetime DEFAULT NULL,
 `time_edit` datetime DEFAULT NULL,
 `user_create` bigint(20) DEFAULT NULL,
 `user_edit` bigint(20) DEFAULT NULL,
 `admin_create` bigint(20) DEFAULT NULL,
 `admin_edit` bigint(20) DEFAULT NULL,
 `ip_create` varchar(40) DEFAULT NULL,
 `ip_edit` varchar(40) DEFAULT NULL,
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_api_user` (
 `api_user_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `user_id` bigint(20) DEFAULT NULL,
 `time_create` datetime DEFAULT NULL,
 `time_edit` datetime DEFAULT NULL,
 `user_create` bigint(20) DEFAULT NULL,
 `user_edit` bigint(20) DEFAULT NULL,
 `admin_create` bigint(20) DEFAULT NULL,
 `admin_edit` bigint(20) DEFAULT NULL,
 `ip_create` varchar(48) DEFAULT NULL,
 `ip_edit` varchar(48) DEFAULT NULL,
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`api_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_client` (
 `client_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `device_id` varchar(40) DEFAULT NULL,
 `last_token` varchar(40) DEFAULT NULL,
 `last_ip` varchar(40) DEFAULT NULL,
 `connection` int(11) DEFAULT '0',
 `last_time` datetime DEFAULT NULL,
 `time_create` datetime DEFAULT NULL,
 `blocked` tinyint(1) DEFAULT '0',
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE TABLE `push_client_group` (
 `client_group_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `name` varchar(100) DEFAULT NULL,
 `group_key` varchar(50) DEFAULT NULL,
 `description` longtext,
 `blocked` tinyint(1) DEFAULT '0',
 `time_create` datetime DEFAULT NULL,
 `time_edit` datetime DEFAULT NULL,
 `ip_create` varchar(40) DEFAULT NULL,
 `ip_edit` varchar(40) DEFAULT NULL,
 `user_create` bigint(20) DEFAULT NULL,
 `user_edit` bigint(20) DEFAULT NULL,
 `admin_create` bigint(20) DEFAULT NULL,
 `admin_edit` bigint(20) DEFAULT NULL,
 `sort_order` int(11) DEFAULT NULL,
 `default_data` tinyint(4) DEFAULT NULL,
 `active` tinyint(4) DEFAULT NULL,
 PRIMARY KEY (`client_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_notification` (
 `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `device_id` varchar(40) DEFAULT NULL,
 `api_id` bigint(20) DEFAULT NULL,
 `client_group_id` bigint(20) DEFAULT '0',
 `title` text,
 `subtitle` text,
 `message` longtext,
 `ticker_text` text,
 `uri` text,
 `color` varchar(10) DEFAULT NULL,
 `vibrate` text,
 `click_action` text,
 `sound` text,
 `badge` text,
 `large_icon` longtext,
 `small_icon` longtext,
 `type` varchar(20) DEFAULT NULL,
 `misc_data` longtext,
 `time_create` datetime DEFAULT NULL,
 `is_read` tinyint(1) DEFAULT '0',
 `time_read` datetime DEFAULT NULL,
 `is_sent` tinyint(1) DEFAULT '0',
 `time_sent` datetime DEFAULT NULL,
 `expire` tinyint(1) DEFAULT '0',
 `expiration_time` datetime DEFAULT NULL,
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_pusher_address` (
 `pusher_address_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `address` varchar(40) DEFAULT NULL,
 application_name varchar(100) DEFAULT NULL, 
 application_version varchar(40) DEFAULT NULL, 
 user_agent varchar(200) DEFAULT NULL,
 `first_access` datetime(6) DEFAULT NULL,
 `last_access` datetime(6) DEFAULT NULL,
 `auth` varchar(100) DEFAULT NULL,
 `need_confirmation` tinyint(1) DEFAULT '1',
 `blocked` tinyint(1) DEFAULT '0',
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`pusher_address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_trash` (
 `trash_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `device_id` varchar(40) DEFAULT NULL,
 `client_group_id` bigint(20) NULL DEFAULT '0',
 `notification_id` bigint(20) DEFAULT NULL,
 `time_delete` datetime(6) NULL,
 PRIMARY KEY (`trash_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_user` (
 `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `username` varchar(100) DEFAULT NULL,
 `name` varchar(100) DEFAULT NULL,
 `gender` enum('M','W') DEFAULT 'M',
 `birth_place` varchar(100) DEFAULT NULL,
 `birth_day` date DEFAULT NULL,
 `phone` varchar(30) DEFAULT NULL,
 `email` varchar(100) DEFAULT NULL,
 `password` varchar(45) DEFAULT NULL,
 `password_initial` varchar(45) DEFAULT NULL,
 `auth` varchar(45) DEFAULT NULL,
 `address` varchar(255) DEFAULT NULL,
 `country_id` bigint(20) DEFAULT NULL,
 `state_id` bigint(20) DEFAULT NULL,
 `city_id` bigint(20) DEFAULT NULL,
 `religion_id` varchar(2) DEFAULT NULL,
 `time_create` datetime DEFAULT NULL,
 `time_edit` datetime DEFAULT NULL,
 `user_create` bigint(20) DEFAULT NULL,
 `user_edit` bigint(20) DEFAULT NULL,
 `admin_create` bigint(20) DEFAULT NULL,
 `admin_edit` bigint(20) DEFAULT NULL,
 `ip_create` varchar(45) DEFAULT NULL,
 `ip_edit` varchar(45) DEFAULT NULL,
 `blocked` tinyint(1) DEFAULT '0',
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`user_id`),
 UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
