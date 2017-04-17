package de.patgrosse.asyncfoldercompare.utils;

import com.google.gson.annotations.Expose;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FileAttributeStorage {
    @Expose
    private Map<CompareCheckReference, String> data;

    public FileAttributeStorage(FileAttributeStorage old) {
        this.data = new HashMap<>();
        for (Entry<CompareCheckReference, String> entry : old.data.entrySet()) {
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
        collector.getAllAttributes().forEach(this::setData);
    }

    public void setData(CompareCheck check, String value) {
        if (check == null || value == null) {
            throw new IllegalArgumentException();
        }
        data.put(new CompareCheckReference(check.getPlugin().getName(), check.getKeyName()), value);
    }

    public String getData(CompareCheck check) {
        return data.get(new CompareCheckReference(check.getPlugin().getName(), check.getKeyName()));
    }

    public String getData(CompareCheckReference checkRef) {
        return data.get(checkRef);
    }

    public Map<CompareCheckReference, String> getAllData() {
        return Collections.unmodifiableMap(data);
    }
}
