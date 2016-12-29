package de.patgrosse.asyncfoldercompare.entities.storage;

import com.google.gson.annotations.Expose;

public class Credentials {
    @Expose
    private String domain = "localhost";
    @Expose
    private String user = "username";
    @Expose
    private String password = "password";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
