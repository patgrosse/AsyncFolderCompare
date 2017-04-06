package de.patgrosse.asyncfoldercompare.matcher.files;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.matcher.ObjectMatcher;

public abstract class FileMatcher extends ObjectMatcher<RealFile> {

    public FileMatcher(String name, MatchStrategy strategy) {
        super(name, strategy);
    }
}
