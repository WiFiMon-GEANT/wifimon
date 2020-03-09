package net.geant.wifimon.agent.model;

import net.geant.wifimon.model.entity.CorrelationMethod;
import net.geant.wifimon.model.entity.UserData;
import net.geant.wifimon.model.entity.UserVisualOption;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kokkinos on 27/6/2017.
 */
public class VisualOptionsUpdateFormModel implements Serializable {

    @NotNull
    private UserData userdata;

    @NotNull
    private CorrelationMethod correlationmethod;

    @NotNull
    private UserVisualOption uservisualoption;

    public UserData getUserdata() {
        return userdata;
    }

    public void setUserdata(UserData userdata) {
        this.userdata = userdata;
    }

    public CorrelationMethod getCorrelationmethod() {
        return correlationmethod;
    }

    public void setCorrelationmethod(CorrelationMethod correlationmethod) {
        this.correlationmethod = correlationmethod;
    }

    public UserVisualOption getUservisualoption() {
        return uservisualoption;
    }

    public void setUservisualoption(UserVisualOption uservisualoption) {
        this.uservisualoption = uservisualoption;
    }
}
