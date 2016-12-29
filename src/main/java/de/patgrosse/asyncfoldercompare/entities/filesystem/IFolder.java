package de.patgrosse.asyncfoldercompare.entities.filesystem;

import java.util.Collection;

public interface IFolder<FolderType, FileType> {
    boolean addContainedFolder(FolderType containedFolder);

    boolean removeContainedFolder(FolderType containedFolder);

    boolean isFolderContained(String folderName);

    FolderType getContainedFolder(String folderName);

    Collection<FolderType> getContainedFolders();

    boolean addContainedFile(FileType containedFile);

    boolean removeContainedFile(FileType containedFile);

    boolean isFileContained(String fileName);

    FileType getContainedFile(String fileName);

    Collection<FileType> getContainedFiles();
}
