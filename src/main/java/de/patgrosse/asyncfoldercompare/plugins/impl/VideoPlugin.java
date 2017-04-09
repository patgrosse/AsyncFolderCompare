package de.patgrosse.asyncfoldercompare.plugins.impl;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import de.patgrosse.asyncfoldercompare.utils.CompareHelper;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;
import net.filebot.mediainfo.MediaInfo;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.util.RandomAccessMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VideoPlugin extends ComparePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(VideoPlugin.class);
    private static final String SIZE_SPLIT_STRING = " x ";
    private static final String KEY_VIDEO_SIZE = "videosize";
    private static final String KEY_BITRATE = "bitrate";
    private static final String KEY_FRAME_RATE = "fps";

    private CompareCheck checkSize, checkBitrate, checkFramerate;

    public VideoPlugin() {
        super("VideoPlugin");
        checkSize = new CompareCheck(KEY_VIDEO_SIZE, "Video size", this::formatSize);
        checkBitrate = new CompareCheck(KEY_BITRATE, "Bit rate", this::formatBitrate);
        checkFramerate = new CompareCheck(KEY_FRAME_RATE, "Frame rate", this::formatFramerate);
        setCheckNames(Arrays.asList(checkSize, checkBitrate, checkFramerate));
    }

    @Override
    public void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception {
        MediaInfo mi = openMediaInfo(file);
        if (mi != null) {
            collector.setAttribute(KEY_VIDEO_SIZE, generateDataSize(mi));
            collector.setAttribute(KEY_BITRATE, generateDataBitrate(mi));
            collector.setAttribute(KEY_FRAME_RATE, generateDataFramerate(mi));
            closeMediaInfo(mi);
        }
    }

    @Override
    public PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld, FileAttributeDisposer disposerNew) {
        PluginCompareResult sizeResult = compareSizes(disposerOld.getAttribute(KEY_VIDEO_SIZE), disposerNew.getAttribute(KEY_VIDEO_SIZE));
        PluginCompareResult bitrateResult = compareBitrate(disposerOld.getAttribute(KEY_BITRATE), disposerNew.getAttribute(KEY_BITRATE));
        PluginCompareResult framerateResult = compareFramerate(disposerOld.getAttribute(KEY_FRAME_RATE), disposerNew.getAttribute(KEY_FRAME_RATE));

        Map<CompareCheck, PluginCompareResult> fullResult = new HashMap<>();
        fullResult.put(checkSize, sizeResult);
        fullResult.put(checkBitrate, bitrateResult);
        fullResult.put(checkFramerate, framerateResult);
        PluginCompareResult total = CompareHelper.combineResults(Arrays.asList(sizeResult, bitrateResult, framerateResult));
        return new PluginFileCompareResultHolder(total, fullResult);
    }

    private MediaInfo openMediaInfo(FileObject file) throws IOException, InterruptedException, URISyntaxException {
        MediaInfo mi = new MediaInfo();
        try {
            mi.openViaRandomAccessContent(file.getContent().getRandomAccessContent(RandomAccessMode.READ));
        } catch (Exception e) {
            LOG.warn("Could not open file for MediaInfo: " + e.getMessage());
            return null;
        }
        if (mi.streamCount(MediaInfo.StreamKind.Video) > 0) {
            return mi;
        }
        mi.close();
        return null;
    }

    private void closeMediaInfo(MediaInfo mi) throws IOException, InterruptedException {
        mi.close();
    }

    private String generateDataSize(MediaInfo mi) {
        int width = Integer.parseInt(mi.get(MediaInfo.StreamKind.Video, 0, "Width",
                MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        int height = Integer.parseInt(mi.get(MediaInfo.StreamKind.Video, 0, "Height",
                MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        return width + SIZE_SPLIT_STRING + height;
    }

    private String generateDataBitrate(MediaInfo mi) {
        return mi.get(MediaInfo.StreamKind.Video, 0, "BitRate",
                MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    private String generateDataFramerate(MediaInfo mi) {
        return mi.get(MediaInfo.StreamKind.Video, 0, "FrameRate",
                MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    private PluginCompareResult compareSizes(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return PluginCompareResult.MATCH;
        }
        if (oldValue == null || newValue == null) {
            return PluginCompareResult.UNDEFINED;
        }
        String[] splittedOld = oldValue.split(SIZE_SPLIT_STRING);
        String[] splittedNew = newValue.split(SIZE_SPLIT_STRING);
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

    private PluginCompareResult compareBitrate(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return PluginCompareResult.MATCH;
        }
        if (oldValue == null || newValue == null) {
            return PluginCompareResult.UNDEFINED;
        }
        int bitrateOld = Integer.parseInt(oldValue);
        int bitrateNew = Integer.parseInt(newValue);
        if (bitrateOld < bitrateNew) {
            return PluginCompareResult.PREFERNEW;
        } else if (bitrateOld > bitrateNew) {
            return PluginCompareResult.PREFEROLD;
        } else {
            return PluginCompareResult.MATCH;
        }
    }

    private PluginCompareResult compareFramerate(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return PluginCompareResult.MATCH;
        }
        if (oldValue == null || newValue == null) {
            return PluginCompareResult.UNDEFINED;
        }
        double framerateOld = Double.parseDouble(oldValue);
        double framerateNew = Double.parseDouble(newValue);
        if (framerateNew - framerateOld < 0.1D) {
            return PluginCompareResult.MATCH;
        } else if (framerateOld < framerateNew) {
            return PluginCompareResult.PREFERNEW;
        } else if (framerateOld > framerateNew) {
            return PluginCompareResult.PREFEROLD;
        }
        return PluginCompareResult.MATCH;
    }

    private String formatSize(String input) {
        return input;
    }

    private String formatBitrate(String input) {
        double kbps = Long.parseLong(input) / 1000D;
        return String.format("%.2f", kbps) + " kbps";
    }

    private String formatFramerate(String input) {
        double fps = Double.parseDouble(input);
        return String.format("%.2f", fps) + " fps";
    }
}
