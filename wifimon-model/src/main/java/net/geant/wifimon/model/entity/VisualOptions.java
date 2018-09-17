package net.geant.wifimon.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by kokkinos on 27/6/2017.
 */
@Entity
@Table(name = "options")
public class VisualOptions implements Serializable {

    private Long optionsid;
    private UserData userdata;
    private CorrelationMethod correlationmethod;
    private UserVisualOption uservisualoption;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "optionsid")
    public Long getOptionsid() {
        return optionsid;
    }

    public void setOptionsid(Long optionsid) {
        this.optionsid = optionsid;
    }

    @Column(name = "userdata", nullable = false)
    @Enumerated(EnumType.STRING)
    public UserData getUserdata() {
        return userdata;
    }

    public void setUserdata(UserData userdata) {
        this.userdata = userdata;
    }

    @Column(name = "correlationmethod", nullable = false)
    @Enumerated(EnumType.STRING)
    public CorrelationMethod getCorrelationmethod() {
        return correlationmethod;
    }

    public void setCorrelationmethod(CorrelationMethod correlationmethod) {
        this.correlationmethod = correlationmethod;
    }

    @Column(name = "uservisualoption", nullable = false)
    @Enumerated(EnumType.STRING)
    public UserVisualOption getUservisualoption() {
        return uservisualoption;
    }

    public void setUservisualoption(UserVisualOption uservisualoption) {
        this.uservisualoption = uservisualoption;
    }
}
