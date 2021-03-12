package de.freese.base.net.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Wrapper fuer einen SFTP-Client.
 *
 * @author Thomas Freese
 */
public class JCraftFTPSWrapper implements FTPWrapper
{
    /**
     * Logger fuer das Jsch-Framework.
     *
     * @author Thomas Freese
     */
    private static class JschLoggerAdapter implements com.jcraft.jsch.Logger
    {
        /**
         *
         */
        private int level = -1;

        /**
         *
         */
        private final Logger logger;

        /**
         * Erstellt ein neues {@link JschLoggerAdapter} Object.
         *
         * @param level int
         * @param logger {@link Logger}
         */
        public JschLoggerAdapter(final int level, final Logger logger)
        {
            super();

            this.level = level;
            this.logger = logger;
        }

        /**
         * @see com.jcraft.jsch.Logger#isEnabled(int)
         */
        @Override
        public boolean isEnabled(final int i)
        {
            return this.level == i;
        }

        /**
         * @see com.jcraft.jsch.Logger#log(int, java.lang.String)
         */
        @Override
        public void log(final int i, final String s)
        {
            switch (i)
            {
                case DEBUG:
                    this.logger.debug(s);
                    break;

                case INFO:
                    this.logger.info(s);
                    break;

                case WARN:
                    this.logger.warn(s);
                    break;

                case FATAL:
                case ERROR:
                    this.logger.error(s);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JCraftFTPSWrapper.class);

    /**
     *
     */
    private final Properties config;

    /**
     *
     */
    private boolean debug;

    /**
     *
     */
    private String host;

    /**
     * Default
     */
    private int port = 22;

    /**
     *
     */
    private Session session;

    /**
     *
     */
    private ChannelSftp sftp;

    /**
     *
     */
    private int timeOut;

    /**
     * Creates a new {@link JCraftFTPSWrapper} object.
     */
    public JCraftFTPSWrapper()
    {
        super();

        this.config = new Properties();
        this.config.put("StrictHostKeyChecking", "no");
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#changeWorkingDirectory(java.lang.String)
     */
    @Override
    public void changeWorkingDirectory(final String dir) throws Exception
    {
        changeWorkingDirectory(dir, true);
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#changeWorkingDirectory(java.lang.String, boolean)
     */
    @Override
    public void changeWorkingDirectory(final String path, final boolean createDirs) throws Exception
    {
        try
        {
            this.sftp.cd(path);
        }
        catch (Exception ex)
        {
            if (createDirs)
            {
                this.sftp.mkdir(path);
                this.sftp.cd(path);
            }
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#connect(java.lang.String)
     */
    @Override
    public void connect(final String host) throws Exception
    {
        this.host = host;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        this.sftp.exit();
        this.sftp.disconnect();
        this.session.disconnect();

        this.sftp = null;
        this.session = null;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#isDebug()
     */
    @Override
    public boolean isDebug()
    {
        return this.debug;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(final String user, final String psw) throws Exception
    {
        JSch jsch = new JSch();

        this.session = jsch.getSession(user, this.host, this.port);
        this.session.setPassword(psw.getBytes(StandardCharsets.UTF_8));
        this.session.setConfig(this.config);

        this.session.connect();
        this.session.setTimeout(this.timeOut);

        Channel channel = this.session.openChannel("sftp");
        channel.connect();
        this.sftp = (ChannelSftp) channel;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#logout()
     */
    @Override
    public void logout() throws Exception
    {
        if (this.sftp.isConnected())
        {
            this.sftp.quit();
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#printWorkingDirectory()
     */
    @Override
    public String printWorkingDirectory() throws Exception
    {
        return this.sftp.pwd();
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#retrieveFile(java.lang.String)
     */
    @Override
    public ByteArrayOutputStream retrieveFile(final String dateiName) throws Exception
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        retrieveFile(dateiName, outputStream);

        return outputStream;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#retrieveFile(java.lang.String, java.io.OutputStream)
     */
    @Override
    public void retrieveFile(final String dateiName, final OutputStream outputStream) throws Exception
    {
        this.sftp.get(dateiName, outputStream);
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setDebug(boolean)
     */
    @Override
    public void setDebug(final boolean debug)
    {
        this.debug = debug;

        if (debug)
        {
            JSch.setLogger(new JschLoggerAdapter(com.jcraft.jsch.Logger.INFO, LOGGER));
        }
        else
        {
            JSch.setLogger(new JschLoggerAdapter(-1, LOGGER));
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setPort(int)
     */
    @Override
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setTimeOut(int)
     */
    @Override
    public void setTimeOut(final int timeOut) throws Exception
    {
        this.timeOut = timeOut;
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#storeFile(java.lang.String, java.io.InputStream)
     */
    @Override
    public void storeFile(final String dateiName, final InputStream is) throws Exception
    {
        this.sftp.put(is, dateiName);
    }
}
