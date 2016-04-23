package net.geant.wifimon.processor.repository;

import net.geant.wifimon.model.entity.Radius;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

/**
 * Created by kanakisn on 17/02/16.
 */
public interface RadiusRepository extends PagingAndSortingRepository<Radius, Long> {

    Optional<Radius> findFirst1ByFramedIpAddressOrderByStopTimeAsc(String framedIpAddress);

    @Query(value = "SELECT DISTINCT ON (framedipaddress) radacct.* FROM radacct WHERE abbrev(framedipaddress) = :client_ip AND acctstoptime IS NULL OR acctstoptime < acctstarttime AND acctstarttime < :date ORDER BY framedipaddress, acctstarttime DESC", nativeQuery = true)
    Radius find(@Param("client_ip") String clientIp, @Param("date") Date date);
}
