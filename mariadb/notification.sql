CREATE TABLE `push_api` (
 `api_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_key` varchar(50) NOT NULL,
 `hash_password_client` varchar(100) DEFAULT NULL,
 `hash_password_pusher` varchar(100) DEFAULT NULL,
 `active` tinyint(1) NOT NULL DEFAULT '1',
 PRIMARY KEY (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_client` (
 `client_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) NOT NULL,
 `device_id` varchar(40) DEFAULT NULL,
 `last_token` varchar(40) DEFAULT NULL,
 `last_ip` varchar(40) DEFAULT NULL,
 `connection` int(11) NOT NULL DEFAULT '0',
 `last_time` datetime DEFAULT NULL,
 `time_create` datetime NOT NULL,
 `blocked` tinyint(1) NOT NULL DEFAULT '0',
 `active` tinyint(1) NOT NULL DEFAULT '1',
 PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_client_group` (
 `client_group_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) NOT NULL,
 `name` varchar(100) DEFAULT NULL,
 `group_key` varchar(50) DEFAULT NULL,
 `description` timestamp NULL DEFAULT NULL,
 `time_create` datetime NOT NULL,
 `time_edit` datetime NOT NULL,
 `ip_create` varchar(40) NOT NULL,
 `ip_edit` varchar(40) NOT NULL,
 `user_create` bigint(20) NOT NULL,
 `user_edit` bigint(20) NOT NULL,
 `admin_create` bigint(20) NOT NULL,
 `admin_edit` bigint(20) NOT NULL,
 `sort_order` int(11) NOT NULL,
 `default_data` tinyint(4) NOT NULL,
 `active` tinyint(4) NOT NULL,
 PRIMARY KEY (`client_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_notification` (
 `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `device_id` varchar(40) DEFAULT NULL,
 `api_id` bigint(20) NOT NULL,
 `client_group_id` bigint(20) NOT NULL DEFAULT '0',
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
 `time_create` datetime(6) DEFAULT NULL,
 `is_read` tinyint(1) NOT NULL DEFAULT '0',
 `time_read` datetime(6) DEFAULT NULL,
 `is_sent` tinyint(1) NOT NULL DEFAULT '0',
 `time_sent` datetime(6) DEFAULT NULL,
 `expire` tinyint(1) NOT NULL DEFAULT '0',
 `expiration_time` datetime(6) DEFAULT NULL,
 `active` tinyint(1) NOT NULL DEFAULT '1',
 PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_pusher_address` (
 `pusher_address_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `address` varchar(40) DEFAULT NULL,
 `first_access` datetime DEFAULT NULL,
 `last_access` datetime DEFAULT NULL,
 `need_confirmation` tinyint(1) DEFAULT '1',
 `blocked` tinyint(1) DEFAULT '0',
 `active` tinyint(1) DEFAULT '1',
 PRIMARY KEY (`pusher_address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `push_trash` (
 `trash_id` bigint(20) NOT NULL AUTO_INCREMENT,
 `api_id` bigint(20) DEFAULT NULL,
 `device_id` varchar(40) DEFAULT NULL,
 `client_group_id` bigint(20) NOT NULL DEFAULT '0',
 `notification_id` bigint(20) DEFAULT NULL,
 `time_delete` datetime NOT NULL,
 PRIMARY KEY (`trash_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
