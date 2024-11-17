package net.geant.wifimon.model.entity;

import java.io.Serializable;

/**
 * Created by kokkinos on 22/11/2017.
 */
public class UrlParameters implements Serializable {

    private String currentUserRole;
    private String queryFilter;

    public String getQueryFilter() {
        return queryFilter;
    }

    public void setQueryFilter(String queryFilter) {
        this.queryFilter = queryFilter;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
    }
}



