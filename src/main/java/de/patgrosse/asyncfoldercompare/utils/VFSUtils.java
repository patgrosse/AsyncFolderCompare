package de.patgrosse.asyncfoldercompare.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.patgrosse.asyncfoldercompare.entities.filesystem.PathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;
import de.patgrosse.asyncfoldercompare.entities.storage.Credentials;
import de.patgrosse.asyncfoldercompare.entities.storage.ScanSession;
import de.patgrosse.asyncfoldercompare.matcher.files.KodiLevenshteinFileMatcher;
import de.patgrosse.asyncfoldercompare.matcher.folders.KodiLevenshteinFolderMatcher;
import de.patgrosse.asyncfoldercompare.plugins.impl.SizePlugin;
import de.patgrosse.asyncfoldercompare.plugins.impl.VideoSizePlugin;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.ByteTransferCallback;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.NameScope;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

import de.patgrosse.asyncfoldercompare.filter.files.HiddenFileFilter;
import de.patgrosse.asyncfoldercompare.filter.folders.HiddenFolderFilter;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.MultiFileTransferCallback;

public final class VFSUtils {
    private VFSUtils() {
    }

    public static FileObject resolveVFS(String vfsURI, Credentials cred)
            throws FileNotFoundException, FileSystemException {
        StaticUserAuthenticator sua = new StaticUserAuthenticator(cred.getDomain(), cred.getUser(), cred.getPassword());
        FileSystemOptions opts = new FileSystemOptions();
        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, sua);
        return VFS.getManager().resolveFile(vfsURI, opts);
    }

    public static FileObject getTargetObject() throws FileSystemException {
        return VFS.getManager().resolveFile(new File(".").getAbsolutePath());
    }

    public static FileObject resolveFile(PathObject pathObject, FileObject foRoot) throws FileSystemException {
        return resolvePath(pathObject, foRoot, false);
    }

    public static FileObject resolveFolder(PathObject pathObject, FileObject foRoot) throws FileSystemException {
        return resolvePath(pathObject, foRoot, true);
    }

    private static FileObject resolvePath(PathObject pathObject, FileObject foRoot, boolean resolveFolder)
            throws FileSystemException {
        List<String> relativePath = new LinkedList<>(pathObject.getRelativePath());
        Iterator<String> iter = relativePath.iterator();
        FileObject path = foRoot;
        while (iter.hasNext()) {
            String next = iter.next();
            if (!next.equals("")) {
                path = path.resolveFile(next);
            }
        }
        if (resolveFolder) {
            return path;
        } else {
            return path.resolveFile(pathObject.getName());
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void copyFile(FileObject srcFile, FileObject destFile, ByteTransferCallback fileCallback)
            throws IOException {
        try (InputStream sourceFileIn = srcFile.getContent().getInputStream()) {
            try (OutputStream destinationFileOut = destFile.getContent().getOutputStream()) {
                Util.copyStream(sourceFileIn, destinationFileOut, Util.DEFAULT_COPY_BUFFER_SIZE,
                        srcFile.getContent().getSize(), new CopyStreamListener() {

                            @Override
                            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred,
                                                         long streamSize) {
                                if (fileCallback != null) {
                                    fileCallback.bytesTransferred(totalBytesTransferred, bytesTransferred, streamSize);
                                }
                            }

                            @Override
                            public void bytesTransferred(CopyStreamEvent event) {
                            }
                        });
            }
        }
    }

    public static Pair<RootRealFolder, FileObject> parseUserInput(FileTreeComparator comp, String inputURI,
                                                                  boolean isJSONFile, Credentials cred) throws IOException {
        if (isJSONFile) {
            ScanSession session = GsonUtils.readScanSessionFromJSON(inputURI, cred);
            return Pair.of(session.getScannedFolder(), null);
        } else {
            FileObject fo = VFSUtils.resolveVFS(inputURI, cred);
            return Pair.of(comp.mapFolder(fo), fo);
        }
    }

    public static FileTreeComparator createTreeComparator() {
        FileTreeComparator comp = new FileTreeComparator(new KodiLevenshteinFileMatcher(),
                new KodiLevenshteinFolderMatcher(), new HiddenFileFilter(), new HiddenFolderFilter());
        // comp.enablePlugin(new LastModifiedPlugin());
        comp.enablePlugin(new SizePlugin());
        comp.enablePlugin(new VideoSizePlugin());
        return comp;
    }

    public static void performCopyTaskWithCallback(QueuedCopyTask task, MultiFileTransferCallback callback)
            throws IOException {
        if (!task.getSource().exists()) {
            throw new FileSystemException("vfs.provider/copy-missing-file.error", task.getSource());
        }

        // Locate the files to copy across
        final ArrayList<FileObject> files = new ArrayList<>();
        task.getSource().findFiles(new AllFileSelector(), false, files);

        int amountOfFiles = 0;
        int currentFileNumber = 0;
        for (FileObject fo : files) {
            if (fo.getType().hasContent()) {
                amountOfFiles++;
            }
        }

        final int amountOfFilesF = amountOfFiles;

        // Copy everything across
        for (final FileObject srcFile : files) {
            // Determine the destination file
            final String relPath = task.getSource().getName().getRelativeName(srcFile.getName());
            final FileObject destFile = task.getDestination().resolveFile(relPath, NameScope.DESCENDENT_OR_SELF);

            // Clean up the destination file, if necessary
            if (destFile.exists() && destFile.getType() != srcFile.getType()) {
                // The destination file exists, and is not of the same type,
                // so delete it
                destFile.deleteAll();
            }

            // Copy across
            if (srcFile.getType().hasContent()) {
                currentFileNumber++;
                final int currentFileNumberF = currentFileNumber;
                callback.nextFile(currentFileNumberF, amountOfFilesF, srcFile);
                VFSUtils.copyFile(srcFile, destFile, callback::bytesTransferred);
            } else if (srcFile.getType().hasChildren()) {
                destFile.createFolder();
            }
        }
    }

}
