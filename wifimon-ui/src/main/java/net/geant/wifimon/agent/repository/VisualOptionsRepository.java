package net.geant.wifimon.agent.repository;

import net.geant.wifimon.agent.service.VisualOptionsService;
import net.geant.wifimon.model.entity.UserData;
import net.geant.wifimon.model.entity.VisualOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by kokkinos on 27/6/2017.
 */
public interface VisualOptionsRepository extends JpaRepository<VisualOptions, Long> {
    Optional<VisualOptions> findOneByUserdata(String Userdata);

    @Query(value = "SELECT * FROM options", nativeQuery = true)
    List<VisualOptions> getAll();

    @Query(value = "SELECT * FROM options ORDER BY optionsid desc limit 1", nativeQuery = true)
    List<VisualOptions> getLast();

    @Query(value = "DELETE FROM options WHERE optionsid < :optionsid RETURNING 1", nativeQuery = true)
    Integer deletePreviousEntries(@Param("optionsid") Long optionsid);

    @Query(value = "UPDATE options SET userdata = :userdata, correlationmethod = :correlationmethod, uservisualoption = :uservisualoption RETURNING 1", nativeQuery = true)
    Integer updateEntry(@Param("userdata") String userdata, @Param("uservisualoption") String uservisualoption, @Param("correlationmethod") String correlationmethod);

    @Query(value = "SELECT COUNT(*) FROM options", nativeQuery = true)
    Integer countEntries();

    @Query(value = "SELECT * FROM options ORDER BY optionsid desc limit 1", nativeQuery = true)
    VisualOptions getLastEntry();

}
