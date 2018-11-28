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
  api_key character varying(50) DEFAULT NULL::character varying,
  name character varying(100) DEFAULT NULL::character varying,
  hash_password_client character varying(100) DEFAULT NULL::character varying,
  hash_password_pusher character varying(100) DEFAULT NULL::character varying,
  blocked smallint DEFAULT 0,
  time_create timestamp without time zone,
  time_edit timestamp without time zone,
  user_create bigint,
  user_edit bigint,
  admin_create bigint,
  admin_edit bigint,
  ip_create character varying(40) DEFAULT NULL::character varying,
  ip_edit character varying(40) DEFAULT NULL::character varying,
  active smallint DEFAULT 1,
  CONSTRAINT push_api_pkey PRIMARY KEY (api_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_api_user
(
  api_user_id bigserial,
  api_id bigint,
  user_id bigint,
  time_create timestamp without time zone,
  time_edit timestamp without time zone,
  user_create bigint,
  user_edit bigint,
  admin_create bigint,
  admin_edit bigint,
  ip_create character varying(48) DEFAULT NULL::character varying,
  ip_edit character varying(48) DEFAULT NULL::character varying,
  active smallint DEFAULT 1,
  CONSTRAINT push_api_user_pkey PRIMARY KEY (api_user_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_client
(
  client_id bigserial,
  api_id bigint,
  device_id character varying(40) DEFAULT NULL::character varying,
  last_token character varying(40) DEFAULT NULL::character varying,
  last_ip character varying(40) DEFAULT NULL::character varying,
  connection integer DEFAULT 0,
  last_time timestamp without time zone,
  time_create timestamp without time zone,
  blocked smallint DEFAULT 0,
  active smallint DEFAULT 1,
  CONSTRAINT push_client_pkey PRIMARY KEY (client_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_client_group
(
  client_group_id bigserial,
  api_id bigint,
  name character varying(100) DEFAULT NULL::character varying,
  group_key character varying(50) DEFAULT NULL::character varying,
  description text,
  blocked smallint DEFAULT 0,
  time_create timestamp without time zone,
  time_edit timestamp without time zone,
  ip_create character varying(40) DEFAULT NULL::character varying,
  ip_edit character varying(40) DEFAULT NULL::character varying,
  user_create bigint,
  user_edit bigint,
  admin_create bigint,
  admin_edit bigint,
  sort_order integer,
  default_data smallint,
  active smallint,
  CONSTRAINT push_client_group_pkey PRIMARY KEY (client_group_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_notification
(
  notification_id bigserial,
  device_id character varying(40) DEFAULT NULL::character varying,
  api_id bigint,
  client_group_id bigint DEFAULT 0,
  title text,
  subtitle text,
  message text,
  ticker_text text,
  uri text,
  color character varying(10) DEFAULT NULL::character varying,
  vibrate text,
  click_action text,
  sound text,
  badge text,
  large_icon text,
  small_icon text,
  type character varying(20) DEFAULT NULL::character varying,
  misc_data text,
  time_create timestamp without time zone,
  is_read smallint DEFAULT 0,
  time_read timestamp without time zone,
  is_sent smallint DEFAULT 0,
  time_sent timestamp without time zone,
  expire smallint DEFAULT 0,
  expiration_time timestamp without time zone,
  active smallint DEFAULT 1,
  CONSTRAINT push_notification_pkey PRIMARY KEY (notification_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_pusher_address
(
  pusher_address_id bigserial,
  api_id bigint,
  address character varying(40) DEFAULT NULL::character varying,
  application_name character varying(100) DEFAULT NULL::character varying,
  application_version character varying(40) DEFAULT NULL::character varying,
  user_agent character varying(200) DEFAULT NULL::character varying,
  first_access timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
  last_access timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
  auth character varying(100) DEFAULT NULL::character varying,
  need_confirmation smallint DEFAULT 1,
  blocked smallint DEFAULT 0,
  active smallint DEFAULT 1,
  CONSTRAINT push_pusher_address_pkey PRIMARY KEY (pusher_address_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_trash
(
  trash_id bigserial,
  api_id bigint,
  device_id character varying(40) DEFAULT NULL::character varying,
  client_group_id bigint DEFAULT 0,
  notification_id bigint,
  time_delete timestamp(6) without time zone,
  CONSTRAINT push_trash_pkey PRIMARY KEY (trash_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE push_user
(
  user_id bigserial,
  api_id bigint,
  username character varying(100) DEFAULT NULL::character varying,
  name character varying(100) DEFAULT NULL::character varying,
  gender character varying(1) DEFAULT 'M'::character varying,
  birth_place character varying(100) DEFAULT NULL::character varying,
  birth_day date,
  phone character varying(30) DEFAULT NULL::character varying,
  email character varying(100) DEFAULT NULL::character varying,
  password character varying(45) DEFAULT NULL::character varying,
  password_initial character varying(45) DEFAULT NULL::character varying,
  auth character varying(45) DEFAULT NULL::character varying,
  address character varying(255) DEFAULT NULL::character varying,
  country_id bigint,
  state_id bigint,
  city_id bigint,
  religion_id character varying(2) DEFAULT NULL::character varying,
  time_create timestamp without time zone,
  time_edit timestamp without time zone,
  user_create bigint,
  user_edit bigint,
  admin_create bigint,
  admin_edit bigint,
  ip_create character varying(45) DEFAULT NULL::character varying,
  ip_edit character varying(45) DEFAULT NULL::character varying,
  blocked smallint DEFAULT 0,
  active smallint DEFAULT 1,
  CONSTRAINT push_user_pkey PRIMARY KEY (user_id),
  CONSTRAINT email UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
