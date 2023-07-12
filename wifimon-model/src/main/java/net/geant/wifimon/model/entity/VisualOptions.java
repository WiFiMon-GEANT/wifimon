package net.geant.wifimon.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
