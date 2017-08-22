package net.geant.wifimon.agent.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kokkinos on 3/7/2017.
 */
public class MapSettingsUpdateFormModel implements Serializable {

    @NotNull
    private Integer mapzoom;

    @NotNull
    private String maplatitude;

    @NotNull
    private String maplongitude;

    public Integer getMapzoom() {
        return mapzoom;
    }

    public void setMapzoom(Integer mapzoom) {
        this.mapzoom = mapzoom;
    }

    public String getMaplatitude() {
        return maplatitude;
    }

    public void setMaplatitude(String maplatitude) {
        this.maplatitude = maplatitude;
    }

    public String getMaplongitude() {
        return maplongitude;
    }

    public void setMaplongitude(String maplongitude) {
        this.maplongitude = maplongitude;
    }
}
