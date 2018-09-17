#!/bin/sh
# create index wifimon and radiuslogs for Elasticsearch
curl -XPUT 'localhost:9200/wifimon?pretty' -H 'Content-Type: application/json' -d'
{  "mappings" : {
      "measurement" : {
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
            "username" : { "type" : "keyword" },
            "nasPort" : { "type" : "keyword" },
            "callingStationId" : { "type" : "keyword" },
            "nasIdentifier" : { "type" : "keyword" },
            "calledStationId" : { "type" : "keyword" },
            "nasIpAddress" : { "type" : "keyword" },
            "apBuilding" : { "type" : "keyword" },
            "apFloor" : { "type" : "keyword" },
            "apLocation" : { "type" : "geo_point" },
            "apNotes" : { "type" : "keyword" }
         }
      }
   }
}'

curl -XPUT 'localhost:9200/radiuslogs?pretty' -H 'Content-Type: application/json' -d'
{  "settings": {
      "analysis": {
         "normalizer": {
            "my_normalizer": {
               "type": "custom",
               "char_filter": ["replace_filter"],
               "filter": ["lowercase"]
            }
         },
         "char_filter": {
            "replace_filter": {
               "type": "mapping",
               "mappings": [": => -"]
            }
         }
      }
   },
   "mappings" : {
      "radiuslog" : {
         "properties" : {
            "timestamp" : { "type" : "date"},
            "username" : { "type" : "keyword"},
            "nas_port" : { "type" : "keyword"},
            "source_host" : { "type" : "keyword"},
            "calling_station_id" : { "type" : "keyword" , "normalizer": "my_normalizer"},
            "result" : { "type" : "keyword"},
            "trace_id" : { "type" : "keyword"},
            "nas_identifier" : { "type" : "keyword"},
            "called_station_id" : { "type" : "keyword"},
            "nas_ip_address" : { "type" : "ip"},
            "framed_ip_address" : { "type" : "ip"},
            "acct_status_type" : { "type" : "keyword"}
         }
      }
   }
}'
