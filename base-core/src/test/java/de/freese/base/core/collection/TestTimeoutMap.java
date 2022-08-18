// Created: 04.06.2018
package de.freese.base.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestTimeoutMap
{
    static Stream<Arguments> createArguments() throws Exception
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("HashMap", new HashMap<>()),
                Arguments.of("TreeMap", new TreeMap<>()),
                Arguments.of("LinkedHashMap", new LinkedHashMap<>()),
                Arguments.of("ConcurrentHashMap", new ConcurrentHashMap<>()),
                Arguments.of("IdentityHashMap", new IdentityHashMap<>()),
                Arguments.of("WeakHashMap", new WeakHashMap<>()),
                Arguments.of("Hashtable", new Hashtable<>())
        );
        // @formatter:on
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArguments")
    void testCompute(final String name, final Map<String, Integer> decorated) throws Exception
    {
        Map<String, Integer> map = new TimeoutMap<>(Duration.ofMillis(500), decorated);

        map.compute("a", (key, value) -> value == null ? 1 : ++value);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(1, map.get("a"));
        assertIterableEquals(Set.of("a"), map.keySet());
        assertIterableEquals(Set.of(1), map.values());

        map.compute("a", (key, value) -> value == null ? 1 : ++value);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(2, map.get("a"));
        assertIterableEquals(Set.of("a"), map.keySet());
        assertIterableEquals(Set.of(2), map.values());

        TimeUnit.MILLISECONDS.sleep(500);

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertNull(map.get("a"));
        assertIterableEquals(Set.of(), map.keySet());
        assertIterableEquals(Set.of(), map.values());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArguments")
    void testComputeIfAbsent(final String name, final Map<String, List<Integer>> decorated) throws Exception
    {
        Map<String, List<Integer>> map = new TimeoutMap<>(Duration.ofMillis(500), decorated);

        map.computeIfAbsent("a", key -> new ArrayList<>()).add(1);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertIterableEquals(List.of(1), map.get("a"));
        assertIterableEquals(Set.of("a"), map.keySet());
        assertIterableEquals(Set.of(List.of(1)), map.values());

        map.computeIfAbsent("a", key -> new ArrayList<>()).add(1);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(List.of(1, 1), map.get("a"));
        assertIterableEquals(Set.of("a"), map.keySet());
        assertIterableEquals(Set.of(List.of(1, 1)), map.values());

        TimeUnit.MILLISECONDS.sleep(500);

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertNull(map.get("a"));
        assertIterableEquals(Set.of(), map.keySet());
        assertIterableEquals(Set.of(), map.values());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArguments")
    void testGet(final String name, final Map<String, Integer> decorated) throws Exception
    {
        Map<String, Integer> map = new TimeoutMap<>(Duration.ofMillis(500), decorated);

        map.put("a", 1);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(1, map.get("a"));
        assertIterableEquals(Set.of("a"), map.keySet());
        assertIterableEquals(Set.of(1), map.values());

        TimeUnit.MILLISECONDS.sleep(500);

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertNull(map.get("a"));
        assertIterableEquals(Set.of(), map.keySet());
        assertIterableEquals(Set.of(), map.values());
    }
}
