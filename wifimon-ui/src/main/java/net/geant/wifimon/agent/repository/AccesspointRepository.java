package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.Accesspoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by kokkinos on 8/3/2017.
 */
public interface AccesspointRepository extends JpaRepository<Accesspoint, Long> {
    Optional<Accesspoint> findOneByMac(String mac);

    @Query(value = "SELECT * FROM accesspoints", nativeQuery = true)
    List<Accesspoint> getAll();

    @Query(value = "UPDATE accesspoints SET measurementscount = :measurementscount, downloadavg = :downloadavg, downloadmin = :downloadmin, downloadmax = :downloadmax," +
            "uploadavg = :uploadavg, uploadmin = :uploadmin, uploadmax = :uploadmax," +
            "pingavg = :pingavg, pingmin = :pingmin, pingmax = :pingmax WHERE mac = :ap_mac RETURNING 1", nativeQuery = true)
    Integer updateApStats(@Param("ap_mac") String mac, @Param("measurementscount") Integer measurementscount,
                            @Param("downloadavg") Double downloadavg, @Param("downloadmin") Double downloadmin, @Param("downloadmax") Double downloadmax,
                            @Param("uploadavg") Double uploadavg, @Param("uploadmin") Double uploadmin, @Param("uploadmax") Double uploadmax,
                            @Param("pingavg") Double pingavg, @Param("pingmin") Double pingmin, @Param("pingmax") Double pingmax);

}
