package de.patgrosse.asyncfoldercompare.entities.filesystem;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;
import de.patgrosse.asyncfoldercompare.entities.compareresults.CompleteFileCompareResultHolder;

public class ResultFile extends ResultPathObject<RealFile> implements IFile {
    private CompleteFileCompareResultHolder fullResult;

    public ResultFile(String name) {
        super(CompareObjectType.FILE, name);
    }

    public CompleteFileCompareResultHolder getFullResult() {
        return fullResult;
    }

    public void setFullResult(CompleteFileCompareResultHolder fullResult) {
        if (fullResult == null) {
            throw new IllegalArgumentException();
        }
        this.fullResult = fullResult;
        setCompareResult(fullResult.getTotal());
    }
}
