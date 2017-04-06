package de.patgrosse.asyncfoldercompare.matcher.files;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;

public class EqualWithoutCaseFileMatcher extends SingleFileMatcher {

    public EqualWithoutCaseFileMatcher() {
        super("EqualWithoutCaseFileMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean filesMatch(RealFile oldFile, RealFile newFile) {
        return oldFile.getName().equalsIgnoreCase(newFile.getName());
    }
}
