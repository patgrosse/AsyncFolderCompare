package de.patgrosse.asyncfoldercompare.plugins;

import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

public class LastModifiedPlugin extends SingleValueComparePlugin {

    public LastModifiedPlugin() {
        super("LastModifiedPlugin", "lastModified", "Modified");
    }

    @Override
    public String generateSingleDataForFile(FileObject file) throws Exception {
        return Long.toString(file.getContent().getLastModifiedTime());
    }

    @Override
    public PluginCompareResult compareSingleValue(String oldValue, String newValue) {
        return oldValue.equals(newValue) ? PluginCompareResult.MATCH : PluginCompareResult.DIFFER;
    }

}
