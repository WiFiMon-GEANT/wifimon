package net.geant.wifimon.secureprocessor.repository;

import net.geant.wifimon.model.entity.Subnet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by kokkinos on 2/9/2016.
 */
public interface SubnetRepository extends JpaRepository<Subnet, Long> {

    List findBySubnet(String subnet);
}
