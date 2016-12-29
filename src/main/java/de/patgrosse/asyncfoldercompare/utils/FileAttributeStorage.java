package de.patgrosse.asyncfoldercompare.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.Expose;

public class FileAttributeStorage {
    @Expose
    private Map<String, String> data;

    public FileAttributeStorage(FileAttributeStorage old) {
        this.data = new HashMap<>();
        for (Entry<String, String> entry : old.data.entrySet()) {
            this.data.put(entry.getKey(), entry.getValue());
        }
    }

    public FileAttributeStorage() {
        data = new HashMap<>();
    }

    public void inputFromCollector(FileAttributeCollector collector) {
        if (collector == null) {
            throw new IllegalArgumentException();
        }
        data.putAll(collector.getAllAttributes());
    }

    public void setData(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        data.put(key, value);
    }

    public String getData(String key) {
        return data.get(key);
    }

    public Map<String, String> getAllData() {
        return Collections.unmodifiableMap(data);
    }
}
