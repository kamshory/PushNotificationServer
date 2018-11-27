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
	time_create time without time zone NULL,
	time_edit time without time zone NULL,
	user_create bigint NULL,
	user_edit bigint NULL,
	admin_create bigint NULL,
	admin_edit bigint NULL,
	ip_create character varying(40) NULL,
	ip_edit character varying(40) NULL,
	active smallint DEFAULT 1, 
	CONSTRAINT api_id PRIMARY KEY (api_id),
	CONSTRAINT push_api_api_key_key UNIQUE (api_key)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_client (
	client_id bigserial,
	api_id bigint NULL,
	device_id character varying(40) DEFAULT NULL,
	last_token character varying(40) DEFAULT NULL,
	last_ip character varying(40) DEFAULT NULL,
	connection int NULL DEFAULT 0,
	last_time time without time zone DEFAULT NULL,
	time_create time without time zone NULL,
	blocked smallint NULL DEFAULT 0,
	active smallint NULL DEFAULT '1',
	CONSTRAINT client_id PRIMARY KEY (client_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_client_group (
	client_group_id bigserial,
	api_id bigint NULL,
	name character varying(100) DEFAULT NULL,
	group_key character varying(50) DEFAULT NULL,
	description text DEFAULT NULL,
	blocked smallint DEFAULT 0, 
	time_create time without time zone NULL,
	time_edit time without time zone NULL,
	ip_create character varying(40) NULL,
	ip_edit character varying(40) NULL,
	user_create bigint NULL,
	user_edit bigint NULL,
	admin_create bigint NULL,
	admin_edit bigint NULL,
	sort_order int NULL,
	default_data smallint NULL,
	active smallint NULL,
	CONSTRAINT client_group_id PRIMARY KEY (client_group_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_notification (
	notification_id bigserial,
	device_id character varying(40) DEFAULT NULL,
	api_id bigint NULL,
	client_group_id bigint NULL DEFAULT 0,
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
	is_read smallint NULL DEFAULT 0,
	time_read time without time zone DEFAULT NULL,
	is_sent smallint NULL DEFAULT 0,
	time_sent time without time zone DEFAULT NULL,
	expire smallint NULL DEFAULT 0,
	expiration_time time without time zone DEFAULT NULL,
	active smallint NULL DEFAULT 1,
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
	application_name character varying(100) DEFAULT NULL, 
	application_version character varying(40) DEFAULT NULL, 
	user_agent character varying(200) DEFAULT NULL,
	first_access time without time zone DEFAULT NULL,
	last_access time without time zone DEFAULT NULL,
	auth character varying(100) NULL,
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
	client_group_id bigint NULL DEFAULT 0,
	notification_id bigint DEFAULT NULL,
	time_delete time without time zone NULL,
	CONSTRAINT trash_id PRIMARY KEY (trash_id)
) 
WITH (
  OIDS = FALSE
)
;

CREATE TABLE push_user
(
  user_id bigint NOT NULL DEFAULT nextval('push_user_user_id_seq'::regclass),
  api_id bigint,
  reg_number character varying(20) DEFAULT NULL::character varying,
  reg_number_national character varying(20) DEFAULT NULL::character varying,
  username character varying(100) NULL,
  name character varying(100) DEFAULT NULL::character varying,
  gender character varying(2) DEFAULT 'M'::character varying,
  birth_place character varying(100) DEFAULT NULL::character varying,
  birth_day date,
  phone character varying(30) DEFAULT NULL::character varying,
  email character varying(100) DEFAULT NULL::character varying,
  password character varying(45) DEFAULT NULL::character varying,
  password_initial character varying(45) DEFAULT NULL::character varying,
  auth character varying(45) DEFAULT NULL::character varying,
  address character varying(255) DEFAULT NULL::character varying,
  country_id bigint NULL,
  state_id bigint NULL,
  city_id bigint NULL,
  religion_id character varying(2) DEFAULT NULL::character varying,
  time_create timestamp without time zone,
  time_edit timestamp without time zone,
  user_create bigint,
  user_edit bigint,
  admin_create bigint,
  admin_edit bigint,
  ip_create character varying(45) DEFAULT NULL::character varying,
  ip_edit character varying(45) DEFAULT NULL::character varying,
  blocked smallint DEFAULT '0'::smallint,
  active smallint NULL DEFAULT '1'::smallint,
  CONSTRAINT user_id PRIMARY KEY (user_id),
  CONSTRAINT push_user_email_key UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
