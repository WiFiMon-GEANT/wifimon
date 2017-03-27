package net.geant.wifimon.processor.resource;

import net.geant.wifimon.model.dto.NetTestMeasurement;
import net.geant.wifimon.model.entity.Accesspoint;
import net.geant.wifimon.model.entity.GenericMeasurement;
import net.geant.wifimon.model.entity.Radius;
import net.geant.wifimon.model.entity.Subnet;
import net.geant.wifimon.processor.repository.AccesspointsRepository;
import net.geant.wifimon.processor.repository.GenericMeasurementRepository;
import net.geant.wifimon.processor.repository.RadiusRepository;
import net.geant.wifimon.processor.repository.SubnetRepository;
import org.apache.commons.net.util.SubnetUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kanakisn on 12/02/16.
 */
@Component
@Path("/wifimon")
public class AggregatorResource {

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    GenericMeasurementRepository measurementRepository;

    @Autowired
    RadiusRepository radiusRepository;

    @Autowired
    SubnetRepository subnetRepository;

    @Autowired
    AccesspointsRepository accesspointsRepository;


    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();
        Radius r = radiusRepository.find(ip, new Date());
        if (r != null) {
            Accesspoint ap = accesspointsRepository.find(r.getCalledStationId());
            return addGrafanaMeasurement(addMeasurement(measurement, r, ap, ip, agent));
        }else {
            return addGrafanaMeasurement(addMeasurement(measurement, r, ip, agent));
        }
//        if (r == null) return Response.serverError().build();

    }

    @POST
    @Path("/subnet")
    public Response correlate(@Context HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets == null || subnets.isEmpty()) return Response.ok(false).build();

        List<SubnetUtils.SubnetInfo> s = subnets.stream().
                map(it -> it.fromSubnetString()).collect(Collectors.toList());

        for (SubnetUtils.SubnetInfo si : s) {
            if (si.isInRange(ip)) return Response.ok(true).build();
        }
        return Response.ok(false).build();
    }

    private Response addGrafanaMeasurement(GenericMeasurement measurement) {
        try {
            Point point = Point.measurement("nettest")
                    .tag("ip", measurement.getClientIp() != null ? measurement.getClientIp() : "N/A")
                    .tag("UserAgent", measurement.getUserAgent() != null ? measurement.getUserAgent() : "N/A")
                    .tag("longitude",
                            measurement.getLongitude() != null ? String.valueOf(measurement.getLongitude()) : "N/A")
                    .tag("latitude",
                            measurement.getLatitude() != null ? String.valueOf(measurement.getLatitude()) : "N/A")
                    .tag("locationMethod",
                            measurement.getLocationMethod() != null ? measurement.getLocationMethod() : "N/A")
                    .tag("username", measurement.getUsername() != null ? measurement.getUsername() : "N/A")
                    .tag("callingStationId",
                            measurement.getCallingStationId() != null ? measurement.getCallingStationId() : "N/A")
                    .tag("calledStationId",
                            measurement.getCalledStationId() != null ? measurement.getCalledStationId() : "N/A")
                    .tag("nasPortType", measurement.getNasPortType() != null ? measurement.getNasPortType() : "N/A")
                    .tag("nasIpAddress", measurement.getNasIpAddress() != null ? measurement.getNasIpAddress() : "N/A")
                    .field("DownloadThroughtput",
                            measurement.getDownloadRate() == -1 ? -1d : measurement.getDownloadRate() * 8 * 1000)
                    .field("UploadThroughtput",
                            measurement.getUploadRate() == -1 ? -1d : measurement.getUploadRate() * 8 * 1000)
                    .field("ping", measurement.getLocalPing() == -1 ? -1d : measurement.getLocalPing())
                    .build();
            influxDB.write("wifimon", "default", point);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private GenericMeasurement addMeasurement(NetTestMeasurement measurement, Radius radius, Accesspoint accesspoint, String ip, String agent) {
        GenericMeasurement m = new GenericMeasurement();
        m.setDate(new Date());
        m.setDownloadRate(measurement.getDownloadThroughput());
        m.setUploadRate(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(Double.valueOf(measurement.getLatitude()));
        m.setLongitude(Double.valueOf(measurement.getLongitude()));
        m.setLocationMethod(measurement.getLocationMethod());
        m.setClientIp(ip);
        m.setUserAgent(agent);
        m.setStartTime(radius != null ? radius.getStartTime() : null);
        m.setUsername(radius != null ? radius.getUsername() : null);
        m.setFramedIpAddress(radius != null ? radius.getFramedIpAddress() : null);
        m.setSessionId(radius != null ? radius.getSessionId() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
        m.setApMac(accesspoint != null ? accesspoint.getMac() : null);
        m.setApLatitude(accesspoint != null ? accesspoint.getLatitude() : null);
        m.setApLongitude(accesspoint != null ? accesspoint.getLongitude() : null);
        m.setApBuilding(accesspoint != null ? accesspoint.getBuilding() : null);
        m.setApFloor(accesspoint != null ? accesspoint.getFloor() : null);
        m.setApNotes(accesspoint != null ? accesspoint.getNotes() : null);
        return measurementRepository.save(m);
    }

    private GenericMeasurement addMeasurement(NetTestMeasurement measurement, Radius radius, String ip, String agent) {
        GenericMeasurement m = new GenericMeasurement();
        m.setDate(new Date());
        m.setDownloadRate(measurement.getDownloadThroughput());
        m.setUploadRate(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(Double.valueOf(measurement.getLatitude()));
        m.setLongitude(Double.valueOf(measurement.getLongitude()));
        m.setLocationMethod(measurement.getLocationMethod());
        m.setClientIp(ip);
        m.setUserAgent(agent);
        m.setStartTime(radius != null ? radius.getStartTime() : null);
        m.setUsername(radius != null ? radius.getUsername() : null);
        m.setFramedIpAddress(radius != null ? radius.getFramedIpAddress() : null);
        m.setSessionId(radius != null ? radius.getSessionId() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
        return measurementRepository.save(m);
    }

    private SubnetUtils.SubnetInfo fromSubnetString(String subnet) {
        return new SubnetUtils(subnet).getInfo();
    }

}