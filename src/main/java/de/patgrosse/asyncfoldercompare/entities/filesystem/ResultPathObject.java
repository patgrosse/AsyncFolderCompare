package de.patgrosse.asyncfoldercompare.entities.filesystem;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;
import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;

public abstract class ResultPathObject<T extends PathObject> extends PathObject implements ResultObject {
    private CompleteObjectCompareResult compareResult;
    private T correspondingOld, correnspondingNew;

    public ResultPathObject(CompareObjectType type, String name) {
        super(type, name);
    }

    @Override
    public CompleteObjectCompareResult getCompareResult() {
        return compareResult;
    }

    @Override
    public void setCompareResult(CompleteObjectCompareResult compareResult) {
        this.compareResult = compareResult;
    }

    public T getCorrespondingOld() {
        return correspondingOld;
    }

    public void setCorrespondingOld(T correspondingOld) {
        this.correspondingOld = correspondingOld;
    }

    public T getCorrespondingNew() {
        return correnspondingNew;
    }

    public void setCorrespondingNew(T correnspondingNew) {
        this.correnspondingNew = correnspondingNew;
    }
}
