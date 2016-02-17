package net.geant.wifimon.processor.repository;

import net.geant.wifimon.processor.data.Radius;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by kanakisn on 17/02/16.
 */
public interface RadiusRepository extends PagingAndSortingRepository<Radius, Long> {

    @Query("SELECT DISTINCT ON (framedipaddress) radacct.* FROM radacct WHERE abbrev(framedipaddress) = :client_ip AND acctstoptime IS NULL OR acctstoptime < acctstarttime AND acctstarttime < :date ORDER BY framedipaddress, acctstarttime DESC")
    public Radius find(@Param("clientIp") String clientIp, @Param("date") Date date);
}
