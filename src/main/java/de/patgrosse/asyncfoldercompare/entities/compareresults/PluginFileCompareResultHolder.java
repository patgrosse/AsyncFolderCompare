package de.patgrosse.asyncfoldercompare.entities.compareresults;

import java.util.Map;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

public class PluginFileCompareResultHolder {
    private PluginCompareResult total;
    private Map<String, PluginCompareResult> subResults;

    public PluginFileCompareResultHolder(PluginCompareResult total, Map<String, PluginCompareResult> subResults) {
        if (total == null || subResults == null) {
            throw new IllegalArgumentException();
        }
        this.total = total;
        this.subResults = subResults;
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

    public Map<String, PluginCompareResult> getSubResults() {
        return subResults;
    }

    public void setSubResults(Map<String, PluginCompareResult> subResults) {
        if (subResults == null) {
            throw new IllegalArgumentException();
        }
        this.subResults = subResults;
    }

}
