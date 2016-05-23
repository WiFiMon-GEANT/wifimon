package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by kanakisn on 2/14/16.
 */
public class GrafanaSnapshotResponse implements Serializable {

    private String deleteKey;
    private String deleteUrl;
    private String key;
    private String url;

    public String getDeleteKey() {
        return deleteKey;
    }

    public void setDeleteKey(String deleteKey) {
        this.deleteKey = deleteKey;
    }

    public String getDeleteUrl() {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
