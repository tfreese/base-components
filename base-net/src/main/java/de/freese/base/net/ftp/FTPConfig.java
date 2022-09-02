package de.freese.base.net.ftp;

/**
 * @author Thomas Freese
 */
public class FTPConfig
{
    /**
     *
     */
    private String basedir;
    /**
     *
     */
    private String host;
    /**
     *
     */
    private String password;
    /**
     *
     */
    private int port = -1;
    /**
     *
     */
    private String user;

    /**
     * @return String
     */
    public String getBasedir()
    {
        return this.basedir;
    }

    /**
     * @return String
     */
    public String getHost()
    {
        return this.host;
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @return String
     */
    public String getUser()
    {
        return this.user;
    }

    /**
     * @param basedir String
     */
    public void setBasedir(final String basedir)
    {
        this.basedir = basedir;
    }

    /**
     * @param host String
     */
    public void setHost(final String host)
    {
        this.host = host;
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * @param port int
     */
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * @param user String
     */
    public void setUser(final String user)
    {
        this.user = user;
    }
}
