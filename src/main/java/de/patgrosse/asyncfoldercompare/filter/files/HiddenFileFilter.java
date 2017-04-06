package de.patgrosse.asyncfoldercompare.filter.files;

import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.filter.Filter;

public class HiddenFileFilter extends Filter<RealFile> {

    @Override
    public boolean isObjectFiltered(RealFile object) {
        return object.getName().charAt(0) == '.';
    }

}
