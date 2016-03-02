package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.data.Subnet;
import net.geant.wifimon.agent.repository.SubnetRepository;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by kanakisn on 28/02/16.
 */
@Component
public class SubnetValidator implements Validator {

    @Autowired
    SubnetRepository subnetRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Subnet.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Subnet subnet = (Subnet) o;
        if (subnet.getSubnet() == null || subnet.getSubnet().isEmpty()) {
            errors.reject("subnet", "Empty subnet");
            return;
        }
        try {
            new SubnetUtils(subnet.getSubnet());
        } catch (IllegalArgumentException e) {
            errors.reject("subnet", "Invalid CIDR-notation format");
        }
        List s = subnetRepository.findBySubnet(subnet.getSubnet());
        if (!(s == null || s.isEmpty())) errors.reject("subnet", "Subnet already exists");
        // TODO: 28/02/16 add more validation
    }

}
