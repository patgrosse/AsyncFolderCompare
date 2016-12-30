package de.patgrosse.asyncfoldercompare.matcher.files;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFile;

public class StartsWithFileMatcher extends SingleFileMatcher {

    public StartsWithFileMatcher() {
        super("StartsWithFileMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean filesMatch(RealFile oldFile, RealFile newFile) {
        return oldFile.getName().startsWith(newFile.getName()) || newFile.getName().startsWith(oldFile.getName());
    }
}
