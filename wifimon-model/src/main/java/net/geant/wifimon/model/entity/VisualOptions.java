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
    private Units units;
    private Integer radiuslife;
    private ElasticSearchSupport elasticsearchsupport;
    private GrafanaSupport grafanasupport;
    private CorrelationMethod correlationmethod;

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

    @Column(name = "elasticsearchsupport", nullable = false)
    @Enumerated(EnumType.STRING)
    public ElasticSearchSupport getElasticsearchsupport() {
        return elasticsearchsupport;
    }

    public void setElasticsearchsupport(ElasticSearchSupport elasticsearchsupport) {
        this.elasticsearchsupport = elasticsearchsupport;
    }

    @Column(name = "grafanasupport", nullable = false)
    @Enumerated(EnumType.STRING)
    public GrafanaSupport getGrafanasupport() {
        return grafanasupport;
    }

    public void setGrafanasupport(GrafanaSupport grafanasupport) {
        this.grafanasupport = grafanasupport;
    }

    @Column(name = "units", nullable = false)
    @Enumerated(EnumType.STRING)
    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
        this.units = units;
    }

    @Column(name = "radiuslife", nullable = false)
    public Integer getRadiuslife() {
        return radiuslife;
    }

    public void setRadiuslife(Integer radiuslife) {
        this.radiuslife = radiuslife;
    }

    @Column(name = "correlationmethod", nullable = false)
    @Enumerated(EnumType.STRING)
    public CorrelationMethod getCorrelationmethod() {
        return correlationmethod;
    }

    public void setCorrelationmethod(CorrelationMethod correlationmethod) {
        this.correlationmethod = correlationmethod;
    }
}
