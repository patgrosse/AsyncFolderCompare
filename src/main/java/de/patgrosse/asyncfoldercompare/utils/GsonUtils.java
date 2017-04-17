package de.patgrosse.asyncfoldercompare.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;
import de.patgrosse.asyncfoldercompare.entities.storage.Credentials;
import de.patgrosse.asyncfoldercompare.entities.storage.LastSettings;
import de.patgrosse.asyncfoldercompare.entities.storage.ScanSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class GsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GsonUtils.class);
    private static Gson gsonInstance;

    private GsonUtils() {
    }

    public static Gson getGson() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .enableComplexMapKeySerialization()
                    .create();
        }
        return gsonInstance;
    }

    public static String serializeScanSession(ScanSession folder) {
        return getGson().toJson(folder);
    }

    public static ScanSession deserializeScanSession(String serializedSession) {
        ScanSession sess = getGson().fromJson(serializedSession, ScanSession.class);
        if (sess != null && sess.getScannedFolder() != null) {
            createRelativePaths(sess.getScannedFolder());
        }
        return sess;
    }

    private static void createRelativePaths(RootRealFolder folder) {
        List<String> relativePath = new LinkedList<>();
        folder.setRelativePath(relativePath);
        createRelativePaths(folder, relativePath);
    }

    public static void createRelativePaths(RealFolder folder, List<String> relativePath) {
        for (RealFile obj : folder.getContainedFiles()) {
            obj.setRelativePath(relativePath);
        }
        for (RealFolder obj : folder.getContainedFolders()) {
            obj.setRelativePath(relativePath);
            List<String> newRelativePath = new LinkedList<>(relativePath);
            newRelativePath.add(obj.getName());
            createRelativePaths(obj, newRelativePath);
        }
    }

    public static LastSettings readLastSettings() {
        try {
            File file = getCreateSettingsFile();
            LOG.info("Reading settings from file " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            return getGson().fromJson(fr, LastSettings.class);
        } catch (FileNotFoundException e) {
            return new LastSettings();
        } catch (IOException e) {
            LOG.error("Could not read settings", e);
            System.exit(1);
            return null;
        }
    }

    private static File getCreateSettingsFile() throws IOException {
        Path path;
        if (SystemUtils.IS_OS_WINDOWS) {
            path = Files.createDirectories(
                    FileSystems.getDefault().getPath(
                            System.getenv("APPDATA"),
                            "AsyncFolderCompare"
                    )
            );
        } else if (SystemUtils.IS_OS_MAC) {
            path = Files.createDirectories(
                    FileSystems.getDefault().getPath(
                            System.getProperty("user.home"),
                            "Library", "Application Support", "AsyncFolderCompare"
                    )
            );
        } else if (SystemUtils.IS_OS_UNIX) {
            path = Files.createDirectories(
                    FileSystems.getDefault().getPath(
                            System.getProperty("user.home"),
                            ".local", "share", "AsyncFolderCompare"
                    )
            );
        } else {
            throw new IOException("Unknown operating system");
        }
        return new File(path.toFile(), "afc_settings.json");
    }

    public static void saveLastSettings(LastSettings lastSettings) throws IOException {
        File file = getCreateSettingsFile();
        LOG.info("Saving settings to file " + file.getAbsolutePath());
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.write(getGson().toJson(lastSettings));
        writer.flush();
        writer.close();
    }

    public static ScanSession readScanSessionFromJSON(String jsonVFSURI, Credentials jsonCred) throws IOException {
        FileObject fo = VFSUtils.resolveVFS(jsonVFSURI, jsonCred);
        StringWriter writer = new StringWriter();
        IOUtils.copy(fo.getContent().getInputStream(), writer, Charset.forName("UTF-8"));
        String jsonString = writer.toString();
        return deserializeScanSession(jsonString);
    }

    public static void saveFolderToJSON(FileTreeComparator comp, RootRealFolder mappedFolder, String jsonURI,
                                        Credentials jsonCred) throws IOException {
        PrintWriter writer;
        if (jsonURI != null && jsonURI.length() != 0) {
            FileObject jsonFile = VFSUtils.resolveVFS(jsonURI, jsonCred);
            writer = new PrintWriter(jsonFile.getContent().getOutputStream());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("YY_MM_dd_HH_mm_ss");
            writer = new PrintWriter("testJSONOutput." + sdf.format(new Date()) + ".json", "UTF-8");
        }
        writer.write(serializeScanSession(comp.createScanSession(mappedFolder)));
        writer.flush();
        writer.close();
    }

    public static void saveFolderToJSON(FileTreeComparator comp, String folderURI, Credentials folderCred,
                                        String jsonURI, Credentials jsonCred) throws IOException {
        FileObject fo = VFSUtils.resolveVFS(folderURI, folderCred);
        RootRealFolder mappedFolder = comp.mapFolder(fo);
        saveFolderToJSON(comp, mappedFolder, jsonURI, jsonCred);
    }
}
