package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.MapSettingsUpdateFormModel;
import net.geant.wifimon.agent.repository.MapSettingsRepository;
import net.geant.wifimon.model.entity.MapSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 3/7/2017.
 */

@Service
public class MapSettingsServiceImpl implements MapSettingsService{

    private final MapSettingsRepository mapSettingsRepository;

    @Autowired
    public MapSettingsServiceImpl (final MapSettingsRepository mapSettingsRepository) {
        this.mapSettingsRepository = mapSettingsRepository;
    }

    @Override
    public Optional<MapSettings> getMapsettingsByMapsettingsid(long mapsettingsid) {
        return Optional.ofNullable(mapSettingsRepository.findOne(mapsettingsid));
    }

    @Override
    public Collection<MapSettings> getAllMapSettings() {
        return mapSettingsRepository.findAll();
    }

    @Override
    public Collection<MapSettings> getLastMapSetting() {
        return mapSettingsRepository.getLast();
    }

    @Override
    public MapSettings create(MapSettingsUpdateFormModel form) {
        MapSettings mapSettings = new MapSettings();
        mapSettings.setMapzoom(form.getMapzoom());
        mapSettings.setMaplatitude(form.getMaplatitude());
        mapSettings.setMaplongitude(form.getMaplongitude());
        return mapSettingsRepository.save(mapSettings);
    }
}
