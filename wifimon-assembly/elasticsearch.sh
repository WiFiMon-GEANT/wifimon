#!/bin/sh

# Create wifimon index
curl -XPUT 'http://localhost:9200/wifimon?pretty' -H 'Content-Type: application/json' -d'
{
	"mappings" : {
		"properties" : {
			"Timestamp" : { "type" : "date" },
			"Download-Throughput" : { "type" : "float" },
			"Upload-Throughput" : { "type" : "float" },
			"Local-Ping" : { "type" : "float" },
			"Location" : { "type" : "geo_point" },
			"Location-Method" : { "type" : "keyword" },
			"Client-Ip" : { "type" : "keyword" },
			"User-Agent" : { "type" : "keyword" },
			"User-Browser" : { "type" : "keyword" },
			"User-OS" : { "type" : "keyword" },
			"Test-Tool" : { "type" : "keyword" },
			"Origin" : { "type" : "keyword" },
			"Probe-No" : { "type" : "keyword" },
			"RADIUS-Timestamp" : { "type" : "keyword" },
			"Service-Type" : { "type" : "keyword" },
			"NAS-Port-Id" : { "type" : "keyword" },
			"NAS-Port-Type" : { "type" : "keyword" },
			"User-Name" : { "type" : "keyword" },
			"Acct-Session-Id" : { "type" : "keyword" },
			"Acct-Multi-Session-Id" : { "type" : "keyword" },
			"Calling-Station-Id" : { "type" : "keyword" },
			"Called-Station-Id" : { "type" : "keyword" },
			"Acct-Authentic" : { "type" : "keyword" },
			"Acct-Status-Type" : { "type" : "keyword" },
			"NAS-Identifier" : { "type" : "keyword" },
			"Acct-Delay-Time" : { "type" : "keyword" },
			"NAS-IP-Address" : { "type" : "keyword" },
			"Framed-IP-Address" : { "type" : "keyword" },
			"Acct-Unique-Session-Id" : { "type" : "keyword" },
			"Realm" : { "type" : "keyword" },
			"Ap-Building" : { "type" : "keyword" },
			"Ap-Floor" : { "type" : "keyword" },
			"Ap-Location" : { "type" : "geo_point" },
			"Ap-Notes" : { "type" : "keyword" },
			"Requester-Subnet" : { "type" : "keyword" },
			"Encrypted-IP" : { "type" : "keyword" },
			"IP-Type" : { "type" : "keyword" }
		}
	}
}'

# Create probes index
curl -XPUT 'http://localhost:9200/probes?pretty' -H 'Content-Type: application/json' -d'
{ 
	"mappings" : {
		"properties" : {
			"Timestamp" : { "type" : "date" },
			"Bit-Rate" : { "type" : "float" },
			"Tx-Power" : { "type" : "float" },
			"Link-Quality" : { "type" : "float" },
			"Signal-Level" : { "type" : "float" },
			"Accesspoint" : { "type" : "keyword" },
			"Origin" : { "type" : "keyword"},
			"Probe-No" : { "type" : "keyword" }
		}
	}
}'

# Default Data
curl -XPOST 'http://localhost:9200/wifimon/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -XPOST 'http://localhost:9200/probes/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -X PUT 'http://localhost:9200/_ilm/policy/wifimon_policy?pretty' -H 'Content-Type: application/json' -d'
{
	"policy": {
		"phases": {
			"delete": {
				"min_age": "1d",
				"actions": {
				"delete": {}
				}
			}
		}
	}
}'

curl -X PUT 'http://localhost:9200/_template/wifimon_template?pretty' -H 'Content-Type: application/json' -d'
{
	"index_patterns": ["radiuslogs", "dhcplogs"],
	"settings": {"index.lifecycle.name": "wifimon_policy"}
}'
