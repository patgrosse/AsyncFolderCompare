package de.patgrosse.asyncfoldercompare.entities.compareresults;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.utils.CompareHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<PluginCompareResult> results = pluginResults.values().stream().map(PluginFileCompareResultHolder::getTotal).collect(Collectors.toList());
        total = CompareHelper.pluginResultToCompleteResult(CompareHelper.combineResults(results));
    }

    public CompleteObjectCompareResult getTotal() {
        return total;
    }

}
