package net.geant.wifimon.processor.endpoint;

import net.geant.wifimon.processor.data.GenericMeasurement;
import net.geant.wifimon.processor.data.Radius;
import net.geant.wifimon.processor.dto.NetTestMeasurement;
import net.geant.wifimon.processor.repository.GenericMeasurementRepository;
import net.geant.wifimon.processor.repository.RadiusRepository;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Optional;

/**
 * Created by kanakisn on 12/02/16.
 */
@Component
@Path("/wifimon")
public class AggregatorProcessor {

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    GenericMeasurementRepository measurementRepository;

    @Autowired
    RadiusRepository radiusRepository;

    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();
        Optional<Radius> radius = radiusRepository.findFirst1ByFramedIpAddressOrderByStopTimeAsc(ip);
        Radius r = radius.get();
        if (r == null) return Response.serverError().build();
        return addGrafanaMeasurement(addMeasurement(measurement, r, ip, agent));
    }

    private Response addGrafanaMeasurement(GenericMeasurement measurement) {
        try {
            Point point = Point.measurement("nettest")
                    .tag("ip", measurement.getClientIp())
                    .tag("UserAgent", measurement.getUserAgent())
                    .tag("longitude", String.valueOf(measurement.getLongitude()))
                    .tag("latitude", String.valueOf(measurement.getLatitude()))
                    .tag("locationMethod", measurement.getLocationMethod())
                    .tag("username", measurement.getUsername())
                    .tag("callingStationId", measurement.getCallingStationId())
                    .tag("calledStationId", measurement.getCalledStationId())
                    .tag("nasPortType", measurement.getNasPortType())
                    .tag("nasIpAddress", measurement.getNasIpAddress())
                    .field("DownloadThroughtput", measurement.getDownloadRate() == -1 ? -1d : measurement.getDownloadRate() * 8 * 1000)
                    .field("UploadThroughtput", measurement.getUploadRate() == -1 ? -1d : measurement.getUploadRate() * 8 * 1000)
                    .field("ping", measurement.getLocalPing() == -1 ? -1d : measurement.getLocalPing())
                    //                .time(System.currentTimeMillis() * 1000, TimeUnit.MILLISECONDS)
                    .build();
            influxDB.write("wifimon", "default", point);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private GenericMeasurement addMeasurement(NetTestMeasurement measurement, Radius radius, String ip, String agent) {
        GenericMeasurement m = new GenericMeasurement();
        m.setClientIp(ip);
        m.setDate(new Date());
        m.setDownloadRate(measurement.getDownloadThroughput());
        m.setUploadRate(measurement.getUploadThroughput());
        m.setCalledStationId(radius.getCalledStationId());
        m.setCallingStationId(radius.getCallingStationId());
        m.setUserAgent(agent);
        return measurementRepository.save(m);
    }

    @GET
    @Path("/test")
    public Response test() {
            return Response.ok("Nikos").build();
    }

}