*********************************************
PREREQUISITES TO RUN WIFIMON
*********************************************
To install WiFiMon successfully, the following software components are required on 
the installation computer:
- Postgresql (required) and phppgadmin (optional) 
- Postgresql database and tables
- Java 8 
- Grafana
- Influxdb 
See the sections below for detailed instructions on hot to install the above 
components.


********** POSTGRESQL AND PHPPGADMIN **********
sudo apt-get install postgresql postgresql-contrib phppgadmin


********** POSTGRESQL DATABASE AND TABLES (5 steps) **********

***** Step 1: Create database and user *****
su postgres
psql
CREATE USER nettest_user WITH PASSWORD 'nettestpass';
CREATE DATABASE nettest_database OWNER nettest_user;
\c nettest_database;

***** Step 2: Create measurements table *****
CREATE TABLE measurements (
measurement_date timestamp without time zone,
measurement_download_rate double precision,
measurement_upload_rate double precision,
measurement_local_ping double precision,
latitude numeric(20,18),
longitude numeric(21,18),
location_method text,
client_ip inet,
user_agent text,
acctstarttime timestamp without time zone,
username text,
framedipaddress text,
acctsessionid text,
callingstationid text,
calledstationid text,
nasportid text,
nasporttype text,
nasipaddress text,
measurement_id serial PRIMARY KEY
);

***** Step 3: Create subnets table *****
CREATE TABLE subnets (
subnet text,
subnet_id serial PRIMARY KEY
);

***** Step 4: radacct table ***** 
SQL accounting for the freeRADIUS server should be enabled and the records should
be inserted in the radacct table of nettest_database. This table should have
the following columns and types:
	- radacctid: serial PRIMARY KEY
	- acctsessionid: character varying(64) NOT NULL
	- acctuniqueid: character varying(32) NOT NULL
	- username: character varying(253)
	- groupname: character varying(253)
	- realm: character varying(64)
	- nasipaddress: inet NOT NULL
	- nasportid: character varying(15)
	- nasporttype: character varying(32)
	- acctstarttime: timestamp with time zone
	- acctstoptime: timestamp with time zone
	- acctsessiontime: bigint
	- acctauthentic: character varying(32)
	- connectinfo_start: character varying(50)
	- connectinfo_stop: character varying(50)
	- acctinputoctets: bigint
	- acctoutputoctets: bigint
	- calledstationid: character varying(50)
	- callingstationid: character varying(50)
	- acctterminatecause: character varying(32)
	- servicetype: character varying(32)
	- xascendsessionsvrkey: character varying(10)
	- framedprotocol: character varying(32)
	- framedipaddress: inet
	- acctstartdelay: integer
	- acctstopdelay: integer

***** Step 5: Setting privileges commands (if necessary) *****
su postgres
psql
GRANT USAGE ON SCHEMA public to nettest_user;
GRANT CONNECT ON DATABASE nettest_database to nettest_user;
\c nettest_database
GRANT USAGE ON SCHEMA public to nettest_user;
GRANT SELECT ON measurements, radacct, subnets TO nettest_user;
GRANT INSERT ON measurements, radacct, subnets TO nettest_user;
GRANT DELETE ON measurements, radacct, subnets TO nettest_user;
GRANT USAGE, SELECT ON SEQUENCE measurements_measurement_id_seq TO nettest_user;
GRANT USAGE, SELECT ON SEQUENCE radacct_radacctid_seq TO nettest_user;
GRANT USAGE, SELECT ON SEQUENCE subnets_subnet_id_seq TO nettest_user;


********** JAVA 8 **********
See instructions at http://tecadmin.net/install-oracle-java-8-jdk-8-ubuntu-via-ppa/


********** GRAFANA (2 steps) **********

***** Step 1: Install Grafana *****
wget https://grafanarel.s3.amazonaws.com/builds/grafana_2.6.0_amd64.deb
sudo apt-get install -y adduser libfontconfig
sudo dpkg -i grafana_2.6.0_amd64.deb
sudo service grafana-server start

***** Step 2: Grafana configuration file *****
Open to Grafana configuration file (/etc/grafana/grafana.ini) and make the 
following changes:

[server]
# Protocol (http or https)
protocol = https
# The ip address to bind to, empty will bind to all interfaces
http_addr =
# The http port to use
http_port = 3000
# The public facing domain name used to access grafana from a browser
domain = INSERT the public facing domain name
# https certs & key file
cert_file = INSERT the path to cert file
cert_key = INSERT the path to cert file

[auth.anonymous]
# enable anonymous access
enabled = true

Save the file and restart the Grafana server: sudo service grafana-server restart

********** Influxdb **********
wget http://influxdb.s3.amazonaws.com/influxdb_0.9.4.2_amd64.deb
sudo dpkg -i influxdb_0.9.4.2_amd64.deb
influx
CREATE DATABASE wifimon