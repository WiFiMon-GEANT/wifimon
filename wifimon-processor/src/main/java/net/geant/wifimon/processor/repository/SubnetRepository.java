package net.geant.wifimon.processor.repository;

import net.geant.wifimon.processor.data.Subnet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by kanakisn on 27/02/16.
 */
public interface SubnetRepository extends JpaRepository<Subnet, Long> {

    List findBySubnet(String subnet);

}
