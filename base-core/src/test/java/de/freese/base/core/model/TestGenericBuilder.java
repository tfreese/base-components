// Created: 24.01.2018
package de.freese.base.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestGenericBuilder
{
    /**
     * Erzeugt eine neue Instanz von {@link TestGenericBuilder}.
     */
    public TestGenericBuilder()
    {
        super();
    }

    /**
     *
     */
    @Test
    public void test010WithConsumer()
    {
        //@formatter:off
        List<String> list = GenericBuilder.of(ArrayList<String>::new)
            .with(l -> l.add("A"))
            .with(l -> l.add("B"))
            .build();
        //@formatter:on

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    /**
     *
     */
    @Test
    public void test020WithBiConsumer()
    {
        //@formatter:off
        List<String> list = GenericBuilder.of(ArrayList<String>::new)
            .with(ArrayList::add, "A")
            .with(ArrayList::add, "B")
            .build();
        //@formatter:on

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    /**
    *
    */
    @Test
    public void test030WithMix()
    {
       //@formatter:off
       List<String> list = GenericBuilder.of(ArrayList<String>::new)
           .with(l -> l.add("A"))
           .with(ArrayList::add, "B")
           .build();
       //@formatter:on

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    /**
    *
    */
    @Test
    public void test040Multiple()
    {
        // 3 Objekte bauen
        int n = 3;

       //@formatter:off
        List<String[]> list = GenericBuilder.of(() -> new String[2])
           .with(a -> a[0] = "A")
           .with(a -> a[1] = "B")
           .build(n);
       //@formatter:on

        assertNotNull(list);
        assertEquals(3, list.size());

        list.forEach(obj -> {
            assertEquals("A", obj[0]);
            assertEquals("B", obj[1]);
        });
    }
}
