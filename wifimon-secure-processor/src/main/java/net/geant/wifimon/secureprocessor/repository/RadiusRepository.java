package net.geant.wifimon.secureprocessor.repository;
import net.geant.wifimon.model.entity.Radius;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by kokkinos on 2/9/2016.
 */

public interface RadiusRepository extends PagingAndSortingRepository<Radius, Long> {

    Optional<Radius> findFirst1ByFramedIpAddressOrderByStopTimeAsc(String framedIpAddress);

    @Query(value = "SELECT DISTINCT ON (framedipaddress) radacct.* FROM radacct WHERE abbrev(framedipaddress) = :client_ip AND acctstoptime IS NULL OR acctstoptime < acctstarttime AND acctstarttime < :date ORDER BY framedipaddress, acctstarttime DESC", nativeQuery = true)
    Radius find(@Param("client_ip") String clientIp, @Param("date") Date date);

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '1 day' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords();

    @Query(value = "SELECT DISTINCT ON (framedipaddress) radacct.* FROM radacct WHERE acctstarttime < NOW() - INTERVAL '1 day'", nativeQuery = true)
    List<Radius> findOldRecords();
}
