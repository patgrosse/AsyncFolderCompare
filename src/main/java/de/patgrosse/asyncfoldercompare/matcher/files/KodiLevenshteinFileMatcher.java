package de.patgrosse.asyncfoldercompare.matcher.files;

public class KodiLevenshteinFileMatcher extends LevenshteinFileMatcher {

    public KodiLevenshteinFileMatcher() {
        super("KodiFileMatcher");
    }

    @Override
    public String prepareFileName(String filename) {
        return super.prepareFileName(filename).replaceAll("\\((?:\\d{4}|\\d{2})\\)$", "").trim();
    }
}
