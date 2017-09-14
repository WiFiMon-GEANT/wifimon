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

    /*@Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '6 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords();*/

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '1 hour' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords1();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '2 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords2();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '3 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords3();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '4 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords4();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '5 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords5();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '6 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords6();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '7 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords7();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '8 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords8();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '9 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords9();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '10 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords10();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '11 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords11();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '12 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords12();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '13 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords13();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '14 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords14();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '15 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords15();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '16 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords16();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '17 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords17();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '18 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords18();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '19 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords19();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '20 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords20();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '21 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords21();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '22 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords22();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '23 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords23();

    @Query(value = "DELETE FROM radacct WHERE acctstarttime < NOW() - INTERVAL '24 hours' RETURNING 1", nativeQuery = true)
    Integer deleteOldRecords24();

    @Query(value = "SELECT DISTINCT ON (framedipaddress) radacct.* FROM radacct WHERE acctstarttime < NOW() - INTERVAL '1 day'", nativeQuery = true)
    List<Radius> findOldRecords();
}
