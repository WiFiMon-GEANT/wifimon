package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.dto.NetTestMeasurement;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kanakisn on 09/11/15.
 */

@RestController
//@CrossOrigin(origins = "http://62.217.124.241:8080", maxAge = 3600)
@CrossOrigin
@RequestMapping("nettest")
public class NetTestController {

    @Autowired
    private InfluxDB influxDB;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/{add}", method = RequestMethod.POST)
    public void add(@RequestBody NetTestMeasurement measurement) {
        try {
            Point point = Point.measurement("nettest")
                    .tag("ip", measurement.getClientIp())
                    .tag("UserAgent", measurement.getUserAgent())
                    .tag("longitude", measurement.getLongitude())
                    .tag("latitude", measurement.getLatitude())
                    .tag("locationMethod", measurement.getLocationMethod())
                    .tag("username", measurement.getUsername())
                    .tag("callingStationId", measurement.getCallingStationId())
                    .tag("calledStationId", measurement.getCalledStationId())
                    .tag("nasPortType", measurement.getNasPortType())
                    .tag("nasIpAddress", measurement.getNasIpAddress())
                    .field("DownloadThroughtput", measurement.getDownloadThroughput() == -1 ? -1d : measurement.getDownloadThroughput() * 8 * 1000)
                    .field("UploadThroughtput", measurement.getUploadThroughput() == -1 ? -1d : measurement.getUploadThroughput() * 8 * 1000)
                    .field("ping", measurement.getLocalPing() == -1 ? -1d : measurement.getLocalPing())
//                .time(System.currentTimeMillis() * 1000, TimeUnit.MILLISECONDS)
                    .build();
            influxDB.write("wifimon", "default", point);
        } catch (Exception e) {}
    }

}
