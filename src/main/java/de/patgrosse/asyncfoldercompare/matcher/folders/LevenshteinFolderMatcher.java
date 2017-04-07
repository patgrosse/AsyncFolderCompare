package de.patgrosse.asyncfoldercompare.matcher.folders;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFolder;
import de.patgrosse.asyncfoldercompare.matcher.MatchCallback;
import de.patgrosse.asyncfoldercompare.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LevenshteinFolderMatcher extends FolderMatcher {
    private static final int DIFF_CHARS_LIMIT = 4;
    private static final double DIFF_PERCENTAGE_LIMIT = 0.2;
    private static final Logger LOG = LoggerFactory.getLogger(LevenshteinFolderMatcher.class);

    public LevenshteinFolderMatcher() {
        super("LevenshteinFolderMatcher", MatchStrategy.MANY);
    }

    protected LevenshteinFolderMatcher(String subName) {
        super(subName, MatchStrategy.MANY);
    }

    @Override
    public void matchObjects(Collection<RealFolder> oldFolders, Collection<RealFolder> newFolders,
                             MatchCallback<RealFolder> callback) {
        Map<Pair<RealFolder, RealFolder>, Pair<Integer, Integer>> levenshteinMatches = new HashMap<>();
        for (RealFolder oldFolder : oldFolders) {
            for (RealFolder newFolder : newFolders) {
                String oldName = prepareFolderName(oldFolder.getName());
                String newName = prepareFolderName(newFolder.getName());
                levenshteinMatches.put(Pair.of(oldFolder, newFolder),
                        Pair.of(oldName.length(), StringUtils.getLevenshteinDistance(oldName, newName)));
            }
        }
        Map<Pair<RealFolder, RealFolder>, Pair<Integer, Integer>> matchesSorted = MapUtils
                .sortByValue(levenshteinMatches, false, Comparator.comparing(Pair::getRight));
        while (!matchesSorted.isEmpty()) {
            Entry<Pair<RealFolder, RealFolder>, Pair<Integer, Integer>> s = matchesSorted.entrySet().iterator().next();
            if (s.getValue().getRight() > DIFF_CHARS_LIMIT) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(s.getKey().getLeft().getName() + " : " + s.getKey().getRight().getName()
                            + " : chars limited with (length, diff) " + s.getValue());
                }
                break;
            }
            if (((double) s.getValue().getRight()) / s.getValue().getLeft() > DIFF_PERCENTAGE_LIMIT) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(s.getKey().getLeft().getName() + " : " + s.getKey().getRight().getName()
                            + " : percentage limited with (length, diff) " + s.getValue());
                }
                matchesSorted.remove(s.getKey());
                continue;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(s.getKey().getLeft().getName() + " : " + s.getKey().getRight().getName()
                        + " : matched with (length, diff) " + s.getValue());
            }
            callback.objectsMatched(s.getKey().getLeft().getName(), s.getKey().getLeft(), s.getKey().getRight());
            MapUtils.deleteFromPairMapWhereAorBEqual(matchesSorted, s.getKey().getLeft(), s.getKey().getRight());
        }
    }

    public String prepareFolderName(String folderName) {
        return folderName.trim().toLowerCase();
    }
}
