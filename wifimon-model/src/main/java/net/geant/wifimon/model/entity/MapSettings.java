package net.geant.wifimon.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by kokkinos on 3/7/2017.
 */
@Entity
@Table(name = "mapsettings")
public class MapSettings implements Serializable {

    private Long mapsettingsid;
    private Integer mapzoom;
    private String maplatitude;
    private String maplongitude;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapsettingsid")
    public Long getMapsettingsid() {
        return mapsettingsid;
    }

    public void setMapsettingsid(Long mapsettingsid) {
        this.mapsettingsid = mapsettingsid;
    }

    @Column(name = "mapzoom", nullable = false)
    public Integer getMapzoom() {
        return mapzoom;
    }

    public void setMapzoom(Integer mapzoom) {
        this.mapzoom = mapzoom;
    }

    @Column(name = "maplatitude", nullable = false)
    public String getMaplatitude() {
        return maplatitude;
    }

    public void setMaplatitude(String maplatitude) {
        this.maplatitude = maplatitude;
    }

    @Column(name = "maplongitude", nullable = false)
    public String getMaplongitude() {
        return maplongitude;
    }

    public void setMaplongitude(String maplongitude) {
        this.maplongitude = maplongitude;
    }
}
