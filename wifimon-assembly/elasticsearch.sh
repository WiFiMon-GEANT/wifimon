#!/bin/sh

# Create wifimon index
curl -XPUT 'http://FQDN:9200/wifimon?pretty' -H 'Content-Type: application/json' -d'
{
	"mappings" : {
		"properties" : {
			"timestamp" : { "type" : "date" },
			"Download-Throughput" : { "type" : "float" },
			"Upload-Throughput" : { "type" : "float" },
			"Local-Ping" : { "type" : "float" },
			"Location" : { "type" : "geo_point" },
			"Location-Method" : { "type" : "keyword" },
			"Client-Ip" : { "type" : "keyword" },
			"User-Agent" : { "type" : "keyword" },
			"User-Browser" : { "type" : "keyword" },
			"user-OS" : { "type" : "keyword" },
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
			"Encrypted-IP" : { "type" : "keyword" }
		}
	}
}'

# Create probes index
curl -XPUT 'http://FQDN:9200/probes?pretty' -H 'Content-Type: application/json' -d'
{ 
	"mappings" : {
		"properties" : {
			"Timestamp" : { "type" : "date" },
			"Bit-Rate" : { "type" : "float" },
			"Tx-Power" : { "type" : "float" },
			"Link-Quality" : { "type" : "float" },
			"Signal-Level" : { "type" : "float" },
			"Accesspoint" : { "type" : "keyword" },
			"Probe-No" : { "type" : "keyword" }
		}
	}
}'

# Default Data
curl -XPOST 'http://FQDN:9200/wifimon/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -XPOST 'http://FQDN:9200/probes/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'
