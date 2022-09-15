package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled("sshd not always available")
class TestSshExec
{
    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @EnabledOnOs(
            {
                    OS.LINUX, OS.MAC
            })
    void testSshUserCertificate() throws Exception
    {
        SshExec sshExec = SshExec.connectByUserCertificate("user", "pass", "remote", 22);

        String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        System.out.println(result);

        sshExec.disconnect();
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @EnabledOnOs(
            {
                    OS.LINUX, OS.MAC
            })
    void testSshUserPassword() throws Exception
    {
        SshExec sshExec = SshExec.connectByUserPassword("user", "pass", "remote", 22);

        String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        System.out.println(result);

        sshExec.disconnect();
    }
}
