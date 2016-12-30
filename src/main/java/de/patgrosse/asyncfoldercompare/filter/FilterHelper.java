package de.patgrosse.asyncfoldercompare.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class FilterHelper {

    private FilterHelper() {
    }

    public static <T> Collection<T> filterCollection(Collection<T> inputCollection, Filter<T> filter) {
        Set<T> filtered = new HashSet<>();
        for (T object : inputCollection) {
            if (!filter.isObjectFiltered(object)) {
                filtered.add(object);
            }
        }
        return filtered;
    }

}
