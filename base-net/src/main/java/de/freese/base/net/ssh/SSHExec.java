package de.freese.base.net.ssh;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Util-Klasse für das Aufrufen von Commandos über eine SSH-Verbindung.
 *
 * @author Thomas Freese
 */
public class SSHExec
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SSHExec.class);
    /**
     *
     */
    private final String host;
    /**
     *
     */
    private int lastExitStatus = -1;
    /**
     *
     */
    private final CharSequence password;
    /**
     *
     */
    private final int port;
    /**
     *
     */
    private Session session;
    /**
     *
     */
    private int timeOut;
    /**
     *
     */
    private final String user;

    /**
     * Erstellt ein neues {@link SSHExec} Object.<br>
     * Default Port = 22.
     *
     * @param user String
     * @param password {@link CharSequence}
     * @param host String
     */
    public SSHExec(final String user, final CharSequence password, final String host)
    {
        this(user, password, host, 22);
    }

    /**
     * Erstellt ein neues {@link SSHExec} Object.
     *
     * @param user String
     * @param password {@link CharSequence}
     * @param host String
     * @param port int
     */
    public SSHExec(final String user, final CharSequence password, final String host, final int port)
    {
        super();

        Objects.requireNonNull(user, "user required");
        Objects.requireNonNull(password, "password required");
        Objects.requireNonNull(host, "host required");

        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * Remote-Login.
     *
     * @throws JSchException Falls was schief geht.
     */
    public void connect() throws JSchException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("connecting to {}@{}", this.user, this.host);
        }

        JSch jsch = new JSch();

        this.session = jsch.getSession(this.user, this.host, this.port);
        this.session.setPassword(this.password.toString());
        this.session.setTimeout(this.timeOut);

        this.session.setConfig("StrictHostKeyChecking", "no");
        this.session.setConfig("PreferredAuthentications", "password"); // Verhindert Kerberos Authetifizierung
        // this.session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password"); // Verhindert Kerberos
        // Authetifizierung

        this.session.connect();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("session connected");
        }
    }

    /**
     * Verbindung beenden.
     */
    public void disconnect()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("disconnecting session");
        }

        if (this.session == null)
        {
            return;
        }

        if (!this.session.isConnected())
        {
            this.session = null;
            return;
        }

        this.session.disconnect();
        this.session = null;

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("session disconnected");
        }
    }

    /**
     * Ausführung des Commandos.<br>
     *
     * @param command String
     *
     * @return {@link List}
     *
     * @throws JSchException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public List<String> execute(final String command) throws JSchException, IOException
    {
        return execute(command, response -> {
            List<String> result = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response), StandardCharsets.UTF_8)))
            {
                result = reader.lines().toList();
                // for (;;)
                // {
                // String line = reader.readLine();
                //
                // if (line == null)
                // {
                // break;
                // }
                //
                // result.add(line);
                // }
            }
            catch (IOException ex)
            {
                throw new UncheckedIOException(ex);
            }

            return result;
        });
    }

    /**
     * Ausführung des Commandos in einem parallen Thread.<br>
     *
     * @param command String
     * @param executorService {@link ExecutorService}
     *
     * @return {@link List}
     */
    public Future<List<String>> execute(final String command, final ExecutorService executorService)
    {
        Objects.requireNonNull(executorService, "executorService required");

        return executorService.submit(() -> execute(command));
    }

    /**
     * Ausführung des Commandos in einem parallen Thread.<br>
     *
     * @param <R> Konkreter Return-Typ
     * @param command String
     * @param executorService {@link ExecutorService}
     * @param responseMapper {@link Function}; Wandelt die Roh-Daten des Response u
     *
     * @return {@link List}
     */
    public <R> Future<R> execute(final String command, final ExecutorService executorService, final Function<byte[], R> responseMapper)
    {
        Objects.requireNonNull(executorService, "executorService required");

        return executorService.submit(() -> execute(command, responseMapper));
    }

    /**
     * Ausführung des Commandos.<br>
     *
     * @param <R> Konkreter Return-Typ
     * @param command String
     * @param responseMapper {@link Function}; Wandelt die Roh-Daten des Response um
     *
     * @return {@link List}
     *
     * @throws JSchException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public <R> R execute(final String command, final Function<byte[], R> responseMapper) throws JSchException, IOException
    {
        if (this.session == null)
        {
            throw new IllegalStateException("session not connected");
        }

        Objects.requireNonNull(command, "command required");
        Objects.requireNonNull(responseMapper, "responseMapper required");

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("executing: {}", command);
        }

        this.lastExitStatus = -1;

        byte[] response = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ChannelExec channel = (ChannelExec) this.session.openChannel("exec");
            channel.setCommand(command);
            channel.setOutputStream(baos);
            channel.setExtOutputStream(baos);
            channel.setErrStream(System.err);
            channel.connect();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("channel connected");
            }

            // Warten auf Response.
            // Thread.sleep(1000L);
            while (channel.getExitStatus() == -1)
            // while (!channel.isClosed())
            {
                try
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("wait for response");
                    }

                    Thread.sleep(100);
                }
                catch (Exception ex)
                {
                    // Ignore
                }
            }

            this.lastExitStatus = channel.getExitStatus();
            channel.disconnect();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("channel disconnected with exitStatus = {}", this.lastExitStatus);
            }

            baos.flush();
            response = baos.toByteArray();
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("response size = {} bytes", response.length);
        }

        // Kompletter String
        // String result = new String(response, "UTF-8");

        R result = responseMapper.apply(response);

        return result;
    }

    /**
     * Liefert den ExistStatus des letzten Commands.
     *
     * @return int
     */
    public int getLastExitStatus()
    {
        return this.lastExitStatus;
    }

    /**
     * Setzt den Timeout.<br>
     * Default = 0 = kein Timeout
     *
     * @param timeOut int
     */
    public void setTimeOut(final int timeOut)
    {
        this.timeOut = timeOut;
    }
}
