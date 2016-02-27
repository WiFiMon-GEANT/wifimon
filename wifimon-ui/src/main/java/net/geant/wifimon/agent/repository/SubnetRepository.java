package net.geant.wifimon.agent.repository;

import net.geant.wifimon.agent.data.Subnet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by kanakisn on 27/02/16.
 */
public interface SubnetRepository extends JpaRepository<Subnet, Long> {

}
