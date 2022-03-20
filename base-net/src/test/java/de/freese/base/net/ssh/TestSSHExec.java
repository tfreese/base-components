package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
class TestSSHExec
{
    /**
     *
     */
    private static SSHExec sshExec;

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        if (sshExec != null)
        {
            sshExec.disconnect();
            sshExec = null;
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        if (sshExec == null)
        {
            sshExec = new SSHExec("...", "...", "192.168.155.100", 22);

            sshExec.connect();
        }
    }

    /**
     * @return {@link SSHExec}
     */
    static SSHExec getSshExec()
    {
        return sshExec;
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testSSH() throws Exception
    {
        List<String> result = sshExec.execute("df -h");

        assertEquals(0, sshExec.getLastExitStatus());
        assertFalse(result.isEmpty());

        result.forEach(System.out::println);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testSSHAsync() throws Exception
    {
        Future<List<String>> resultFuture = sshExec.execute("df -h", ForkJoinPool.commonPool());
        List<String> result = resultFuture.get();

        assertEquals(0, sshExec.getLastExitStatus());
        assertFalse(result.isEmpty());

        result.forEach(System.out::println);
    }
}
