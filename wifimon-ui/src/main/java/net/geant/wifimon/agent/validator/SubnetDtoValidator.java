package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.repository.SubnetRepository;
import net.geant.wifimon.model.dto.SubnetDto;
import net.geant.wifimon.subnet.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by kanakisn on 28/02/16.
 */
@Component
public class SubnetDtoValidator implements Validator {

    private static final String SUBNET = "subnet";

    @Autowired
    SubnetRepository subnetRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SubnetDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SubnetDto subnet = (SubnetDto) o;
        if (subnet.getSubnet() == null || subnet.getSubnet().isEmpty()) {
            errors.reject(SUBNET, "Empty subnet");
            return;
        }
        try {
            new SubnetUtils(subnet.getSubnet());
        } catch (IllegalArgumentException e) {
            errors.reject(SUBNET, "Invalid CIDR-notation format");
        }
        List s = subnetRepository.findBySubnet(subnet.getSubnet());
        if (!(s == null || s.isEmpty())) errors.reject(SUBNET, "Subnet already exists");
    }
}
