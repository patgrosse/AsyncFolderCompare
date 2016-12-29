package de.patgrosse.asyncfoldercompare.entities.filesystem;

import java.util.LinkedList;

public class RootCompareFolder extends RealFolder implements RootFolder {

    public RootCompareFolder() {
        super((String) null);
        setRelativePath(new LinkedList<>());
    }

}
