package de.patgrosse.asyncfoldercompare.plugins.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import de.patgrosse.asyncfoldercompare.plugins.SingleValueComparePlugin;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

public class CRC32Plugin extends SingleValueComparePlugin {

    public CRC32Plugin() {
        super("CRC32Plugin", "crc32", "CRC32");
    }

    @Override
    public String generateSingleDataForFile(FileObject file) throws Exception {
        return Long.toString(customCRC32(file));
    }

    @Override
    public PluginCompareResult compareSingleValue(String oldValue, String newValue) {
        return oldValue.equals(newValue) ? PluginCompareResult.MATCH : PluginCompareResult.DIFFER;
    }

    private long customCRC32(FileObject file) throws IOException {
        final CRC32 crc = new CRC32();
        if (file.isFolder()) {
            throw new IllegalArgumentException("Checksums can't be computed on directories");
        }
        InputStream in = null;
        try {
            in = new CheckedInputStream(file.getContent().getInputStream(), crc);
            IOUtils.copy(in, new NullOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
        return crc.getValue();
    }

}
