package net.geant.wifimon.secureprocessor.repository;
import net.geant.wifimon.model.entity.GenericMeasurement;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by kokkinos on 2/9/2016.
 */
public interface GenericMeasurementRepository extends PagingAndSortingRepository<GenericMeasurement, Long> {
}
