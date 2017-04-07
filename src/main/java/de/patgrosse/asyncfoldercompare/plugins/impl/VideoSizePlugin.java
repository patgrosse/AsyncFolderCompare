package de.patgrosse.asyncfoldercompare.plugins.impl;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.plugins.SingleValueComparePlugin;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoSizePlugin extends SingleValueComparePlugin {
    private static Logger LOG = LoggerFactory.getLogger(VideoSizePlugin.class);
    private static final String SPLIT_STRING = " x ";
    private static final String STANDARD_STRING = "unknown";

    public VideoSizePlugin() {
        super("VideoSizePlugin", "videosize", "Video size");
    }

    @Override
    public String generateSingleDataForFile(FileObject file) throws Exception {
        String out = STANDARD_STRING;
        IContainer container = IContainer.make();
        int result = container.open(file.getPublicURIString(), IContainer.Type.READ, null);
        if (result < 0) {
            LOG.info("Xuggler error: " + result);
        } else {
            for (int i = 0; i < container.getNumStreams(); i++) {
                IStream stream = container.getStream(0);
                IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    out = coder.getWidth() + SPLIT_STRING + coder.getHeight();
                }
            }
        }
        return out;
    }

    @Override
    public PluginCompareResult compareSingleValue(String oldValue, String newValue) {
        if (oldValue.equals(STANDARD_STRING)) {
            if (newValue.equals(STANDARD_STRING)) {
                return PluginCompareResult.MATCH;
            } else {
                return PluginCompareResult.UNDEFINED;
            }
        } else if (newValue.equals(STANDARD_STRING)) {
            return PluginCompareResult.UNDEFINED;
        }
        String[] splittedOld = oldValue.split(SPLIT_STRING);
        String[] splittedNew = newValue.split(SPLIT_STRING);
        int widthOld = Integer.parseInt(splittedOld[0]);
        int heightOld = Integer.parseInt(splittedOld[1]);
        int widthNew = Integer.parseInt(splittedNew[0]);
        int heightNew = Integer.parseInt(splittedNew[1]);
        if (widthOld > widthNew) {
            if (heightOld >= heightNew) {
                return PluginCompareResult.PREFEROLD;
            } else {
                return PluginCompareResult.UNDEFINED;
            }
        } else if (widthOld < widthNew) {
            if (heightOld <= heightNew) {
                return PluginCompareResult.PREFERNEW;
            } else {
                return PluginCompareResult.UNDEFINED;
            }
        } else {
            if (heightOld == heightNew) {
                return PluginCompareResult.MATCH;
            } else if (heightOld > heightNew) {
                return PluginCompareResult.PREFEROLD;
            } else {
                return PluginCompareResult.PREFERNEW;
            }
        }
    }

}
