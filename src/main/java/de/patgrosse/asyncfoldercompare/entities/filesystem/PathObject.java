package de.patgrosse.asyncfoldercompare.entities.filesystem;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;

import de.patgrosse.asyncfoldercompare.constants.CompareObjectType;

public abstract class PathObject {
    @Expose
    private String name;
    @Expose
    private CompareObjectType type;
    private List<String> relativePath;

    public PathObject(CompareObjectType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompareObjectType getType() {
        return type;
    }

    public void setType(CompareObjectType type) {
        this.type = type;
    }

    public List<String> getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(List<String> relativePath) {
        if (relativePath == null) {
            this.relativePath = new LinkedList<>();
        } else {
            this.relativePath = new LinkedList<>(relativePath);
        }
    }

    public List<String> generateRelativePathForChild() {
        List<String> newRelativePath = new LinkedList<>(getRelativePath());
        if (!(this instanceof RootFolder)) {
            newRelativePath.add(getName());
        }
        return newRelativePath;
    }

    @Override
    public String toString() {
        return getName();
    }
}
