package de.patgrosse.asyncfoldercompare.entities.filesystem;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;

public class ResultFolder extends ResultPathObject<RealFolder> implements IFolder<ResultFolder, ResultFile> {
    private Map<String, ResultFolder> containedFolders;
    private Map<String, ResultFile> containedFiles;

    public ResultFolder(String name) {
        super(CompareObjectType.FOLDER, name);
        containedFolders = new TreeMap<>();
        containedFiles = new TreeMap<>();
    }

    @Override
    public boolean addContainedFolder(ResultFolder containedFolder) {
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
    public boolean removeContainedFolder(ResultFolder containedFolder) {
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
    public Collection<ResultFolder> getContainedFolders() {
        return containedFolders.values();
    }

    @Override
    public ResultFolder getContainedFolder(String folderName) {
        if (folderName == null) {
            throw new IllegalArgumentException();
        }
        return containedFolders.get(folderName);
    }

    @Override
    public boolean addContainedFile(ResultFile containedFile) {
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
    public boolean removeContainedFile(ResultFile containedFile) {
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
    public Collection<ResultFile> getContainedFiles() {
        return containedFiles.values();
    }

    @Override
    public ResultFile getContainedFile(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException();
        }
        return containedFiles.get(fileName);
    }
}
