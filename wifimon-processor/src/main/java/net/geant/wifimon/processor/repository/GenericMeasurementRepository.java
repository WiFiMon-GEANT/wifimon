package net.geant.wifimon.processor.repository;

import net.geant.wifimon.processor.data.GenericMeasurement;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by kanakisn on 17/02/16.
 */
public interface GenericMeasurementRepository extends PagingAndSortingRepository<GenericMeasurement, Long> {
}