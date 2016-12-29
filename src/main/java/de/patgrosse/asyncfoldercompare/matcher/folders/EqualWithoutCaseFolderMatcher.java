package de.patgrosse.asyncfoldercompare.matcher.folders;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;

public class EqualWithoutCaseFolderMatcher extends SingleFolderMatcher {

    public EqualWithoutCaseFolderMatcher() {
        super("EqualWithoutCaseFolderMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean foldersMatch(RealFolder oldFolder, RealFolder newFolder) {
        return oldFolder.getName().equalsIgnoreCase(newFolder.getName());
    }

}
