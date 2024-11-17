package net.geant.wifimon.secureprocessor.repository;

import net.geant.wifimon.model.entity.VisualOptions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by kokkinos on 27/6/2017.
 */
public interface VisualOptionsRepository extends PagingAndSortingRepository<VisualOptions, Long> {

    @Query(value = "SELECT options.correlationmethod FROM options ORDER BY optionsid desc limit 1", nativeQuery = true)
    String findCorrelationmethod();
}
