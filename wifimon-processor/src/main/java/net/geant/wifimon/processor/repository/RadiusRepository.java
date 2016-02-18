package net.geant.wifimon.processor.repository;

import net.geant.wifimon.processor.data.Radius;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Created by kanakisn on 17/02/16.
 */
public interface RadiusRepository extends PagingAndSortingRepository<Radius, Long> {

    public Optional<Radius> findFirst1ByFramedIpAddressOrderByStopTimeAsc(String framedIpAddress);
}
