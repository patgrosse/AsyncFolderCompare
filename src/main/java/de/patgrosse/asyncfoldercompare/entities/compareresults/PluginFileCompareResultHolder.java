package de.patgrosse.asyncfoldercompare.entities.compareresults;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class PluginFileCompareResultHolder {
    private PluginCompareResult total;
    private Map<CompareCheck, PluginCompareResult> subResults;

    public PluginFileCompareResultHolder(PluginCompareResult total, Map<CompareCheck, PluginCompareResult> subResults) {
        if (total == null || subResults == null) {
            throw new IllegalArgumentException();
        }
        this.total = total;
        setSubResults(subResults);
    }

    public PluginCompareResult getTotal() {
        return total;
    }

    public void setTotal(PluginCompareResult total) {
        if (total == null) {
            throw new IllegalArgumentException();
        }
        this.total = total;
    }

    public Map<CompareCheck, PluginCompareResult> getSubResults() {
        return Collections.unmodifiableMap(subResults);
    }

    public void setSubResults(Map<CompareCheck, PluginCompareResult> subResults) {
        if (subResults == null) {
            throw new IllegalArgumentException();
        }
        this.subResults = new TreeMap<>(Comparator.comparing(CompareCheck::getDisplayName));
        this.subResults.putAll(subResults);
    }

}
