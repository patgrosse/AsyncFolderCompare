package de.patgrosse.asyncfoldercompare.filter.folders;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;
import de.patgrosse.asyncfoldercompare.filter.Filter;

public class NoFolderFilter extends Filter<RealFolder> {

    @Override
    public boolean isObjectFiltered(RealFolder object) {
        return false;
    }

}
