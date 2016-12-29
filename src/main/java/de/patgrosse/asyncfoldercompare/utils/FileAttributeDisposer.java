package de.patgrosse.asyncfoldercompare.utils;

import java.util.Map;

public class FileAttributeDisposer {
    private Map<String, String> attributes;

    public FileAttributeDisposer(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }
}
