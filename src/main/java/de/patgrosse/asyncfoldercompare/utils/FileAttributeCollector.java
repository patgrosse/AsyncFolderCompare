package de.patgrosse.asyncfoldercompare.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileAttributeCollector {
    private Map<String, String> attributes;

    public FileAttributeCollector() {
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    protected Map<String, String> getAllAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}
