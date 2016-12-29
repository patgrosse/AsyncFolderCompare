package de.patgrosse.asyncfoldercompare.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RootCompareFolder;
import de.patgrosse.asyncfoldercompare.entities.storage.Credentials;
import de.patgrosse.asyncfoldercompare.entities.storage.LastSettings;
import de.patgrosse.asyncfoldercompare.entities.storage.ScanSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonUtils {
    private static Gson gsonInstance;

    private GsonUtils() {
    }

    public static Gson getGson() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
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

    private static void createRelativePaths(RootCompareFolder folder) {
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
            FileReader fr = new FileReader(new File("afc_settings.json"));
            return getGson().fromJson(fr, LastSettings.class);
        } catch (FileNotFoundException e) {
            return new LastSettings();
        }
    }

    public static void saveLastSettings(LastSettings lastSettings) throws IOException {
        PrintWriter writer = new PrintWriter("afc_settings.json", "UTF-8");
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

    public static void saveFolderToJSON(FileTreeComparator comp, RootCompareFolder mappedFolder, String jsonURI,
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
        RootCompareFolder mappedFolder = comp.mapFolder(fo);
        saveFolderToJSON(comp, mappedFolder, jsonURI, jsonCred);
    }
}
