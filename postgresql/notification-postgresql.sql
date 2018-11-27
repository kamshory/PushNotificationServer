create extension pgcrypto;

CREATE FUNCTION sha1(bytea) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN ENCODE(DIGEST($1, 'sha1'), 'hex');
END;
$_$;

CREATE TABLE push_api
(
	api_id bigserial, 
	api_key character varying(50) DEFAULT NULL, 
	hash_password_client character varying(100) DEFAULT NULL, 
	hash_password_pusher character varying(100) DEFAULT NULL, 
	blocked smallint DEFAULT 0, 
	time_create time without time zone NOT NULL,
	time_edit time without time zone NOT NULL,
	user_create bigint NOT NULL,
	user_edit bigint NOT NULL,
	admin_create bigint NOT NULL,
	admin_edit bigint NOT NULL,
	ip_create character varying(40) NOT NULL,
	ip_edit character varying(40) NOT NULL,
	active smallint DEFAULT 1, 
	CONSTRAINT api_id PRIMARY KEY (api_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_client (
	client_id bigserial,
	api_id bigint NOT NULL,
	device_id character varying(40) DEFAULT NULL,
	last_token character varying(40) DEFAULT NULL,
	last_ip character varying(40) DEFAULT NULL,
	connection int NOT NULL DEFAULT 0,
	last_time time without time zone DEFAULT NULL,
	time_create time without time zone NOT NULL,
	blocked smallint NOT NULL DEFAULT 0,
	active smallint NOT NULL DEFAULT '1',
	CONSTRAINT client_id PRIMARY KEY (client_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_client_group (
	client_group_id bigserial,
	api_id bigint NOT NULL,
	name character varying(100) DEFAULT NULL,
	group_key character varying(50) DEFAULT NULL,
	description text DEFAULT NULL,
	blocked smallint DEFAULT 0, 
	time_create time without time zone NOT NULL,
	time_edit time without time zone NOT NULL,
	ip_create character varying(40) NOT NULL,
	ip_edit character varying(40) NOT NULL,
	user_create bigint NOT NULL,
	user_edit bigint NOT NULL,
	admin_create bigint NOT NULL,
	admin_edit bigint NOT NULL,
	sort_order int NOT NULL,
	default_data smallint NOT NULL,
	active smallint NOT NULL,
	CONSTRAINT client_group_id PRIMARY KEY (client_group_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_notification (
	notification_id bigserial,
	device_id character varying(40) DEFAULT NULL,
	api_id bigint NOT NULL,
	client_group_id bigint NOT NULL DEFAULT 0,
	title text,
	subtitle text,
	message text,
	ticker_text text,
	uri text,
	color character varying(10) DEFAULT NULL,
	vibrate text,
	click_action text,
	sound text,
	badge text,
	large_icon text,
	small_icon text,
	type character varying(20) DEFAULT NULL,
	misc_data text,
	time_create time without time zone DEFAULT NULL,
	is_read smallint NOT NULL DEFAULT 0,
	time_read time without time zone DEFAULT NULL,
	is_sent smallint NOT NULL DEFAULT 0,
	time_sent time without time zone DEFAULT NULL,
	expire smallint NOT NULL DEFAULT 0,
	expiration_time time without time zone DEFAULT NULL,
	active smallint NOT NULL DEFAULT 1,
	CONSTRAINT notification_id PRIMARY KEY (notification_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_pusher_address (
	pusher_address_id bigserial,
	api_id bigint DEFAULT NULL,
	address character varying(40) DEFAULT NULL,
	first_access time without time zone DEFAULT NULL,
	last_access time without time zone DEFAULT NULL,
	need_confirmation smallint DEFAULT 1,
	blocked smallint DEFAULT 0,
	active smallint DEFAULT 1,
	CONSTRAINT pusher_address_id PRIMARY KEY (pusher_address_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_trash (
	trash_id bigserial,
	api_id bigint DEFAULT NULL,
	device_id character varying(40) DEFAULT NULL,
	client_group_id bigint NOT NULL DEFAULT 0,
	notification_id bigint DEFAULT NULL,
	time_delete time without time zone NOT NULL,
	CONSTRAINT trash_id PRIMARY KEY (trash_id)
) 
WITH (
  OIDS = FALSE
)
;
