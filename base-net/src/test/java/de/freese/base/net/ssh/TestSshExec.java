package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@Disabled("sshd not always available")
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSshExec {
    private static final String HOST = "remote";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSshExec.class);
    private static final String PASSWORD = "pass";
    private static final String USER = "user";

    //    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserCertificate() throws Exception {
        final SshExec sshExec = SshExec.connectByUserCertificate(USER, PASSWORD, HOST, 22);

        final String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        LOGGER.info(result);

        sshExec.disconnect();
    }

    //    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserPassword() throws Exception {
        final SshExec sshExec = SshExec.connectByUserPassword(USER, PASSWORD, HOST, 22);

        final String result = sshExec.execute("df -h");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        LOGGER.info(result);

        sshExec.disconnect();
    }
}
