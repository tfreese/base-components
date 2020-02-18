package de.freese.base.net.protocol.pop3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SharedByteArrayOutputStream;
import de.freese.base.net.protocol.AbstractProtocol;
import de.freese.base.net.protocol.GeneralCommand;

/**
 * Diese Klasse implementiert das POP3-Protokoll.
 *
 * @author Rhomas Freese
 */
public class POP3Protocol extends AbstractProtocol implements POP3Command, AutoCloseable
{
    /**
     *
     */
    private String apopChallenge = null;

    /**
     *
     */
    private BufferedReader inputReader;

    /**
     *
     */
    private Writer outputWriter;

    /**
     *
     */
    private Socket serverSocket;

    /**
     * Erstellt ein neues {@link POP3Protocol} Object.
     *
     * @param host String
     * @param port int
     * @param props {@link Properties}
     * @param propPrefix String
     * @param isSSL boolean
     * @throws IOException Falls was schief geht.
     */
    public POP3Protocol(final String host, final int port, final Properties props, final String propPrefix, final boolean isSSL) throws IOException
    {
        super(props);

        Properties m_props = props;
        int m_port = port;

        if (m_props == null)
        {
            m_props = new Properties();
        }

        Response r = new Response();
        String apop = m_props.getProperty(propPrefix + ".apop.enable");
        boolean enableAPOP = (apop != null) && apop.equalsIgnoreCase("true");

        try
        {
            if (m_port == -1)
            {
                m_port = POP3Command.POP3_PORT;
            }

            debug("DEBUG POP3: connecting to host \"" + host + "\", port " + m_port + ", isSSL " + isSSL);

            if (isSSL)
            {
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.serverSocket = socketFactory.createSocket(host, m_port);
                ((SSLSocket) this.serverSocket).setEnabledProtocols(new String[]
                {
                        "TLSv1"
                });
            }
            else
            {
                this.serverSocket = new Socket(host, m_port);
            }

            this.inputReader = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream(), "iso-8859-1"));
            this.outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.serverSocket.getOutputStream(), "iso-8859-1")));

            // should be US-ASCII, but not all JDK's support
            r = simpleCommand(null);
        }
        catch (IOException ioe)
        {
            try
            {
                this.serverSocket.close();
            }
            catch (Throwable th)
            {
                throw new IOException("Connect failed");
            }
        }

        if (!r.ok)
        {
            try
            {
                this.serverSocket.close();
            }
            catch (Throwable th)
            {
                throw new IOException("Connect failed");
            }
        }

        if (enableAPOP)
        {
            int challStart = r.data.indexOf('<'); // start of challenge
            int challEnd = r.data.indexOf('>', challStart); // end of challenge

            if ((challStart != -1) && (challEnd != -1))
            {
                this.apopChallenge = r.data.substring(challStart, challEnd + 1);
            }

            debug("DEBUG POP3: APOP challenge: " + this.apopChallenge);
        }
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        if (this.serverSocket != null)
        { // Forgot to logout ?!
            quit();
        }
    }

    /**
     * Delete (permanently) the specified message.
     *
     * @param messageNumber int
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public synchronized boolean dele(final int messageNumber) throws IOException
    {
        Response r = simpleCommand(POP3Command.DELE + " " + messageNumber);

        return r.ok;
    }

    /**
     * @see de.freese.base.net.protocol.AbstractProtocol#getDigest(java.lang.String)
     */
    @Override
    protected String getDigest(final String password)
    {
        return super.getDigest(this.apopChallenge + password);
    }

    /**
     * Return the size of the message using the LIST command.
     *
     * @param messageNumber int
     * @return int
     * @throws IOException Falls was schief geht.
     */
    public synchronized int list(final int messageNumber) throws IOException
    {
        Response r = simpleCommand(POP3Command.LIST + " " + messageNumber);
        int size = -1;

        if (r.ok && (r.data != null))
        {
            try
            {
                StringTokenizer st = new StringTokenizer(r.data);
                st.nextToken(); // skip message number
                size = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        return size;
    }

    /**
     * Login to the server, using the USER and PASS commands.
     *
     * @param user String
     * @param password String
     * @throws IOException Falls was schief geht.
     */
    public synchronized void login(final String user, final String password) throws IOException
    {
        Response r;
        String dpw = null;

        if (this.apopChallenge != null)
        {
            dpw = getDigest(password);
        }

        if ((this.apopChallenge != null) && (dpw != null))
        {
            r = simpleCommand(POP3Command.APOP + " " + user + " " + dpw);
        }
        else
        {
            r = simpleCommand(POP3Command.USER + " " + user);

            if (!r.ok)
            {
                String message = "USER command failed";

                if (r.data != null)
                {
                    message = r.data;
                }

                throw new IOException(message);
            }

            r = simpleCommand(POP3Command.PASS + " " + password);
        }

        if (!r.ok)
        {
            String message = "login failed";

            if (r.data != null)
            {
                message = r.data;
            }

            throw new IOException(message);
        }
    }

    /**
     * Issue a POP3 command that expects a multi-line response. <code>size</code> is an estimate of the response size.
     *
     * @param cmd String
     * @param size int
     * @return Response
     * @throws IOException Falls was schief geht.
     * @throws EOFException Falls was schief geht.
     */
    private Response multilineCommand(final String cmd, final int size) throws IOException
    {
        Response r = simpleCommand(cmd);

        if (!r.ok)
        {
            return (r);
        }

        try (SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size))
        {
            int b;
            int lastb = '\n';

            while ((b = this.inputReader.read()) >= 0)
            {
                if ((lastb == '\n') && (b == '.'))
                {
                    b = this.inputReader.read();

                    if (b == '\r')
                    {
                        // end of response, consume LF as well
                        this.inputReader.read();

                        break;
                    }
                }

                buf.write(b);

                debug(b);

                lastb = b;
            }

            if (b < 0)
            {
                throw new EOFException("EOF on serverSocket");
            }

            r.bytes = buf.toStream();
        }

        return r;
    }

    /**
     * Do a NOOP.
     *
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public synchronized boolean noop() throws IOException
    {
        Response r = simpleCommand(POP3Command.NOOP);

        return r.ok;
    }

    /**
     * Close down the connection, sending the QUIT command if expunge is true.
     *
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public synchronized boolean quit() throws IOException
    {
        boolean ok = false;

        try
        {
            Response r = simpleCommand(POP3Command.QUIT);
            ok = r.ok;
        }
        finally
        {
            try
            {
                this.serverSocket.close();
            }
            finally
            {
                this.serverSocket = null;
                this.inputReader = null;
                this.outputWriter = null;
            }
        }

        return ok;
    }

    /**
     * Retrieve the specified message. Given an estimate of the message's size we can be more efficient, preallocating the array and returning a
     * ISharedInputStream to allow us to share the array.
     *
     * @param messageNumber int
     * @param size int
     * @return {@link InputStream}
     * @throws IOException Falls was schief geht.
     */
    public synchronized InputStream retr(final int messageNumber, final int size) throws IOException
    {
        Response r = multilineCommand(POP3Command.RETR + " " + messageNumber, size);

        return r.bytes;
    }

    /**
     * Do an RSET.
     *
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public synchronized boolean rset() throws IOException
    {
        Response r = simpleCommand(POP3Command.RSET);

        return r.ok;
    }

    /**
     * Issue a simple POP3 command and return the response.
     *
     * @param cmd String
     * @return Response
     * @throws IOException Falls was schief geht.
     * @throws EOFException Falls was schief geht.
     */
    private Response simpleCommand(final String cmd) throws IOException
    {
        if (this.serverSocket == null)
        {
            throw new IOException("Folder is closed"); // XXX
        }

        String m_cmd = cmd;

        if (m_cmd != null)
        {
            debug("C: " + m_cmd);

            m_cmd += GeneralCommand.CRLF;
            this.outputWriter.write(m_cmd);
            this.outputWriter.flush();
        }

        String line = this.inputReader.readLine();

        if (line == null)
        {
            debug("S: EOF");

            throw new EOFException("EOF on serverSocket");
        }

        debug("S: " + line);

        Response r = new Response();

        if (line.startsWith(POP3Command.OK))
        {
            r.ok = true;
        }
        else if (line.startsWith(POP3Command.ERR))
        {
            r.ok = false;
        }
        else
        {
            throw new IOException("Unexpected response: " + line);
        }

        int i;

        if ((i = line.indexOf(' ')) >= 0)
        {
            // +OK/-ERR abschneiden
            r.data = line.substring(i + 1);
        }

        return r;
    }

    /**
     * Return the total number of messages and mailbox size, using the STAT command.
     *
     * @return int[]; 0 = Messages, 1 = Size of Messages
     * @throws IOException Falls was schief geht.
     */
    public synchronized int[] stat() throws IOException
    {
        Response r = simpleCommand(POP3Command.STAT);
        int[] result = new int[2];

        if (r.ok && (r.data != null))
        {
            try
            {
                StringTokenizer st = new StringTokenizer(r.data);

                result[0] = Integer.parseInt(st.nextToken());
                result[1] = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        return result;
    }

    /**
     * Return the message header and the first n lines of the message.
     *
     * @param messageNumber int
     * @param n int
     * @return {@link InputStream}
     * @throws IOException Falls was schief geht.
     */
    public synchronized InputStream top(final int messageNumber, final int n) throws IOException
    {
        Response r = multilineCommand(POP3Command.TOP + " " + messageNumber + " " + n, 0);

        return r.bytes;
    }

    /**
     * Return the UIDL string for the message.
     *
     * @param messageNumber int
     * @return String
     * @throws IOException Falls was schief geht.
     */
    public synchronized String uidl(final int messageNumber) throws IOException
    {
        Response r = simpleCommand(POP3Command.UIDL + " " + messageNumber);

        if (!r.ok)
        {
            return null;
        }

        int i = r.data.indexOf(' ');

        if (i > 0)
        {
            return r.data.substring(i + 1);
        }

        return null;
    }

    /**
     * Return the UIDL strings for all messages. The UID for msg #N is returned in uids[N-1].
     *
     * @param uids String[]
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public synchronized boolean uidl(final String[] uids) throws IOException
    {
        Response r = multilineCommand(POP3Command.UIDL, 15 * uids.length);

        if (!r.ok)
        {
            return false;
        }

        try (LineInputStream lis = new LineInputStream(r.bytes))
        {
            String line = null;

            while ((line = lis.readLine()) != null)
            {
                int i = line.indexOf(' ');

                if ((i < 1) || (i >= line.length()))
                {
                    continue;
                }

                int n = Integer.parseInt(line.substring(0, i));

                if ((n > 0) && (n <= uids.length))
                {
                    uids[n - 1] = line.substring(i + 1);
                }
            }
        }

        return true;
    }
}

/**
 * Enthaelt das Ergebniss des Requests.
 *
 * @author Thomas Freese
 */
final class Response
{
    /**
     * all the bytes from a multi-line response
     */
    InputStream bytes = null;

    /**
     * rest of line after "+OK" or "-ERR"
     */
    String data = null;

    /**
     * true if "+OK"
     */
    boolean ok = false;
}
