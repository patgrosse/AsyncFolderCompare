package de.patgrosse.asyncfoldercompare.filter.folders;

import de.patgrosse.asyncfoldercompare.entities.filesystem.RealFolder;
import de.patgrosse.asyncfoldercompare.filter.Filter;

public class HiddenFolderFilter extends Filter<RealFolder> {

    @Override
    public boolean isObjectFiltered(RealFolder object) {
        return object.getName().charAt(0) == '.';
    }

}
