package net.geant.wifimon.agent.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.geant.wifimon.agent.repository.GenericMeasurementRepository;
import net.geant.wifimon.agent.util.UiConstants;
import net.geant.wifimon.model.dto.GrafanaSnapshotResponse;
import net.geant.wifimon.model.entity.GenericMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

/**
 * Created by kanakisn on 8/5/15.
 */

@Controller
public class MeasurementsController implements UiConstants {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    private static final int PAGE_SIZE = 10;
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC,"date"));

    @Autowired
    GenericMeasurementRepository gmRepository;

    @Autowired
    Client client;

    @Value("${grafana.port}") @NotNull
    private int grafanaPort;

    @RequestMapping(value = "/secure/measurements/generic")
    public String afterLogin(@RequestParam(value = "move", required = false) String move, Model model,
                             HttpSession session) {
        Integer page = (Integer) session.getAttribute("page");
        if (page != null && move != null && !move.isEmpty()) {
            if ("front".equals(move)) page++;
            if ("back".equals(move)) page--;
            if (page < 0) page = 0;
        } else {
            page = 0;
        }
        Page<GenericMeasurement> measurementPage = gmRepository.findAll(new PageRequest(page, PAGE_SIZE, sort));
        int totalPages = measurementPage.getTotalPages();
        if (totalPages > 0 && page > totalPages - 1) {
            page = totalPages - 1;
            measurementPage = gmRepository.findAll(new PageRequest(page, PAGE_SIZE, sort));
        }
        session.setAttribute("page", page);
        model.addAttribute("measurements", measurementPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("classActiveSettingsHome","active");
        return "secure/genericMeasurements";
    }

    @RequestMapping(value = "/secure/grafana")
    public String grafana(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsTimeline","active");
        WebResource webResource = client
                .resource("https://admin:admin@localhost:" + grafanaPort + "/api/snapshots");
        ClientResponse response = null;
        try {
            response = webResource.accept("application/json").type("application/json")
                    .post(ClientResponse.class, SNAPSHOT_JSON_REQUEST);
        } catch (Exception e) {
            LOG.error("An error occurred on creating grafana snapshot", e);
        }
        if (response == null || response.getStatus() != 200) {
            model.addAttribute("error", "Grafana snapshot creation failed");
            return "secure/grafana";
        }
        GrafanaSnapshotResponse snapshotResponse = response.getEntity(GrafanaSnapshotResponse.class);
        model.addAttribute("url", snapshotResponse.getUrl());
        return "secure/grafana";
    }

    @RequestMapping(value = "/secure/guide")
    public String guide(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsGuide","active");
        return "secure/guide";
    }
    @RequestMapping(value = "/secure/help")
    public String help(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsHelp","active");
        return "secure/help";
    }
}
