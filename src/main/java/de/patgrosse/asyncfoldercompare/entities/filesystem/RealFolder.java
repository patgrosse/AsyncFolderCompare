package de.patgrosse.asyncfoldercompare.entities.filesystem;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.Expose;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;

public class RealFolder extends PathObject implements IFolder<RealFolder, RealFile> {
    @Expose
    private Map<String, RealFolder> containedFolders;
    @Expose
    private Map<String, RealFile> containedFiles;

    public RealFolder(RealFolder old) {
        this(old.getName());
    }

    public RealFolder(String name) {
        super(CompareObjectType.FOLDER, name);
        containedFolders = new TreeMap<>();
        containedFiles = new TreeMap<>();
    }

    @Override
    public boolean addContainedFolder(RealFolder containedFolder) {
        if (containedFolder == null) {
            throw new IllegalArgumentException();
        }
        if (!isFolderContained(containedFolder.getName())) {
            containedFolders.put(containedFolder.getName(), containedFolder);
            containedFolder.setRelativePath(generateRelativePathForChild());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeContainedFolder(RealFolder containedFolder) {
        if (containedFolder == null) {
            throw new IllegalArgumentException();
        }
        if (containedFolders.remove(containedFolder.getName()) != null) {
            containedFolder.setRelativePath(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFolderContained(String folderName) {
        if (folderName == null) {
            throw new IllegalArgumentException();
        }
        return containedFolders.containsKey(folderName);
    }

    @Override
    public Collection<RealFolder> getContainedFolders() {
        return containedFolders.values();
    }

    @Override
    public RealFolder getContainedFolder(String folderName) {
        if (folderName == null) {
            throw new IllegalArgumentException();
        }
        return containedFolders.get(folderName);
    }

    @Override
    public boolean addContainedFile(RealFile containedFile) {
        if (containedFile == null) {
            throw new IllegalArgumentException();
        }
        if (!isFileContained(containedFile.getName())) {
            containedFiles.put(containedFile.getName(), containedFile);
            containedFile.setRelativePath(generateRelativePathForChild());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeContainedFile(RealFile containedFile) {
        if (containedFile == null) {
            throw new IllegalArgumentException();
        }
        if (containedFiles.remove(containedFile.getName()) != null) {
            containedFile.setRelativePath(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFileContained(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException();
        }
        return containedFiles.containsKey(fileName);
    }

    @Override
    public Collection<RealFile> getContainedFiles() {
        return containedFiles.values();
    }

    @Override
    public RealFile getContainedFile(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException();
        }
        return containedFiles.get(fileName);
    }
}
