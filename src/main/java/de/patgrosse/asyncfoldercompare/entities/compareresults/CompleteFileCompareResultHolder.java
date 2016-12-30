package de.patgrosse.asyncfoldercompare.entities.compareresults;

import java.util.HashMap;
import java.util.Map;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;

public class CompleteFileCompareResultHolder {
    private CompleteObjectCompareResult total;
    private Map<String, PluginFileCompareResultHolder> pluginResults;

    public CompleteFileCompareResultHolder() {
        total = CompleteObjectCompareResult.MATCH;
        pluginResults = new HashMap<>();
    }

    public void setPluginResults(String pluginName, PluginFileCompareResultHolder results) {
        if (pluginName == null || results == null) {
            throw new IllegalArgumentException();
        }
        pluginResults.put(pluginName, results);
    }

    public PluginFileCompareResultHolder getPluginResults(String pluginName) {
        if (pluginName == null) {
            throw new IllegalArgumentException();
        }
        return pluginResults.get(pluginName);
    }

    public void calculateTotal() {
        total = CompleteObjectCompareResult.MATCH;
        for (PluginFileCompareResultHolder pluginResult : pluginResults.values()) {
            switch (pluginResult.getTotal()) {
                case MATCH:
                    break;
                case DIFFER:
                    total = CompleteObjectCompareResult.DIFFER;
                    break;
                case PREFERNEW:
                    if (total == CompleteObjectCompareResult.MATCH) {
                        total = CompleteObjectCompareResult.PREFERNEW;
                    } else if (total != CompleteObjectCompareResult.PREFERNEW) {
                        total = CompleteObjectCompareResult.DIFFER;
                    }
                    break;
                case PREFEROLD:
                    if (total == CompleteObjectCompareResult.MATCH) {
                        total = CompleteObjectCompareResult.PREFEROLD;
                    } else if (total != CompleteObjectCompareResult.PREFEROLD) {
                        total = CompleteObjectCompareResult.DIFFER;
                    }
                    break;
                case UNDEFINED:
                    total = CompleteObjectCompareResult.UNDEFINED;
                    return;
                default:
                    throw new IllegalStateException("Unexpected value " + pluginResult.getTotal());
            }
        }
    }

    public CompleteObjectCompareResult getTotal() {
        return total;
    }

}
