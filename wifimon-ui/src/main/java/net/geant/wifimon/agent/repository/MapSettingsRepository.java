package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.MapSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by kokkinos on 3/7/2017.
 */
public interface MapSettingsRepository extends JpaRepository<MapSettings, Long> {

    @Query(value = "SELECT * FROM mapsettings", nativeQuery = true)
    List<MapSettings> getAll();

    @Query(value = "SELECT * FROM mapsettings ORDER BY mapsettingsid desc limit 1", nativeQuery = true)
    List<MapSettings> getLast();

    @Query(value = "DELETE FROM mapsettings WHERE mapsettingsid < :mapsettingsid RETURNING 1", nativeQuery = true)
    Integer deletePreviousEntries(@Param("mapsettingsid") Long mapsettingsid);

    @Query(value = "UPDATE mapsettings SET mapzoom = :mapzoom, maplatitude = :maplatitude, maplongitude = :maplongitude RETURNING 1", nativeQuery = true)
    Integer updateEntry(@Param("mapzoom") Integer mapzoom, @Param("maplatitude") String maplatitude, @Param("maplongitude") String maplongitude);

    @Query(value = "SELECT COUNT(*) FROM mapsettings", nativeQuery = true)
    Integer countEntries();

    @Query(value = "SELECT * FROM mapsettings ORDER BY mapsettingsid desc limit 1", nativeQuery = true)
    MapSettings getLastEntry();

}
