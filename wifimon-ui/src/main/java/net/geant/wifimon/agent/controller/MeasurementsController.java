package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.data.GenericMeasurement;
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

    @RequestMapping(value = "/secure/measurements/process")
    public String process(Model model) {
        for (GenericMeasurement gm : gmRepository.findAll()) {
            if (gm != null) {
                if (gm.getClientMac() != null && !gm.getClientMac().isEmpty()) {
                    gm.setMac(new StringBuilder(gm.getClientMac()).substring(0, 17).toUpperCase().replace(":", "-"));
                    gmRepository.save(gm);

                }
            }
        }
        return "redirect:/secure/measurements/generic";
    }
    
        @RequestMapping(value = "/secure/grafana")
    public String grafana(Model model, HttpSession session) {
        return "secure/grafana";
    }

}
