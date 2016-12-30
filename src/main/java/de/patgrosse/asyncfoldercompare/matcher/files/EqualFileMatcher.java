package de.patgrosse.asyncfoldercompare.matcher.files;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFile;

public class EqualFileMatcher extends SingleFileMatcher {

    public EqualFileMatcher() {
        super("EqualFileMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean filesMatch(RealFile oldFile, RealFile newFile) {
        return oldFile.getName().equals(newFile.getName());
    }
}
