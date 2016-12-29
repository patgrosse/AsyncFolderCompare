package de.patgrosse.asyncfoldercompare.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.patgrosse.asyncfoldercompare.utils.MapUtils;
import org.junit.Test;

public class MapUtilsTest {

    @Test
    public void testSortNaturalByValue() {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<>(1000);
        for (int i = 0; i < 1000; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }

        testMap = MapUtils.sortNaturalByValue(testMap, false);
        assertEquals(1000, testMap.size());

        Integer previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            assertNotNull(entry.getValue());
            if (previous != null) {
                assertTrue(entry.getValue() >= previous);
            }
            previous = entry.getValue();
        }
    }

}
