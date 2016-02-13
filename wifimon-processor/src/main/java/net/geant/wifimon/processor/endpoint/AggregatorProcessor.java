package net.geant.wifimon.processor.endpoint;

import net.geant.wifimon.processor.dto.NetTestMeasurement;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by kanakisn on 12/02/16.
 */
@Component
@Path("/wifimon")
public class AggregatorProcessor {

    @Autowired
    private InfluxDB influxDB;

    @POST
    @Path("/add")
    public Response correlate(NetTestMeasurement measurement) {
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
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
            return Response.ok("Nikos").build();
    }

}