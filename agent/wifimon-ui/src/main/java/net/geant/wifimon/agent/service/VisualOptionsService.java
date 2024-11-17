package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.VisualOptionsUpdateFormModel;
import net.geant.wifimon.model.entity.VisualOptions;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 27/6/2017.
 */
public interface VisualOptionsService {

    Optional<VisualOptions> getOptionsByOptionsid(long optionsid);

    Collection<VisualOptions> getAllVisualOptions();

    Collection<VisualOptions> getLastVisualOption();

    VisualOptions create(VisualOptionsUpdateFormModel form);
}
