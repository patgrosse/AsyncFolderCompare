package de.patgrosse.asyncfoldercompare.entities.compareresults;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.utils.CompareHelper;

import java.util.*;
import java.util.stream.Collectors;

public class CompleteFileCompareResultHolder {
    private CompleteObjectCompareResult total;
    private Map<ComparePlugin, PluginFileCompareResultHolder> pluginResults;

    public CompleteFileCompareResultHolder() {
        total = CompleteObjectCompareResult.MATCH;
        pluginResults = new TreeMap<>(Comparator.comparing(ComparePlugin::getName));
    }

    public void setPluginResults(ComparePlugin plugin, PluginFileCompareResultHolder results) {
        if (plugin == null || results == null) {
            throw new IllegalArgumentException();
        }
        pluginResults.put(plugin, results);
    }

    public PluginFileCompareResultHolder getPluginResult(ComparePlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException();
        }
        return pluginResults.get(plugin);
    }

    public PluginFileCompareResultHolder getPluginResultByName(String pluginName) {
        if (pluginName == null) {
            throw new IllegalArgumentException();
        }
        for (Map.Entry<ComparePlugin, PluginFileCompareResultHolder> pluginResult : pluginResults.entrySet()) {
            if (pluginResult.getKey().getName().equals(pluginName)) {
                return pluginResult.getValue();
            }
        }
        return null;
    }

    public Map<ComparePlugin, PluginFileCompareResultHolder> getPluginResults() {
        return Collections.unmodifiableMap(pluginResults);
    }

    public void calculateTotal() {
        List<PluginCompareResult> results = pluginResults.values().stream().map(PluginFileCompareResultHolder::getTotal).collect(Collectors.toList());
        total = CompareHelper.pluginResultToCompleteResult(CompareHelper.combineResults(results));
    }

    public CompleteObjectCompareResult getTotal() {
        return total;
    }

}
