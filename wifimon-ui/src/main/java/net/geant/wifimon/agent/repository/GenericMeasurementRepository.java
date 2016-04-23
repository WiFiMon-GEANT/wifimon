package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.GenericMeasurement;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by kanakisn on 11/17/15.
 */
public interface GenericMeasurementRepository extends PagingAndSortingRepository<GenericMeasurement, Long> {
    
}
