package de.patgrosse.asyncfoldercompare.plugins.impl;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import de.patgrosse.asyncfoldercompare.utils.CompareHelper;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeCollector;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeDisposer;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor.Type;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VideoPlugin extends ComparePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(VideoPlugin.class);
    private static final String SIZE_SPLIT_STRING = " x ";
    private static final String KEY_VIDEO_SIZE = "videosize";
    private static final String KEY_BITRATE = "bitrate";
    private static final String KEY_FRAME_RATE = "fps";

    public VideoPlugin() {
        super("VideoPlugin", Arrays.asList(
                new CompareCheck(KEY_VIDEO_SIZE, "Video size"),
                new CompareCheck(KEY_BITRATE, "Bit rate"),
                new CompareCheck(KEY_FRAME_RATE, "Frame rate")
        ));
    }

    @Override
    public void generateDataForFile(FileObject file, FileAttributeCollector collector) throws Exception {
        Triple<Demuxer, DemuxerStream, Decoder> humbleStream = openHumble(file);
        if (humbleStream != null) {
            collector.setAttribute(KEY_VIDEO_SIZE, generateDataSize(humbleStream.getRight()));
            collector.setAttribute(KEY_BITRATE, generateDataBitrate(humbleStream.getLeft()));
            collector.setAttribute(KEY_FRAME_RATE, generateDataFramerate(humbleStream.getMiddle()));
            closeHumble(humbleStream.getLeft());
        }
    }

    @Override
    public PluginFileCompareResultHolder compareFiles(FileAttributeDisposer disposerOld, FileAttributeDisposer disposerNew) {
        PluginCompareResult sizeResult = compareSizes(disposerOld.getAttribute(KEY_VIDEO_SIZE), disposerNew.getAttribute(KEY_VIDEO_SIZE));
        PluginCompareResult bitrateResult = compareBitrate(disposerOld.getAttribute(KEY_BITRATE), disposerNew.getAttribute(KEY_BITRATE));
        PluginCompareResult framerateResult = compareFramerate(disposerOld.getAttribute(KEY_FRAME_RATE), disposerNew.getAttribute(KEY_FRAME_RATE));

        Map<String, PluginCompareResult> fullResult = new HashMap<>();
        fullResult.put(KEY_VIDEO_SIZE, sizeResult);
        fullResult.put(KEY_BITRATE, bitrateResult);
        fullResult.put(KEY_FRAME_RATE, framerateResult);
        PluginCompareResult total = CompareHelper.combineResults(Arrays.asList(sizeResult, bitrateResult, framerateResult));
        return new PluginFileCompareResultHolder(total, fullResult);
    }

    private Triple<Demuxer, DemuxerStream, Decoder> openHumble(FileObject file) throws IOException, InterruptedException {
        final Demuxer demuxer = Demuxer.make();
        try {
            demuxer.open(file.getPublicURIString(), null, false, true, null, null);
            for (int i = 0; i < demuxer.getNumStreams(); i++) {
                DemuxerStream stream = demuxer.getStream(0);
                Decoder coder = stream.getDecoder();
                if (coder.getCodecType() == Type.MEDIA_VIDEO) {
                    return Triple.of(demuxer, stream, coder);
                }
            }
            LOG.warn("No video stream found");
            demuxer.close();
        } catch (Exception e) {
            LOG.warn("Could not open file for humble: " + e.getMessage());
        }
        return null;
    }

    private void closeHumble(Demuxer demuxer) throws IOException, InterruptedException {
        demuxer.close();
    }

    private String generateDataSize(Decoder coder) {
        return coder.getWidth() + SIZE_SPLIT_STRING + coder.getHeight();
    }

    private String generateDataBitrate(Demuxer demuxer) {
        return Integer.toString(demuxer.getBitRate());
    }

    private String generateDataFramerate(DemuxerStream stream) {
        return Double.toString(stream.getFrameRate().getValue());
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
}
