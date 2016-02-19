package net.geant.wifimon.agent.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import net.geant.wifimon.agent.data.GenericMeasurement;
import net.geant.wifimon.agent.dto.GrafanaSnapshotResponse;
import net.geant.wifimon.agent.repository.GenericMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * Created by kanakisn on 8/5/15.
 */

@Controller
public class MeasurementsController {
    
    private static final int PAGE_SIZE = 20;
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC,"id"), 
            new Sort.Order(Sort.Direction.ASC,"date"));

    @Autowired
    GenericMeasurementRepository gmRepository;

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
        session.setAttribute("page", page);
        Page<GenericMeasurement> measurementPage = gmRepository.findAll(new PageRequest(page, PAGE_SIZE, sort));
        model.addAttribute("measurements", measurementPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalResults", gmRepository.count());
        model.addAttribute("pageSize", PAGE_SIZE);
        return "secure/genericMeasurements";
    }

    @RequestMapping(value = "/secure/grafana")
    public String grafana(Model model, HttpSession session) {

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);

        WebResource webResource = client
                .resource("http://admin:admin@localhost:3000/api/snapshots");

        String snapshotJsonRequest =
        "{ \"dashboard\": { \"editable\":false, " +
                "\"hideControls\":true, " +
                "\"nav\":[ { \"enable\":false, \"type\":\"timepicker\" } ], " +
                "\"rows\": [ { } ], " +
                "\"style\":\"dark\", " +
                "\"tags\":[], " +
                "\"templating\":{ \"list\":[ ] }, " +
                "\"time\":{ }, " +
                "\"timezone\":\"browser\", " +
                "\"title\":\"Home\", " +
                "\"version\":5 }, " +
                "\"expires\": 0}";

        ClientResponse response = webResource.accept("application/json").type("application/json")
                .post(ClientResponse.class, snapshotJsonRequest);

        if (response.getStatus() != 200) {
            model.addAttribute("error", "Grafana snapshot creation failed");
            return "secure/grafana";
        }

        GrafanaSnapshotResponse snapshotResponse = response.getEntity(GrafanaSnapshotResponse.class);
        model.addAttribute("url", snapshotResponse.getUrl());
        return "secure/grafana";
    }

}
