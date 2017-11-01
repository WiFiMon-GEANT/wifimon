package net.geant.wifimon.agent.model;

import net.geant.wifimon.model.entity.CorrelationMethod;
import net.geant.wifimon.model.entity.ElasticSearchSupport;
import net.geant.wifimon.model.entity.GrafanaSupport;
import net.geant.wifimon.model.entity.Units;
import net.geant.wifimon.model.entity.UserData;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kokkinos on 27/6/2017.
 */
public class VisualOptionsUpdateFormModel implements Serializable {

    @NotNull
    private UserData userdata;

    @NotNull
    private Units units;

    @NotNull
    private Integer radiuslife;

    @NotNull
    private GrafanaSupport grafanasupport;

    @NotNull
    private ElasticSearchSupport elasticsearchsupport;

    @NotNull
    private CorrelationMethod correlationmethod;


    public UserData getUserdata() {
        return userdata;
    }

    public void setUserdata(UserData userdata) {
        this.userdata = userdata;
    }

    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
        this.units = units;
    }

    public Integer getRadiuslife() {
        return radiuslife;
    }

    public void setRadiuslife(Integer radiuslife) {
        this.radiuslife = radiuslife;
    }

    public GrafanaSupport getGrafanasupport() {
        return grafanasupport;
    }

    public void setGrafanasupport(GrafanaSupport grafanasupport) {
        this.grafanasupport = grafanasupport;
    }

    public ElasticSearchSupport getElasticsearchsupport() {
        return elasticsearchsupport;
    }

    public void setElasticsearchsupport(ElasticSearchSupport elasticsearchsupport) {
        this.elasticsearchsupport = elasticsearchsupport;
    }

    public CorrelationMethod getCorrelationmethod() {
        return correlationmethod;
    }

    public void setCorrelationmethod(CorrelationMethod correlationmethod) {
        this.correlationmethod = correlationmethod;
    }
}
