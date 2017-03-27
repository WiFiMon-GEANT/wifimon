package net.geant.wifimon.agent.repository;
import net.geant.wifimon.model.entity.GenericMeasurement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Created by kokkinos on 2/9/2016.
 */
public interface GenericMeasurementRepository extends PagingAndSortingRepository<GenericMeasurement, Long> {

    @Query(value = "SELECT COUNT(*) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Integer findCount(@Param("apmac") String apmac);

    @Query(value = "SELECT AVG(measurement_download_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findDownloadAvg(@Param("apmac") String apmac);

    @Query(value = "SELECT MIN(measurement_download_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findDownloadMin(@Param("apmac") String apmac);

    @Query(value = "SELECT MAX(measurement_download_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findDownloadMax(@Param("apmac") String apmac);

    @Query(value = "SELECT AVG(measurement_upload_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findUploadAvg(@Param("apmac") String apmac);

    @Query(value = "SELECT MIN(measurement_upload_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findUploadMin(@Param("apmac") String apmac);

    @Query(value = "SELECT MAX(measurement_upload_rate) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findUploadMax(@Param("apmac") String apmac);

    @Query(value = "SELECT AVG(measurement_local_ping) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findPingAvg(@Param("apmac") String apmac);

    @Query(value = "SELECT MIN(measurement_local_ping) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findPingMin(@Param("apmac") String apmac);

    @Query(value = "SELECT MAX(measurement_local_ping) FROM measurements WHERE REPLACE(UPPER(apmac), '-', ':') = :apmac", nativeQuery = true)
    Double findPingMax(@Param("apmac") String apmac);
}
