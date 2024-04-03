// Created: 24.01.2018
package de.freese.base.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.core.model.builder.GenericBuilder;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestGenericBuilder {
    @Test
    void testMultiple() {
        // 3 Objekte bauen
        final int n = 3;

        final List<String[]> list = GenericBuilder.of(() -> new String[2])
                .with(a -> a[0] = "A")
                .with(a -> a[1] = "B")
                .build(n);

        assertNotNull(list);
        assertEquals(3, list.size());

        list.forEach(obj -> {
            assertEquals("A", obj[0]);
            assertEquals("B", obj[1]);
        });
    }

    @Test
    void testWithBiConsumer() {
        final List<String> list = GenericBuilder.of(ArrayList<String>::new)
                .with(ArrayList::add, "A")
                .with(ArrayList::add, "B")
                .build();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    @Test
    void testWithConsumer() {
        final List<String> list = GenericBuilder.of(ArrayList<String>::new)
                .with(l -> l.add("A"))
                .with(l -> l.add("B"))
                .build();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    @Test
    void testWithMix() {
        final List<String> list = GenericBuilder.of(ArrayList<String>::new)
                .with(l -> l.add("A"))
                .with(ArrayList::add, "B")
                .build();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }
}
