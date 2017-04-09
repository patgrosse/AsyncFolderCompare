package de.patgrosse.asyncfoldercompare.plugins;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;
import org.apache.commons.vfs2.FileObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class SingleValueComparePlugin extends ComparePlugin {
    private CompareCheck check;

    public SingleValueComparePlugin(String name, String compareKeyName, String compareCheckName) {
        super(name);
        check = new CompareCheck(compareKeyName, compareCheckName, this::formatOutput);
        setCheckNames(Collections.singletonList(check));
    }

    @Override
    public void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception {
        collector.setAttribute(check.getKeyName(), generateSingleDataForFile(file));
    }

    @Override
    public PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld,
                                                      FileAttributeDisposer disposerNew) {
        String oldValue = disposerOld.getAttribute(check.getKeyName());
        String newValue = disposerNew.getAttribute(check.getKeyName());
        PluginCompareResult type;
        if (oldValue == null || newValue == null) {
            type = PluginCompareResult.UNDEFINED;
        } else {
            type = compareSingleValue(oldValue, newValue);
        }
        Map<CompareCheck, PluginCompareResult> fullResult = new HashMap<>();
        fullResult.put(check, type);
        return new PluginFileCompareResultHolder(type, fullResult);
    }

    public String formatOutput(String input) {
        return input;
    }

    public abstract String generateSingleDataForFile(FileObject file) throws Exception;

    public abstract PluginCompareResult compareSingleValue(String oldValue, String newValue);

}
