// Created: 12 Sept. 2025
package de.freese.base.net.ssh;

import java.io.IOException;
import java.time.Duration;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class SshTunnel extends AbstractSsh {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshTunnel.class);

    public static SshTunnel create(final String user, final CharSequence password, final int localPort, final String tunnelHost, final int tunnelPort,
                                   final String targetHost, final int targetPort) throws IOException {
        LOGGER.info("Connecting to {}@{}:{}", user, tunnelHost, tunnelPort);

        final SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

        // HOST is configured in ~/.ssh/config (Optional).
        // sshClient.setHostConfigEntryResolver(new ConfigFileHostEntryResolver(Paths.get(System.getProperty("user.home"), ".ssh", "config")));

        // Password for Certificate.
        sshClient.setFilePasswordProvider(FilePasswordProvider.of(password.toString()));

        sshClient.start();

        // Connect to the server.
        final ClientSession clientSession = sshClient.connect(user, tunnelHost, tunnelPort).verify(Duration.ofSeconds(5)).getSession();

        // For User/Password authentication without certificate.
        // clientSession.addPasswordIdentity(password.toString());

        if (!clientSession.auth().verify(Duration.ofSeconds(5)).await(Duration.ofSeconds(5))) {
            throw new IllegalStateException("SSH Authentication failed");
        }

        clientSession.startLocalPortForwarding(localPort, new SshdSocketAddress(targetHost, targetPort));

        LOGGER.info("SSH Tunnel established: localhost:{} -> {}:{} -> {}:{}", localPort, tunnelHost, tunnelPort, targetHost, targetPort);

        return new SshTunnel(sshClient, clientSession);
    }

    private SshTunnel(final SshClient sshClient, final ClientSession clientSession) {
        super(sshClient, clientSession);
    }
}
