package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.MapSettingsUpdateFormModel;
import net.geant.wifimon.model.entity.MapSettings;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 3/7/2017.
 */
public interface MapSettingsService {

    Optional<MapSettings> getMapsettingsByMapsettingsid(long mapsettingsid);

    Collection<MapSettings> getAllMapSettings();

    Collection<MapSettings> getLastMapSetting();

    MapSettings create(MapSettingsUpdateFormModel form);
}
