package de.freese.base.core.nio.buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestAutoExpandBuffer
{
    /**
     *
     */
    @Test
    void testByteBufferWithByte()
    {
        AutoExpandByteBuffer bb = AutoExpandByteBuffer.of(1, false);
        assertEquals(0, bb.position());
        assertEquals(1, bb.limit());
        assertEquals(1, bb.capacity());
        assertEquals(1, bb.remaining());

        bb.put((byte) 0);
        assertEquals(1, bb.position());
        assertEquals(1, bb.limit());
        assertEquals(1, bb.capacity());
        assertEquals(0, bb.remaining());
        assertEquals(0, bb.get(0));

        // AutoExpand
        bb.put((byte) 1);
        assertEquals(2, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(62, bb.remaining());
        assertEquals(1, bb.get(1));

        bb.put((byte) 2);
        assertEquals(3, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(61, bb.remaining());
        assertEquals(2, bb.get(2));

        bb.put((byte) 3);
        assertEquals(4, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(60, bb.remaining());
        assertEquals(3, bb.get(3));
    }

    /**
     *
     */
    @Test
    void testByteBufferWithLong()
    {
        AutoExpandByteBuffer bb = AutoExpandByteBuffer.of(8, false);
        assertEquals(0, bb.position());
        assertEquals(8, bb.limit());
        assertEquals(8, bb.capacity());
        assertEquals(8, bb.remaining());

        bb.putLong(0L);
        assertEquals(8, bb.position());
        assertEquals(8, bb.limit());
        assertEquals(8, bb.capacity());
        assertEquals(0, bb.remaining());
        assertEquals(0L, bb.getLong(0));

        // AutoExpand
        bb.putLong(1L);
        assertEquals(16, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(48, bb.remaining());
        assertEquals(1L, bb.getLong(8));

        bb.putLong(2L);
        assertEquals(24, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(40, bb.remaining());
        assertEquals(2L, bb.getLong(16));

        bb.putLong(3L);
        assertEquals(32, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(32, bb.remaining());
        assertEquals(3L, bb.getLong(24));
    }

    /**
     *
     */
    @Test
    void testCharBufferCrLfWithChar()
    {
        AutoExpandCharBufferCrLf bb = AutoExpandCharBufferCrLf.of(1, "\r\n");
        assertEquals(0, bb.position());
        assertEquals(1, bb.limit());
        assertEquals(1, bb.capacity());
        assertEquals(1, bb.remaining());

        bb.putLn('a');
        assertEquals(3, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(61, bb.remaining());
        assertEquals('a', bb.get(0));
        assertEquals('\r', bb.get(1));
        assertEquals('\n', bb.get(2));

        // AutoExpand
        bb.putLn('b');
        assertEquals(6, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(58, bb.remaining());
        assertEquals('b', bb.get(3));
        assertEquals('\r', bb.get(4));
        assertEquals('\n', bb.get(5));

        bb.putLn('c');
        assertEquals(9, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(55, bb.remaining());
        assertEquals('c', bb.get(6));
        assertEquals('\r', bb.get(7));
        assertEquals('\n', bb.get(8));

        bb.putLn('d');
        assertEquals(12, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(52, bb.remaining());
        assertEquals('d', bb.get(9));
        assertEquals('\r', bb.get(10));
        assertEquals('\n', bb.get(11));
    }

    /**
     *
     */
    @Test
    void testCharBufferCrLfWithString()
    {
        AutoExpandCharBufferCrLf bb = AutoExpandCharBufferCrLf.of(4, "\r\n");
        assertEquals(0, bb.position());
        assertEquals(4, bb.limit());
        assertEquals(4, bb.capacity());
        assertEquals(4, bb.remaining());

        bb.putLn("aaaa");
        assertEquals(6, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(58, bb.remaining());
        assertEquals("aaaa", bb.getString(0, 4));
        assertEquals("\r\n", bb.getString(4, 2));

        // AutoExpand
        bb.putLn("bbbb");
        assertEquals(12, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(52, bb.remaining());
        assertEquals("bbbb", bb.getString(6, 4));
        assertEquals("\r\n", bb.getString(10, 2));

        bb.putLn("cccc");
        assertEquals(18, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(46, bb.remaining());
        assertEquals("cccc", bb.getString(12, 4));
        assertEquals("\r\n", bb.getString(16, 2));

        bb.putLn("dddd");
        assertEquals(24, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(40, bb.remaining());
        assertEquals("dddd", bb.getString(18, 4));
        assertEquals("\r\n", bb.getString(22, 2));
    }

    /**
     *
     */
    @Test
    void testCharBufferWithChar()
    {
        AutoExpandCharBuffer bb = AutoExpandCharBuffer.of(1);
        assertEquals(0, bb.position());
        assertEquals(1, bb.limit());
        assertEquals(1, bb.capacity());
        assertEquals(1, bb.remaining());

        bb.put('a');
        assertEquals(1, bb.position());
        assertEquals(1, bb.limit());
        assertEquals(1, bb.capacity());
        assertEquals(0, bb.remaining());
        assertEquals('a', bb.get(0));

        // AutoExpand
        bb.put('b');
        assertEquals(2, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(62, bb.remaining());
        assertEquals('b', bb.get(1));

        bb.put('c');
        assertEquals(3, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(61, bb.remaining());
        assertEquals('c', bb.get(2));

        bb.put('d');
        assertEquals(4, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(60, bb.remaining());
        assertEquals('d', bb.get(3));
    }

    /**
     *
     */
    @Test
    void testCharBufferWithString()
    {
        AutoExpandCharBuffer bb = AutoExpandCharBuffer.of(4);
        assertEquals(0, bb.position());
        assertEquals(4, bb.limit());
        assertEquals(4, bb.capacity());
        assertEquals(4, bb.remaining());

        bb.put("aaaa");
        assertEquals(4, bb.position());
        assertEquals(4, bb.limit());
        assertEquals(4, bb.capacity());
        assertEquals(0, bb.remaining());
        assertEquals("aaaa", bb.getString(0, 4));

        // AutoExpand
        bb.put("bbbb");
        assertEquals(8, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(56, bb.remaining());
        assertEquals("bbbb", bb.getString(4, 4));

        bb.put("cccc");
        assertEquals(12, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(52, bb.remaining());
        assertEquals("cccc", bb.getString(8, 4));

        bb.put("dddd");
        assertEquals(16, bb.position());
        assertEquals(64, bb.limit());
        assertEquals(64, bb.capacity());
        assertEquals(48, bb.remaining());
        assertEquals("dddd", bb.getString(12, 4));
    }
}
