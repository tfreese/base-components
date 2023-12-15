package de.freese.base.net.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.config.hosts.ConfigFileHostEntryResolver;
import org.apache.sshd.client.future.ConnectFuture;
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
public final class SshExec {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshExec.class);

    public static SshExec connectByUserCertificate(final String user, final CharSequence password, final String host, final int port) throws IOException {
        return connect(user, host, port, sshClient -> {
            // HOST is configured in ~/.ssh/config.
            sshClient.setHostConfigEntryResolver(new ConfigFileHostEntryResolver(Paths.get(System.getProperty("user.home"), ".ssh", "config")));

            // Password for Certificate.
            sshClient.setFilePasswordProvider(FilePasswordProvider.of(password.toString()));
        }, clientSession -> {
            // Empty
        });
    }

    public static SshExec connectByUserPassword(final String user, final CharSequence password, final String host, final int port) throws IOException {
        return connect(user, host, port, sshClient -> {
            // Empty
        }, clientSession -> {
            // Only for User/Password authentication without certificate.
            clientSession.addPasswordIdentity(password.toString());
        });
    }

    private static SshExec connect(final String user, final String host, final int port, final Consumer<SshClient> sshClientConfigurer, final Consumer<ClientSession> clientSessionConfigurer) throws IOException {
        LOGGER.debug("connecting to {}@{}", user, host);

        final SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

        sshClientConfigurer.accept(sshClient);

        // Open the client
        sshClient.start();

        // Connect to the server
        final ConnectFuture cf = sshClient.connect(user, host, port);
        final ClientSession clientSession = cf.verify().getSession();

        clientSessionConfigurer.accept(clientSession);

        clientSession.auth().verify(Duration.ofSeconds(5));

        LOGGER.debug("session connected");

        return new SshExec(sshClient, clientSession, host);
    }

    private final ClientSession clientSession;
    private final String host;
    private final SshClient sshClient;

    private SshExec(final SshClient sshClient, final ClientSession clientSession, final String host) {
        super();

        this.sshClient = Objects.requireNonNull(sshClient, "sshClient required");
        this.clientSession = Objects.requireNonNull(clientSession, "clientSession required");
        this.host = Objects.requireNonNull(host, "host required");
    }

    public void disconnect() {
        LOGGER.debug("disconnecting session");

        try {
            if (this.clientSession.isOpen()) {
                clientSession.close(false).await(Duration.ofSeconds(3));
            }

            if (this.sshClient.isOpen()) {
                this.sshClient.stop();
            }
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        LOGGER.debug("session disconnected");
    }

    public String execute(final String command) throws IOException {
        //        this.clientSession.createChannel(Channel.CHANNEL_EXEC);

        try (ChannelExec channelExec = this.clientSession.createExecChannel(command);
             ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {
            channelExec.setOut(responseStream);
            channelExec.setErr(errorStream);

            // Execute and wait
            channelExec.open();

            final Duration timeout = Duration.ofSeconds(3);
            final Set<ClientChannelEvent> events = channelExec.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), timeout);

            // Check if timed out
            if (events.contains(ClientChannelEvent.TIMEOUT)) {
                throw new IOException(String.format("Timeout after %d seconds on host '%s' for command '%s'", timeout.toSeconds(), this.host, command));
            }

            errorStream.flush();

            final String error = errorStream.toString(StandardCharsets.UTF_8);

            if (!error.isEmpty()) {
                throw new IOException(error);
            }

            responseStream.flush();

            return responseStream.toString(StandardCharsets.UTF_8);
        }
    }
}
