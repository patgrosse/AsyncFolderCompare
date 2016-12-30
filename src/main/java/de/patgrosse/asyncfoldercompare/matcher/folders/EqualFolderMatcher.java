package de.patgrosse.asyncfoldercompare.matcher.folders;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;

public class EqualFolderMatcher extends SingleFolderMatcher {

    public EqualFolderMatcher() {
        super("FolderMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean foldersMatch(RealFolder oldFolder, RealFolder newFolder) {
        return oldFolder.getName().equals(newFolder.getName());
    }

}
