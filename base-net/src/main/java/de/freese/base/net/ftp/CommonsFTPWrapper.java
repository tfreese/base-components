package de.freese.base.net.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import de.freese.base.core.logging.LoggingOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * FTP-Client aus dem apache.commons.net.ftp Jar.
 *
 * @author Thomas Freese
 */
public class CommonsFTPWrapper implements FTPWrapper
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private boolean debug;
    /**
     *
     */
    private FTPClient ftpClient;
    /**
     *
     */
    private ProtocolCommandListener protocolCommandListener;

    /**
     * Creates a new {@link CommonsFTPWrapper} object.
     */
    public CommonsFTPWrapper()
    {
        super();

        setFtpClient(new FTPClient());
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#changeWorkingDirectory(java.lang.String)
     */
    @Override
    public void changeWorkingDirectory(final String dir) throws Exception
    {
        getFtpClient().changeWorkingDirectory(dir);
        checkReply();
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#changeWorkingDirectory(java.lang.String, boolean)
     */
    @Override
    public void changeWorkingDirectory(final String path, final boolean createDirs) throws Exception
    {
        String mPath = path;

        // this.ftpClient.changeWorkingDirectory(path);
        // checkReply();
        if (mPath == null)
        {
            throw new IllegalArgumentException("Path is NULL !");
        }

        if (mPath.startsWith("/"))
        {
            mPath = mPath.replaceFirst("/", "");
        }

        mPath = mPath.replace('\\', '/');

        // Unterverzeichnisse auslesen
        String[] splits = mPath.split("/");

        for (String split : splits)
        {
            if (split.length() == 0)
            {
                continue;
            }

            if (createDirs)
            {
                getFtpClient().makeDirectory(split);

                // Code 550 = Verzeichnis existiert bereits
                if (getFtpClient().getReplyCode() != 550)
                {
                    checkReply();
                }
            }

            getFtpClient().changeWorkingDirectory(split);
            checkReply();
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#connect(java.lang.String)
     */
    @Override
    public void connect(final String host) throws Exception
    {
        getFtpClient().connect(host);
        checkReply();
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        if (getFtpClient().isConnected())
        {
            getFtpClient().disconnect();
        }

        this.ftpClient = null;
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
        getFtpClient().login(user, psw);
        checkReply();

        getFtpClient().setFileType(FTP.BINARY_FILE_TYPE);
        getFtpClient().enterLocalPassiveMode();
        checkReply();
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#logout()
     */
    @Override
    public void logout() throws Exception
    {
        if (getFtpClient().isConnected())
        {
            getFtpClient().logout();
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#printWorkingDirectory()
     */
    @Override
    public String printWorkingDirectory() throws Exception
    {
        return getFtpClient().printWorkingDirectory();
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
        getFtpClient().retrieveFile(dateiName, outputStream);
        // this.ftpClient.completePendingCommand();
        checkReply();
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setDebug(boolean)
     */
    @Override
    public void setDebug(final boolean debug)
    {
        this.debug = debug;

        if (this.debug)
        {
            this.ftpClient.addProtocolCommandListener(getProtocolCommandListener());
        }
        else
        {
            this.ftpClient.removeProtocolCommandListener(getProtocolCommandListener());
        }
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setPort(int)
     */
    @Override
    public void setPort(final int port)
    {
        getFtpClient().setDefaultPort(port);
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#setTimeOut(int)
     */
    @Override
    public void setTimeOut(final int timeOut) throws Exception
    {
        if (timeOut < 1)
        {
            throw new IllegalArgumentException("TimeOut < 1 !");
        }

        getFtpClient().setDataTimeout(timeOut);
        getFtpClient().setSoTimeout(timeOut);
    }

    /**
     * @see de.freese.base.net.ftp.FTPWrapper#storeFile(java.lang.String, java.io.InputStream)
     */
    @Override
    public void storeFile(final String dateiName, final InputStream is) throws Exception
    {
        getFtpClient().storeFile(dateiName, is);
        // this.ftpClient.completePendingCommand();
        checkReply();
    }

    /**
     * Prüft den Reply-Code des letzten FTP-Commandos.
     *
     * @return String
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected String checkReply() throws Exception
    {
        synchronized (getFtpClient())
        {
            String reply = getFtpClient().getReplyString();
            int replyCode = getFtpClient().getReplyCode();

            // if((!FTPReply.isPositiveCompletion(replyCode)))
            // {
            // throw new Exception("FTP-Error: " + reply);
            // }

            reply = reply.replace("\r\n", "");

            // Codes mit 5xx sind Fehlercodes
            if ((replyCode % 500) < 100)
            {
                throw new Exception("FTP-Error: " + reply);
            }

            return reply;
        }
    }

    /**
     * @return {@link FTPClient}
     */
    protected FTPClient getFtpClient()
    {
        return this.ftpClient;
    }

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link ProtocolCommandListener}
     */
    protected ProtocolCommandListener getProtocolCommandListener()
    {
        if (this.protocolCommandListener == null)
        {
            this.protocolCommandListener = new PrintCommandListener(new PrintWriter(new LoggingOutputStream(getLogger(), Level.INFO), true, StandardCharsets.UTF_8));
        }

        return this.protocolCommandListener;
    }

    /**
     * @param ftpClient {@link FTPClient}
     */
    protected void setFtpClient(final FTPClient ftpClient)
    {
        this.ftpClient = ftpClient;
    }
}
