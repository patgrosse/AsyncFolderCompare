package de.patgrosse.asyncfoldercompare.plugins;

import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;
import org.apache.commons.vfs2.FileObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class ComparePlugin {
    private String name;
    private List<CompareCheck> checks;

    public ComparePlugin(String name) {
        this.name = name;
        this.checks = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public List<CompareCheck> getChecks() {
        return Collections.unmodifiableList(checks);
    }

    public void setCheckNames(List<CompareCheck> checks) {
        this.checks = new LinkedList<>(checks);
    }

    public abstract void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception;

    public abstract PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld,
                                                               FileAttributeDisposer disposerNew);
}
