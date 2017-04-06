package de.patgrosse.asyncfoldercompare.matcher.files;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.matcher.MatchCallback;

public abstract class SingleFileMatcher extends FileMatcher {

    public SingleFileMatcher(String name, MatchStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void matchObjects(Collection<RealFile> oldFiles, Collection<RealFile> newFiles,
                             MatchCallback<RealFile> callback) {
        List<RealFile> oldFilesList = new LinkedList<>(oldFiles);
        List<RealFile> newFilesList = new LinkedList<>(newFiles);
        for (int i = 0; i < oldFilesList.size(); i++) {
            for (int w = i; w < newFilesList.size(); w++) {
                RealFile oldFile = oldFilesList.get(i);
                RealFile newFile = newFilesList.get(w);
                if (filesMatch(oldFile, newFile)) {
                    callback.objectsMatched(oldFile.getName(), oldFile, newFile);
                }
            }
        }
    }

    public abstract boolean filesMatch(RealFile oldFile, RealFile newFile);
}
