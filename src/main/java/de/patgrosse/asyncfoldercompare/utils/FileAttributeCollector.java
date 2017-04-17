package de.patgrosse.asyncfoldercompare.utils;

import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileAttributeCollector {
    private Map<CompareCheck, String> attributes;

    public FileAttributeCollector() {
        this.attributes = new HashMap<>();
    }

    public void setAttribute(CompareCheck check, String value) {
        attributes.put(check, value);
    }

    protected Map<CompareCheck, String> getAllAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}
