package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.Subnet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by kanakisn on 27/02/16.
 */
public interface SubnetRepository extends JpaRepository<Subnet, Long> {

    List findBySubnet(String subnet);

}
