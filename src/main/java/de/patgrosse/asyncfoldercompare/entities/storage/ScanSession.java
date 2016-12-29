package de.patgrosse.asyncfoldercompare.entities.storage;

import java.util.List;

import com.google.gson.annotations.Expose;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RootCompareFolder;

public class ScanSession {
    @Expose
    private RootCompareFolder scannedFolder;
    @Expose
    private List<String> enabledPlugins;

    public ScanSession(RootCompareFolder scannedFolder, List<String> enabledPlugins) {
        this.scannedFolder = scannedFolder;
        this.enabledPlugins = enabledPlugins;
    }

    public RootCompareFolder getScannedFolder() {
        return scannedFolder;
    }

    public void setScannedFolder(RootCompareFolder scannedFolder) {
        this.scannedFolder = scannedFolder;
    }

    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    public void setEnabledPlugins(List<String> enabledPlugins) {
        this.enabledPlugins = enabledPlugins;
    }

}
