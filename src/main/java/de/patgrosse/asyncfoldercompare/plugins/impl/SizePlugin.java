package de.patgrosse.asyncfoldercompare.plugins.impl;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.plugins.SingleValueComparePlugin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;

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

    @Override
    public String formatOutput(String input) {
        return FileUtils.byteCountToDisplaySize(Long.parseLong(input));
    }
}
