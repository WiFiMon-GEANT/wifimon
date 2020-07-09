#!/bin/sh

# Create wifimon index
curl -XPUT 'http://FQDN:9200/wifimon?pretty' -H 'Content-Type: application/json' -d'
{
	"mappings" : {
		"properties" : {
			"timestamp" : { "type" : "date" },
			"downloadThroughput" : { "type" : "float" },
			"uploadThroughput" : { "type" : "float" },
			"localPing" : { "type" : "float" },
			"location" : { "type" : "geo_point" },
			"locationMethod" : { "type" : "keyword" },
			"clientIp" : { "type" : "keyword" },
			"userAgent" : { "type" : "keyword" },
			"userBrowser" : { "type" : "keyword" },
			"userOS" : { "type" : "keyword" },
			"testTool" : { "type" : "keyword" },
			"origin" : { "type" : "keyword" },
			"probeNo" : { "type" : "keyword" },
			"username" : { "type" : "keyword" },
			"nasPort" : { "type" : "keyword" },
			"callingStationId" : { "type" : "keyword" },
			"nasIdentifier" : { "type" : "keyword" },
			"calledStationId" : { "type" : "keyword" },
			"nasIpAddress" : { "type" : "keyword" },
			"apBuilding" : { "type" : "keyword" },
			"apFloor" : { "type" : "keyword" },
			"apLocation" : { "type" : "geo_point" },
			"apNotes" : { "type" : "keyword" },
			"requesterSubnet" : { "type" : "keyword" },
			"encryptedIP" : { "type" : "keyword" }
		}
	}
}'

# Create probes index
curl -XPUT 'http://FQDN:9200/probes?pretty' -H 'Content-Type: application/json' -d'
{ 
	"mappings" : {
		"properties" : {
			"timestamp" : { "type" : "date" },
			"bitRate" : { "type" : "float" },
			"txPower" : { "type" : "float" },
			"linkQuality" : { "type" : "float" },
			"signalLevel" : { "type" : "float" },
			"probeNo" : { "type" : "keyword" }
		}
	}
}'

# Create RADIUS logs index
curl -XPUT 'http://FQDN:9200/radiuslogs?pretty' -H 'Content-Type: application/json' -d' 
{
	"mappings" : {
		"properties" :  {
		      "Timestamp" : { "type" : "date" },
		      "User-Name" : { "type" : "keyword" },
		      "Calling-Station-Id" : { "type" : "keyword" },
		      "NAS-Identifier" : { "type" : "keyword" },
		      "Called-Station-Id" : { "type" : "keyword" },
		      "NAS-IP-Address" : { "type" : "ip" },
		      "Framed-IP-Address" : { "type" : "keyword" },
		      "Acct-Status-Type" : { "type" : "keyword" }
	      }
	}
}'

# Create DHCP logs index
curl -XPUT 'http://FQDN:9200/dhcplogs?pretty' -H 'Content-Type: application/json' -d'
{ 
	"mappings" : {
		"properties: : {
			"Timestamp" : { "type" : "date" },
			"IP-Address" : { "type" : "keyword" },
			"MAC-Address" : { "type" : "keyword" }
		}
	}
}'

# Default Data
curl -XPOST 'http://FQDN:9200/wifimon/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -XPOST 'http://FQDN:9200/radiuslogs/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -XPOST 'http://FQDN:9200/probes/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'

curl -XPOST 'http://FQDN:9200/dhcplogs/_doc/?pretty' -H 'Content-Type: application/json' -d' { "test" : "test" }'
