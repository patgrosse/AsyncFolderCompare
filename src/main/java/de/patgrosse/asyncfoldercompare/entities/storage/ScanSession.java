package de.patgrosse.asyncfoldercompare.entities.storage;

import java.util.List;

import com.google.gson.annotations.Expose;

import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;

public class ScanSession {
    @Expose
    private RootRealFolder scannedFolder;
    @Expose
    private List<String> enabledPlugins;

    public ScanSession(RootRealFolder scannedFolder, List<String> enabledPlugins) {
        this.scannedFolder = scannedFolder;
        this.enabledPlugins = enabledPlugins;
    }

    public RootRealFolder getScannedFolder() {
        return scannedFolder;
    }

    public void setScannedFolder(RootRealFolder scannedFolder) {
        this.scannedFolder = scannedFolder;
    }

    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    public void setEnabledPlugins(List<String> enabledPlugins) {
        this.enabledPlugins = enabledPlugins;
    }

}
