package de.freese.base.net.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util-Klasse für das Aufrufen von Commandos über eine SSH-Verbindung.<br>
 * <a href="https://github.com/apache/mina-sshd/blob/master/docs/client-setup.md">client-setup</a>
 *
 * @author Thomas Freese
 */
public final class SshExec extends AbstractSsh {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshExec.class);

    public static SshExec create(final String user, final CharSequence password, final String host, final int port, final boolean useCertificate) throws IOException {
        LOGGER.info("Connecting to {}@{}:{}", user, host, port);

        final SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

        if (useCertificate) {
            // HOST is configured in ~/.ssh/config (Optional).
            // sshClient.setHostConfigEntryResolver(new ConfigFileHostEntryResolver(Paths.get(System.getProperty("user.home"), ".ssh", "config")));

            // Password for Certificate.
            sshClient.setFilePasswordProvider(FilePasswordProvider.of(password.toString()));
        }

        sshClient.start();

        // Connect to the server.
        final ClientSession clientSession = sshClient.connect(user, host, port).verify(Duration.ofSeconds(5)).getSession();

        if (!useCertificate) {
            // For User/Password authentication without certificate.
            clientSession.addPasswordIdentity(password.toString());
        }

        if (!clientSession.auth().verify(Duration.ofSeconds(5)).await(Duration.ofSeconds(5))) {
            throw new IllegalStateException("SSH Authentication failed");
        }

        LOGGER.info("SSH Connection established: {}@{}:{}", user, host, port);

        return new SshExec(sshClient, clientSession, host);
    }

    private final String host;

    private SshExec(final SshClient sshClient, final ClientSession clientSession, final String host) {
        super(sshClient, clientSession);

        this.host = Objects.requireNonNull(host, "host required");
    }

    public String execute(final String command) throws Exception {
        // clientSession.createChannel(Channel.CHANNEL_EXEC);

        try (ChannelExec channelExec = getClientSession().createExecChannel(command);
             ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {
            channelExec.setOut(responseStream);
            channelExec.setErr(errorStream);

            // Execute and wait-
            channelExec.open();

            final Duration timeout = Duration.ofSeconds(3);
            final Set<ClientChannelEvent> events = channelExec.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), timeout);

            // Check if timed out.
            if (events.contains(ClientChannelEvent.TIMEOUT)) {
                throw new IllegalStateException(String.format("Timeout after %d seconds on host '%s' for command '%s'", timeout.toSeconds(), host, command));
            }

            errorStream.flush();

            final String error = errorStream.toString(StandardCharsets.UTF_8);

            if (!error.isEmpty()) {
                throw new IllegalStateException(error);
            }

            responseStream.flush();

            return responseStream.toString(StandardCharsets.UTF_8);
        }
    }
}
