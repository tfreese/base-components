package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        if (sshExec == null)
        {
            sshExec = new SSHExec("...", "...", "192.168.155.100", 22);

            try
            {
                sshExec.connect();
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
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
    void test01SSH() throws Exception
    {
        List<String> result = sshExec.execute("df -h");

        assertEquals(0, sshExec.getLastExitStatus());
        assertTrue(!result.isEmpty());

        result.forEach(System.out::println);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test02SSHAsync() throws Exception
    {
        Future<List<String>> resultFuture = sshExec.execute("df -h", ForkJoinPool.commonPool());
        List<String> result = resultFuture.get();

        assertEquals(0, sshExec.getLastExitStatus());
        assertTrue(!result.isEmpty());

        result.forEach(System.out::println);
    }
}
