package net.geant.wifimon.model.dto;

import org.apache.commons.net.util.SubnetUtils;

import java.io.Serializable;
import java.util.Objects;

public class SubnetDto implements Serializable {

    private String subnet;

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public SubnetUtils.SubnetInfo fromSubnetString() {
        return new SubnetUtils(subnet).getInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubnetDto)) return false;
        SubnetDto subnetDto = (SubnetDto) o;
        return Objects.equals(subnet, subnetDto.subnet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subnet);
    }
}
