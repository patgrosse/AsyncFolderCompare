package de.patgrosse.asyncfoldercompare.matcher.folders;

public class KodiLevenshteinFolderMatcher extends LevenshteinFolderMatcher {

    public KodiLevenshteinFolderMatcher() {
        super("KodiFileMatcher");
    }

    @Override
    public String prepareFolderName(String foldername) {
        return super.prepareFolderName(foldername).replaceAll("\\((?:\\d{4}|\\d{2})\\)$", "").trim();
    }
}
