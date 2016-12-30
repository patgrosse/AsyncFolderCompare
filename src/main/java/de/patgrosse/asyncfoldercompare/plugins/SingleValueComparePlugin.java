package de.patgrosse.asyncfoldercompare.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;

public abstract class SingleValueComparePlugin extends ComparePlugin {
    private String compareKeyName;

    public SingleValueComparePlugin(String name, String compareKeyName, String compareCheckName) {
        super(name, Collections.singletonList(new CompareCheck(compareKeyName, compareCheckName)));
        this.compareKeyName = compareKeyName;
    }

    @Override
    public void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception {
        collector.setAttribute(compareKeyName, generateSingleDataForFile(file));
    }

    @Override
    public PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld,
                                                      FileAttributeDisposer disposerNew) {
        String oldValue = disposerOld.getAttribute(compareKeyName);
        String newValue = disposerNew.getAttribute(compareKeyName);
        PluginCompareResult type;
        if (oldValue == null || newValue == null) {
            type = PluginCompareResult.UNDEFINED;
        } else {
            type = compareSingleValue(oldValue, newValue);
        }
        Map<String, PluginCompareResult> fullResult = new HashMap<>();
        fullResult.put(compareKeyName, type);
        return new PluginFileCompareResultHolder(type, fullResult);
    }

    public abstract String generateSingleDataForFile(FileObject file) throws Exception;

    public abstract PluginCompareResult compareSingleValue(String oldValue, String newValue);

}
