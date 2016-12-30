package de.patgrosse.asyncfoldercompare.entities.storage;

import com.google.gson.annotations.Expose;

public class LastSettings {
    @Expose
    private String oldURI;
    @Expose
    private boolean oldURIIsJSON;
    @Expose
    private String newURI;
    @Expose
    private boolean newURIIsJSON;
    @Expose
    private String mapjsonFolderURI;
    @Expose
    private String mapjsonJSONURI;
    @Expose
    private Credentials credentialsOld = new Credentials();
    @Expose
    private Credentials credentialsNew = new Credentials();

    public String getOldURI() {
        return oldURI;
    }

    public void setOldURI(String oldURI) {
        this.oldURI = oldURI;
    }

    public boolean isOldURIIsJSON() {
        return oldURIIsJSON;
    }

    public void setOldURIIsJSON(boolean oldURIIsJSON) {
        this.oldURIIsJSON = oldURIIsJSON;
    }

    public String getNewURI() {
        return newURI;
    }

    public void setNewURI(String newURI) {
        this.newURI = newURI;
    }

    public boolean isNewURIIsJSON() {
        return newURIIsJSON;
    }

    public void setNewURIIsJSON(boolean newURIIsJSON) {
        this.newURIIsJSON = newURIIsJSON;
    }

    public String getMapjsonFolderURI() {
        return mapjsonFolderURI;
    }

    public void setMapjsonFolderURI(String mapjsonFolderURI) {
        this.mapjsonFolderURI = mapjsonFolderURI;
    }

    public String getMapjsonJSONURI() {
        return mapjsonJSONURI;
    }

    public void setMapjsonJSONURI(String mapjsonJSONURI) {
        this.mapjsonJSONURI = mapjsonJSONURI;
    }

    public Credentials getCredentialsOld() {
        return credentialsOld;
    }

    public void setCredentialsOld(Credentials credentialsOld) {
        this.credentialsOld = credentialsOld;
    }

    public Credentials getCredentialsNew() {
        return credentialsNew;
    }

    public void setCredentialsNew(Credentials credentialsNew) {
        this.credentialsNew = credentialsNew;
    }

}
