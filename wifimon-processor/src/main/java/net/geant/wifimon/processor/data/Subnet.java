package net.geant.wifimon.processor.data;

import org.apache.commons.net.util.SubnetUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kanakisn on 27/02/16.
 */

@Entity
@Table(name = "subnets")
public class Subnet implements Serializable {

    private Long id;
    private String subnet;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subnet_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "subnet", unique = true)
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
        if (this == o)
            return true;
        if (!(o instanceof Subnet))
            return false;

        Subnet subnet1 = (Subnet) o;

        return subnet != null ? subnet.equals(subnet1.subnet) : subnet1.subnet == null;

    }

    @Override
    public int hashCode() {
        return subnet != null ? subnet.hashCode() : 0;
    }

}