package de.patgrosse.asyncfoldercompare.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

public final class MapUtils {
    private MapUtils() {
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map, final boolean desc,
                                                                             final Comparator<V> valueComp) {
        if (map == null) {
            throw new IllegalArgumentException();
        }
        List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> {
            if (desc) {
                return valueComp.compare(o2.getValue(), o1.getValue());
            } else {
                return valueComp.compare(o1.getValue(), o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortNaturalByValue(final Map<K, V> map,
                                                                                    final boolean desc) {
        return sortByValue(map, desc, Comparator.naturalOrder());
    }

    public static <A, B, C> void deleteFromPairMapWhereAorBEqual(Map<Pair<A, B>, C> map, A deleteWhereAEqual,
                                                                 B deleteWhereBEqual) {
        if (map == null || deleteWhereAEqual == null || deleteWhereBEqual == null) {
            throw new IllegalArgumentException();
        }
        Iterator<Entry<Pair<A, B>, C>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Pair<A, B>, C> entry = iter.next();
            if (deleteWhereAEqual.equals(entry.getKey().getLeft())) {
                iter.remove();
            } else if (deleteWhereBEqual.equals(entry.getKey().getRight())) {
                iter.remove();
            }
        }
    }
}
