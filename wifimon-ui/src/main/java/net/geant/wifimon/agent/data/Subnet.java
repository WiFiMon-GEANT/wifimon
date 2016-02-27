package net.geant.wifimon.agent.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @Column(name = "subnet_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "subnet")
    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
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
