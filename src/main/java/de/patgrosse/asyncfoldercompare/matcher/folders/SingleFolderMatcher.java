package de.patgrosse.asyncfoldercompare.matcher.folders;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFolder;
import de.patgrosse.asyncfoldercompare.matcher.MatchCallback;

public abstract class SingleFolderMatcher extends FolderMatcher {

    public SingleFolderMatcher(String name, MatchStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void matchObjects(Collection<RealFolder> oldFolders, Collection<RealFolder> newFolders,
                             MatchCallback<RealFolder> callback) {
        List<RealFolder> oldFoldersList = new LinkedList<>(oldFolders);
        List<RealFolder> newFoldersList = new LinkedList<>(newFolders);
        for (int i = 0; i < oldFoldersList.size(); i++) {
            for (int w = i; w < newFoldersList.size(); w++) {
                RealFolder oldFolder = oldFoldersList.get(i);
                RealFolder newFolder = newFoldersList.get(w);
                if (foldersMatch(oldFolder, newFolder)) {
                    callback.objectsMatched(oldFolder.getName(), oldFolder, newFolder);
                }
            }
        }
    }

    public abstract boolean foldersMatch(RealFolder oldFolder, RealFolder newFolder);
}
