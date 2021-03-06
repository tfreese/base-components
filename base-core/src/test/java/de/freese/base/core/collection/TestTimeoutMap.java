/**
 * Created: 04.06.2018
 */
package de.freese.base.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestTimeoutMap
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testPutGet() throws Exception
    {
        TimeoutMap<String, String> map = new TimeoutMap<>(3, TimeUnit.SECONDS);

        map.put("key", "value");

        TimeUnit.MILLISECONDS.sleep(2000);

        assertEquals("value", map.get("key"));
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(1, map.keySet().size());
        assertEquals(1, map.entrySet().size());
        assertTrue(map.containsKey("key"));
        assertTrue(map.containsValue("value"));

        TimeUnit.MILLISECONDS.sleep(1001);

        assertNull(map.get("key"));
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertEquals(0, map.keySet().size());
        assertEquals(0, map.entrySet().size());
        assertFalse(map.containsKey("key"));
        assertFalse(map.containsValue("value"));
    }

    /**
     *
     */
    @Test
    void testTimeoutValue()
    {
        TimeoutMap<String, String> map = new TimeoutMap<>(3, TimeUnit.SECONDS);

        assertEquals(3000, map.getTimeoutInMillis());
        assertEquals(3000, map.getTimeout(TimeUnit.MILLISECONDS));
        assertEquals(3, map.getTimeout(TimeUnit.SECONDS));
    }
}
