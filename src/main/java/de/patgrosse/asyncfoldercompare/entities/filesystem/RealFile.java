package de.patgrosse.asyncfoldercompare.entities.filesystem;

import com.google.gson.annotations.Expose;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;
import de.patgrosse.asyncfoldercompare.utils.FileAttributeStorage;

public class RealFile extends PathObject implements IFile {
    @Expose
    private FileAttributeStorage attributes;

    public RealFile(RealFile old) {
        super(CompareObjectType.FILE, old.getName());
        attributes = new FileAttributeStorage(old.attributes);
    }

    public RealFile(String name) {
        super(CompareObjectType.FILE, name);
        attributes = new FileAttributeStorage();
    }

    public FileAttributeStorage getDataStorage() {
        return attributes;
    }

    public void setDataStorage(FileAttributeStorage dataStorage) {
        this.attributes = dataStorage;
    }
}
