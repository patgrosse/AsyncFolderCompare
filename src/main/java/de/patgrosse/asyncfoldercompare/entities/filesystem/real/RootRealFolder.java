package de.patgrosse.asyncfoldercompare.entities.filesystem.real;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RootFolder;

import java.util.LinkedList;

public class RootRealFolder extends RealFolder implements RootFolder {

    public RootRealFolder() {
        super((String) null);
        setRelativePath(new LinkedList<>());
    }

}
