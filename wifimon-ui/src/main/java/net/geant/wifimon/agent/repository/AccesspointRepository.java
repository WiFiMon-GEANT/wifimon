package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.Accesspoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by kokkinos on 8/3/2017.
 */
public interface AccesspointRepository extends JpaRepository<Accesspoint, Long> {
    Optional<Accesspoint> findOneByMac(String mac);

    @Query(value = "SELECT * FROM accesspoints", nativeQuery = true)
    List<Accesspoint> getAll();
}
