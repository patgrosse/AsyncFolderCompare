package de.patgrosse.asyncfoldercompare.entities.filesystem.result;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RootFolder;

import java.util.LinkedList;

public class RootResultFolder extends ResultFolder implements RootFolder {

    public RootResultFolder() {
        super(null);
        setRelativePath(new LinkedList<>());
    }

}
