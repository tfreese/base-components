package de.freese.base.net.ssh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
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

    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshTunnel() throws Exception {
        final String remoteHost = "remoteHost";
        final int remotePort = 4321;

        try (SshTunnel sshTunnel = SshTunnel.create(USER, PASSWORD, 1234, HOST, 22, remoteHost, remotePort)) {
            assertNotNull(sshTunnel);
        }
    }

    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserCertificate() throws Exception {
        try (SshExec sshExec = SshExec.create(USER, PASSWORD, HOST, 22, true)) {
            assertNotNull(sshExec);

            final String result = sshExec.execute("df -h");

            assertNotNull(result);
            assertFalse(result.isEmpty());

            LOGGER.info(result);
        }
    }

    @EnabledOnOs({OS.LINUX, OS.MAC})
    @Test
    void testSshUserPassword() throws Exception {
        try (SshExec sshExec = SshExec.create(USER, PASSWORD, HOST, 22, false)) {
            assertNotNull(sshExec);

            final String result = sshExec.execute("df -h");

            assertNotNull(result);
            assertFalse(result.isEmpty());

            LOGGER.info(result);
        }
    }
}
