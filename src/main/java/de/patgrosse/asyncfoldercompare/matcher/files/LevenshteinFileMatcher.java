package de.patgrosse.asyncfoldercompare.matcher.files;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.matcher.MatchCallback;
import de.patgrosse.asyncfoldercompare.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFile;

public class LevenshteinFileMatcher extends FileMatcher {
    private static final int DIFF_LIMIT = 10;
    private static final Logger LOG = LogManager.getLogger();

    public LevenshteinFileMatcher() {
        super("LevenshteinFileMatcher", MatchStrategy.MANY);
    }

    protected LevenshteinFileMatcher(String subName) {
        super(subName, MatchStrategy.MANY);
    }

    @Override
    public void matchObjects(Collection<RealFile> oldFiles, Collection<RealFile> newFiles,
                             MatchCallback<RealFile> callback) {
        Map<Pair<RealFile, RealFile>, Integer> levenshteinMatches = new HashMap<>();
        for (RealFile oldFile : oldFiles) {
            for (RealFile newFile : newFiles) {
                String oldName = prepareFileName(oldFile.getName());
                String newName = prepareFileName(newFile.getName());
                if (fileExtensionsMatch(oldName, newName)) {
                    levenshteinMatches.put(Pair.of(oldFile, newFile),
                            StringUtils.getLevenshteinDistance(oldName, newName));
                }
            }
        }
        Map<Pair<RealFile, RealFile>, Integer> matchesSorted = MapUtils.sortNaturalByValue(levenshteinMatches, false);
        while (!matchesSorted.isEmpty()) {
            Entry<Pair<RealFile, RealFile>, Integer> s = matchesSorted.entrySet().iterator().next();
            if (s.getValue() > DIFF_LIMIT) {
                break;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(s.getKey().getLeft().getName() + " : " + s.getKey().getRight().getName() + " : "
                        + s.getValue());
            }
            callback.objectsMatched(s.getKey().getLeft().getName(), s.getKey().getLeft(), s.getKey().getRight());
            MapUtils.deleteFromPairMapWhereAorBEqual(matchesSorted, s.getKey().getLeft(), s.getKey().getRight());
        }
    }

    public String prepareFileName(String filename) {
        return filename.trim().toLowerCase();
    }

    private boolean fileExtensionsMatch(String oldFilename, String newFilename) {
        String[] splitOld = oldFilename.split("\\.");
        String[] splitNew = newFilename.split("\\.");
        if (splitOld.length == 1) {
            return splitNew.length == 1;
        }
        return splitNew.length != 1 && splitOld[splitOld.length - 1].equalsIgnoreCase(splitNew[splitNew.length - 1]);
    }

}
