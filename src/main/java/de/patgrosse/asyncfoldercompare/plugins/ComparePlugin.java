package de.patgrosse.asyncfoldercompare.plugins;

import java.util.Collections;
import java.util.List;

import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;

public abstract class ComparePlugin {
    private String name;
    private List<CompareCheck> checkNames;

    public ComparePlugin(String name, List<CompareCheck> checkNames) {
        this.name = name;
        this.checkNames = checkNames;
    }

    public String getName() {
        return name;
    }

    public List<CompareCheck> getCheckNames() {
        return Collections.unmodifiableList(checkNames);
    }

    public abstract void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception;

    public abstract PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld,
                                                               FileAttributeDisposer disposerNew);
}
