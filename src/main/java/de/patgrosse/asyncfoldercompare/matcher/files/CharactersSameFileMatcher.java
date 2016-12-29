package de.patgrosse.asyncfoldercompare.matcher.files;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFile;

public class CharactersSameFileMatcher extends SingleFileMatcher {

    public CharactersSameFileMatcher() {
        super("CharactersSameFileMatcher", MatchStrategy.ONCE);
    }

    @Override
    public boolean filesMatch(RealFile oldFile, RealFile newFile) {
        String charsOld = oldFile.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String charsNew = newFile.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return charsOld.equals(charsNew);
    }
}
