package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@Disabled("sshd not always available")
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSshExec {
    private static final String HOST = "remote";
    private static final String PASSWORD = "pass";
    private static final String USER = "user";

    //    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserCertificate() throws Exception {
        SshExec sshExec = SshExec.connectByUserCertificate(USER, PASSWORD, HOST, 22);

        String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        System.out.println(result);

        sshExec.disconnect();
    }

    //    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserPassword() throws Exception {
        SshExec sshExec = SshExec.connectByUserPassword(USER, PASSWORD, HOST, 22);

        String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        System.out.println(result);

        sshExec.disconnect();
    }
}
