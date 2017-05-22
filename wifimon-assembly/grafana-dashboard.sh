﻿#!/bin/sh
curl -k 'https://admin:admin@localhost:3000/api/datasources' -X POST -H 'Content-Type: application/json;charset=UTF-8' --data-binary '{"name":"WiFiMon measurements","type":"influxdb","url":"http://localhost:8086","access":"proxy","isDefault":true,"database":"wifimon","user":"root","password":"root"}}'
curl -k 'https://admin:admin@localhost:3000/api/dashboards/db' -X POST -H 'Content-Type: application/json;charset=UTF-8' --data-binary ' {"dashboard": { "id": null, "title": "Measurements Display", "originalTitle": "Measurements Display", "tags": [], "style": "dark", "timezone": "browser", "editable": true, "hideControls": false, "sharedCrosshair": false, "rows": [ { "collapse": false, "editable": true, "height": "250px", "panels": [ { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 1, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "Download", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval)", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] }, { "alias": "Upload", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"UploadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval)", "refId": "B", "resultFormat": "time_series", "select": [ [ { "params": [ "UploadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Throughput", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 2, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "Ping", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"ping\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval)", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "ping" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Round Trip Time", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "ms", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 3, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "Windows", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Windows/ AND $timeFilter GROUP BY time($interval)", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Windows/" } ] }, { "alias": "Mac OS X and iOS", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Mac/ AND $timeFilter GROUP BY time($interval)", "refId": "B", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Mac/" } ] }, { "alias": "Linux", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /X11/ AND $timeFilter GROUP BY time($interval)", "refId": "C", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/X11/" } ] }, { "alias": "Android", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Android/ AND $timeFilter GROUP BY time($interval)", "refId": "D", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Android/" } ] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per OS", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 4, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "ip" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"ip\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per IP", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 5, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "Chrome", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Chrome/ AND \"UserAgent\" !~ /Edge/ AND $timeFilter GROUP BY time($interval)", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Chrome/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/Edge/" } ] }, { "alias": "Safari", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Safari/ AND \"UserAgent\" !~ /Chrome/ AND $timeFilter GROUP BY time($interval)", "refId": "B", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Safari/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/Chrome/" } ] }, { "alias": "Firefox", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Firefox/ AND $timeFilter GROUP BY time($interval)", "refId": "C", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Firefox/" } ] }, { "alias": "Internet Explorer", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /MSIE/ AND $timeFilter GROUP BY time($interval)", "refId": "D", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/MSIE/" } ] }, { "alias": "Microsoft Edge", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" =~ /Edge/ AND $timeFilter GROUP BY time($interval)", "refId": "E", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "=~", "value": "/Edge/" } ] }, { "alias": "Other", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE \"UserAgent\" !~ /Edge/ AND \"UserAgent\" !~ /Safari/ AND \"UserAgent\" !~ /MSIE/ AND \"UserAgent\" !~ /Firefox/ AND \"UserAgent\" !~ /Chrome/ AND $timeFilter GROUP BY time($interval)", "refId": "F", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [ { "key": "UserAgent", "operator": "!~", "value": "/Edge/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/Safari/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/MSIE/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/Firefox/" }, { "condition": "AND", "key": "UserAgent", "operator": "!~", "value": "/Chrome/" } ] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per Browser", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 6, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "username" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"username\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per Username", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 7, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "calledStationId" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"calledStationId\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per Access Point mac address", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 8, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "callingStationId" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"callingStationId\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per Client mac address", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 9, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "nasPortType" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"nasPortType\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per NAS port type", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] }, { "aliasColors": {}, "bars": false, "datasource": "WiFiMon measurements", "editable": true, "error": false, "fill": 1, "grid": { "leftLogBase": 1, "leftMax": null, "leftMin": null, "rightLogBase": 1, "rightMax": null, "rightMin": null, "threshold1": null, "threshold1Color": "rgba(216, 200, 27, 0.27)", "threshold2": null, "threshold2Color": "rgba(234, 112, 112, 0.22)" }, "id": 10, "legend": { "alignAsTable": true, "avg": true, "current": true, "max": true, "min": true, "show": true, "total": false, "values": true }, "lines": true, "linewidth": 2, "links": [], "nullPointMode": "connected", "percentage": false, "pointradius": 2, "points": true, "renderer": "flot", "seriesOverrides": [], "span": 6, "stack": false, "steppedLine": false, "targets": [ { "alias": "", "dsType": "influxdb", "groupBy": [ { "params": [ "auto" ], "type": "time" }, { "params": [ "testTool" ], "type": "tag" } ], "measurement": "nettest", "query": "SELECT mean(\"DownloadThroughtput\") FROM \"nettest\" WHERE $timeFilter GROUP BY time($interval), \"testTool\"", "refId": "A", "resultFormat": "time_series", "select": [ [ { "params": [ "DownloadThroughtput" ], "type": "field" }, { "params": [], "type": "mean" } ] ], "tags": [] } ], "timeFrom": null, "timeShift": null, "title": "Download Throughput per Test type", "tooltip": { "shared": true, "value_type": "cumulative" }, "type": "graph", "x-axis": true, "y-axis": true, "y_formats": [ "bps", "short" ] } ], "title": "Row" } ], "time": { "from": "now-30m", "to": "now" }, "timepicker": { "now": true, "refresh_intervals": [ "5s", "10s", "30s", "1m", "5m", "15m", "30m", "1h", "2h", "1d" ], "time_options": [ "5m", "15m", "1h", "6h", "12h", "24h", "2d", "7d", "30d" ] }, "templating": { "list": [] }, "annotations": { "list": [] }, "refresh": "30s", "schemaVersion": 8, "version": 2, "links": [] } }'
