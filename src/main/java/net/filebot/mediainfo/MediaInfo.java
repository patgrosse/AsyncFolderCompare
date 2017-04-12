package net.filebot.mediainfo;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import org.apache.commons.vfs2.RandomAccessContent;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class MediaInfo implements Closeable {

    private Pointer handle;

    public MediaInfo() {
        try {
            handle = MediaInfoLibrary.INSTANCE.New();
        } catch (LinkageError e) {
            throw new MediaInfoException(e);
        }
    }

    public synchronized MediaInfo open(File file) throws IOException, IllegalArgumentException {
        if (!file.isFile() || file.length() < 64 * 1024) {
            throw new IllegalArgumentException("Invalid media file: " + file);
        }

        String path = file.getCanonicalPath();

        // on Mac files that contain accents cannot be opened via JNA WString file paths due to encoding differences so we use the buffer interface instead for these files
        if (Platform.isMac() && !US_ASCII.newEncoder().canEncode(path)) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                if (openViaBuffer(raf)) {
                    return this;
                }
                throw new IOException("Failed to initialize media info buffer: " + path);
            }
        }

        // open via file path
        if (0 != MediaInfoLibrary.INSTANCE.Open(handle, new WString(path))) {
            return this;
        }
        throw new IOException("Failed to open media file: " + path);
    }

    private boolean openViaBuffer(RandomAccessFile f) throws IOException, IllegalArgumentException {
        byte[] buffer = new byte[4 * 1024 * 1024]; // use large buffer to reduce JNA calls
        int read;

        if (0 == MediaInfoLibrary.INSTANCE.Open_Buffer_Init(handle, f.length(), 0)) {
            return false;
        }

        do {
            read = f.read(buffer);
            int result = MediaInfoLibrary.INSTANCE.Open_Buffer_Continue(handle, buffer, read);
            if ((result & 8) == 8) {
                break;
            }

            long gotoPos = MediaInfoLibrary.INSTANCE.Open_Buffer_Continue_GoTo_Get(handle);
            if (gotoPos >= 0) {
                f.seek(gotoPos);
                MediaInfoLibrary.INSTANCE.Open_Buffer_Init(handle, f.length(), gotoPos);
            }
        } while (read > 0);

        MediaInfoLibrary.INSTANCE.Open_Buffer_Finalize(handle);
        return true;
    }

    public void openViaRandomAccessContent(RandomAccessContent cont) throws IOException {
        byte[] buffer = new byte[4 * 1024 * 1024]; // use large buffer to reduce JNA calls
        int read;

        if (cont.length() < 64 * 1024) {
            throw new IllegalArgumentException("Invalid media file");
        }

        if (0 == MediaInfoLibrary.INSTANCE.Open_Buffer_Init(handle, cont.length(), 0)) {
            throw new IOException("Could not open media file");
        }

        do {
            read = cont.getInputStream().read(buffer);
            int result = MediaInfoLibrary.INSTANCE.Open_Buffer_Continue(handle, buffer, read);
            if ((result & 8) == 8) {
                break;
            }

            long gotoPos = MediaInfoLibrary.INSTANCE.Open_Buffer_Continue_GoTo_Get(handle);
            if (gotoPos >= 0) {
                cont.seek(gotoPos);
                MediaInfoLibrary.INSTANCE.Open_Buffer_Init(handle, cont.length(), gotoPos);
            }
        } while (read > 0);

        MediaInfoLibrary.INSTANCE.Open_Buffer_Finalize(handle);
    }

    public synchronized String inform() {
        return MediaInfoLibrary.INSTANCE.Inform(handle).toString();
    }

    public String option(String option) {
        return option(option, "");
    }

    public synchronized String option(String option, String value) {
        return MediaInfoLibrary.INSTANCE.Option(handle, new WString(option), new WString(value)).toString();
    }

    public String get(StreamKind streamKind, int streamNumber, String parameter) {
        return get(streamKind, streamNumber, parameter, InfoKind.Text, InfoKind.Name);
    }

    public String get(StreamKind streamKind, int streamNumber, String parameter, InfoKind infoKind) {
        return get(streamKind, streamNumber, parameter, infoKind, InfoKind.Name);
    }

    public synchronized String get(StreamKind streamKind, int streamNumber, String parameter, InfoKind infoKind, InfoKind searchKind) {
        return MediaInfoLibrary.INSTANCE.Get(handle, streamKind.ordinal(), streamNumber, new WString(parameter), infoKind.ordinal(), searchKind.ordinal()).toString();
    }

    public String get(StreamKind streamKind, int streamNumber, int parameterIndex) {
        return get(streamKind, streamNumber, parameterIndex, InfoKind.Text);
    }

    public synchronized String get(StreamKind streamKind, int streamNumber, int parameterIndex, InfoKind infoKind) {
        return MediaInfoLibrary.INSTANCE.GetI(handle, streamKind.ordinal(), streamNumber, parameterIndex, infoKind.ordinal()).toString();
    }

    public synchronized int streamCount(StreamKind streamKind) {
        return MediaInfoLibrary.INSTANCE.Count_Get(handle, streamKind.ordinal(), -1);
    }

    public synchronized int parameterCount(StreamKind streamKind, int streamNumber) {
        return MediaInfoLibrary.INSTANCE.Count_Get(handle, streamKind.ordinal(), streamNumber);
    }

    @Override
    public synchronized void close() {
        MediaInfoLibrary.INSTANCE.Close(handle);
    }

    public synchronized void dispose() {
        if (handle == null) {
            return;
        }

        // delete handle
        MediaInfoLibrary.INSTANCE.Delete(handle);
        handle = null;
    }

    @Override
    protected void finalize() {
        dispose();
    }

    public enum StreamKind {
        General, Video, Audio, Text, Chapters, Image, Menu;
    }

    public enum InfoKind {
        /**
         * Unique name of parameter.
         */
        Name,

        /**
         * Value of parameter.
         */
        Text,

        /**
         * Unique name of measure unit of parameter.
         */
        Measure,

        Options,

        /**
         * Translated name of parameter.
         */
        Name_Text,

        /**
         * Translated name of measure unit.
         */
        Measure_Text,

        /**
         * More information about the parameter.
         */
        Info,

        /**
         * How this parameter is supported, could be N (No), B (Beta), R (Read only), W (Read/Write).
         */
        HowTo,

        /**
         * Domain of this piece of information.
         */
        Domain;
    }

    public static String version() {
        return staticOption("Info_Version");
    }

    public static String parameters() {
        return staticOption("Info_Parameters");
    }

    public static String codecs() {
        return staticOption("Info_Codecs");
    }

    public static String capacities() {
        return staticOption("Info_Capacities");
    }

    public static String staticOption(String option) {
        return staticOption(option, "");
    }

    public static String staticOption(String option, String value) {
        try {
            return MediaInfoLibrary.INSTANCE.Option(null, new WString(option), new WString(value)).toString();
        } catch (LinkageError e) {
            throw new MediaInfoException(e);
        }
    }

}
