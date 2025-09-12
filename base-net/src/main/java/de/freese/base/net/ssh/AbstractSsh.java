// Created: 12 Sept. 2025
package de.freese.base.net.ssh;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractSsh implements AutoCloseable {
    private final ClientSession clientSession;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SshClient sshClient;

    protected AbstractSsh(final SshClient sshClient, final ClientSession clientSession) {
        super();

        this.sshClient = Objects.requireNonNull(sshClient, "sshClient required");
        this.clientSession = Objects.requireNonNull(clientSession, "clientSession required");
    }

    @Override
    public void close() {
        getLogger().info("disconnecting session");

        try {
            if (clientSession.isOpen()) {
                clientSession.close(false).await(Duration.ofSeconds(3));
            }

            if (sshClient.isOpen()) {
                sshClient.stop();
            }
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        getLogger().info("session disconnected");
    }

    protected ClientSession getClientSession() {
        return clientSession;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected SshClient getSshClient() {
        return sshClient;
    }
}
