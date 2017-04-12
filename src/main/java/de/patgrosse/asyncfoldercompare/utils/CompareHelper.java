package de.patgrosse.asyncfoldercompare.utils;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

import java.util.Collection;

public class CompareHelper {
    private CompareHelper() {
    }

    public static PluginCompareResult combineResults(Collection<PluginCompareResult> results) {
        PluginCompareResult total = PluginCompareResult.MATCH;
        for (PluginCompareResult result : results) {
            switch (result) {
                case MATCH:
                    break;
                case DIFFER:
                    total = PluginCompareResult.DIFFER;
                    break;
                case PREFERNEW:
                    if (total == PluginCompareResult.MATCH) {
                        total = PluginCompareResult.PREFERNEW;
                    } else if (total != PluginCompareResult.PREFERNEW) {
                        total = PluginCompareResult.DIFFER;
                    }
                    break;
                case PREFEROLD:
                    if (total == PluginCompareResult.MATCH) {
                        total = PluginCompareResult.PREFEROLD;
                    } else if (total != PluginCompareResult.PREFEROLD) {
                        total = PluginCompareResult.DIFFER;
                    }
                    break;
                case IGNORE:
                    break;
                case UNDEFINED:
                    total = PluginCompareResult.UNDEFINED;
                    return total;
                default:
                    throw new IllegalStateException("Unexpected value " + result);
            }
        }
        return total;
    }

    public static CompleteObjectCompareResult pluginResultToCompleteResult(PluginCompareResult result) {
        switch (result) {
            case IGNORE:
            case MATCH:
                return CompleteObjectCompareResult.MATCH;
            case DIFFER:
                return CompleteObjectCompareResult.DIFFER;
            case PREFERNEW:
                return CompleteObjectCompareResult.PREFERNEW;
            case PREFEROLD:
                return CompleteObjectCompareResult.PREFEROLD;
            case UNDEFINED:
                return CompleteObjectCompareResult.UNDEFINED;
        }
        return null;
    }
}
