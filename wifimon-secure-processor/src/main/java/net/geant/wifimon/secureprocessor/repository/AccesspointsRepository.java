package net.geant.wifimon.secureprocessor.repository;

import net.geant.wifimon.model.entity.Accesspoint;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Created by kokkinos on 17/3/2017.
 */
public interface AccesspointsRepository extends PagingAndSortingRepository<Accesspoint, Long> {

    Optional<Accesspoint> findFirst1ByMac(String mac);

    @Query(value = "SELECT DISTINCT ON (mac) accesspoints.* FROM accesspoints WHERE mac = REPLACE(UPPER(:ap_mac), '-', ':')", nativeQuery = true)
    Accesspoint find(@Param("ap_mac") String mac);
}

