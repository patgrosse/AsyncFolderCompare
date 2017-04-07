package de.patgrosse.asyncfoldercompare.plugins;

import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

public class SizePlugin extends SingleValueComparePlugin {

    public SizePlugin() {
        super("SizePlugin", "size", "File size");
    }

    @Override
    public String generateSingleDataForFile(FileObject file) throws Exception {
        return Long.toString(file.getContent().getSize());
    }

    @Override
    public PluginCompareResult compareSingleValue(String oldValue, String newValue) {
        long oldV = Long.parseLong(oldValue);
        long newV = Long.parseLong(newValue);
        if (oldV < newV) {
            return PluginCompareResult.PREFERNEW;
        }
        if (oldV > newV) {
            return PluginCompareResult.PREFEROLD;
        }
        return PluginCompareResult.MATCH;
    }

}
