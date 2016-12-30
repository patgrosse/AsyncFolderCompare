package de.patgrosse.asyncfoldercompare.entities.filesystem;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;

public interface ResultObject {
    CompleteObjectCompareResult getCompareResult();

    void setCompareResult(CompleteObjectCompareResult compareResult);

}
