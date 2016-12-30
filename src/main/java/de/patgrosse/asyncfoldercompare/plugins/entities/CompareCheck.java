package de.patgrosse.asyncfoldercompare.plugins.entities;

public class CompareCheck {
    private String keyName;
    private String displayName;

    public CompareCheck(String keyName, String displayName) {
        this.keyName = keyName;
        this.displayName = displayName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
