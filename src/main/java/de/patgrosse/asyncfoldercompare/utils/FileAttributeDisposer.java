package de.patgrosse.asyncfoldercompare.utils;

import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;

import java.util.Map;

public class FileAttributeDisposer {
    private Map<CompareCheckReference, String> attributes;

    public FileAttributeDisposer(Map<CompareCheckReference, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(CompareCheck check) {
        return attributes.get(new CompareCheckReference(check.getPlugin().getName(), check.getKeyName()));
    }

    public String getAttribute(CompareCheckReference checkRef) {
        return attributes.get(checkRef);
    }
}
