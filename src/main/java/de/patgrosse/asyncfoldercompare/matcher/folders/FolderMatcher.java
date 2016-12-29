package de.patgrosse.asyncfoldercompare.matcher.folders;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;
import de.patgrosse.asyncfoldercompare.matcher.ObjectMatcher;

public abstract class FolderMatcher extends ObjectMatcher<RealFolder> {

    public FolderMatcher(String name, MatchStrategy strategy) {
        super(name, strategy);
    }
}
