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
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SharedByteArrayOutputStream;

import de.freese.base.net.protocol.AbstractProtocol;

/**
 * Diese Klasse implementiert das POP3-Protokoll.
 *
 * @author Thomas Freese
 */
public class Pop3Protocol extends AbstractProtocol {
    private String apopChallenge;

    private BufferedReader inputReader;

    private Writer outputWriter;

    private Socket serverSocket;

    public Pop3Protocol(final String host, final int port, final Properties props, final String propPrefix, final boolean isSSL) throws IOException {
        super();

        Properties myProperties = props != null ? props : new Properties();
        int myPort = port > 0 ? port : Pop3Command.POP3_PORT;

        Pop3Response r = new Pop3Response();
        String apop = myProperties.getProperty(propPrefix + ".apop.enable");
        boolean enableAPOP = "true".equalsIgnoreCase(apop);

        try {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("connecting to host \"{}\", port {}, isSSL {}", host, myPort, isSSL);
            }

            if (isSSL) {
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.serverSocket = socketFactory.createSocket(host, myPort);
                ((SSLSocket) this.serverSocket).setEnabledProtocols(new String[]{"TLSv3"});
            }
            else {
                this.serverSocket = new Socket(host, myPort);
            }

            // should be US-ASCII, but not all JDK's support
            this.inputReader = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream(), StandardCharsets.ISO_8859_1));
            this.outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.serverSocket.getOutputStream(), StandardCharsets.ISO_8859_1)));

            r = simpleCommand(null);
        }
        catch (IOException ioe) {
            try {
                this.serverSocket.close();
            }
            catch (Exception th) {
                throw new IOException("Connect failed");
            }
        }

        if (!r.isOk()) {
            try {
                this.serverSocket.close();
            }
            catch (IOException th) {
                throw new IOException("Connect failed");
            }
        }

        if (enableAPOP) {
            int challStart = r.getData().indexOf('<'); // start of challenge
            int challEnd = r.getData().indexOf('>', challStart); // end of challenge

            if ((challStart != -1) && (challEnd != -1)) {
                this.apopChallenge = r.getData().substring(challStart, challEnd + 1);
            }

            getLogger().debug("APOP challenge: {}", this.apopChallenge);
        }
    }

    @Override
    public void close() {
        try {
            if (this.serverSocket != null) {
                // Forgot to log out ?!
                quit();
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * Delete (permanently) the specified message.
     */
    public synchronized boolean dele(final int messageNumber) throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.DELE + " " + messageNumber);

        return r.isOk();
    }

    /**
     * Return the size of the message using the LIST command.
     */
    public synchronized int list(final int messageNumber) throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.LIST + " " + messageNumber);
        int size = -1;

        if (r.isOk() && (r.getData() != null)) {
            try {
                StringTokenizer st = new StringTokenizer(r.getData());
                st.nextToken(); // skip message number
                size = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }

        return size;
    }

    /**
     * Login to the server, using the USER and PASS commands.
     */
    public synchronized void login(final String user, final String password) throws IOException {
        Pop3Response r;
        String dpw = null;

        if (this.apopChallenge != null) {
            dpw = getDigest(password);
        }

        if ((this.apopChallenge != null) && (dpw != null)) {
            r = simpleCommand(Pop3Command.APOP + " " + user + " " + dpw);
        }
        else {
            r = simpleCommand(Pop3Command.USER + " " + user);

            if (!r.isOk()) {
                String message = "USER command failed";

                if (r.getData() != null) {
                    message = r.getData();
                }

                throw new IOException(message);
            }

            r = simpleCommand(Pop3Command.PASS + " " + password);
        }

        if (!r.isOk()) {
            String message = "login failed";

            if (r.getData() != null) {
                message = r.getData();
            }

            throw new IOException(message);
        }
    }

    /**
     * Do a NOOP.
     */
    public synchronized boolean noop() throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.NOOP);

        return r.isOk();
    }

    /**
     * Close down the connection, sending the QUIT command if expunge is true.
     */
    public synchronized boolean quit() throws IOException {
        boolean ok = false;

        try {
            Pop3Response r = simpleCommand(Pop3Command.QUIT);
            ok = r.isOk();
        }
        finally {
            try {
                this.serverSocket.close();
            }
            finally {
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
     */
    public synchronized InputStream retr(final int messageNumber, final int size) throws IOException {
        Pop3Response r = multilineCommand(Pop3Command.RETR + " " + messageNumber, size);

        return r.getBytes();
    }

    /**
     * Do an RESET.
     */
    public synchronized boolean rset() throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.RSET);

        return r.isOk();
    }

    /**
     * Return the total number of messages and mailbox size, using the STAT command.
     *
     * @return int[]; 0 = Messages, 1 = Size of Messages
     */
    public synchronized int[] stat() throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.STAT);
        int[] result = new int[2];

        if (r.isOk() && (r.getData() != null)) {
            try {
                StringTokenizer st = new StringTokenizer(r.getData());

                result[0] = Integer.parseInt(st.nextToken());
                result[1] = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
                // Ignore
            }
        }

        return result;
    }

    /**
     * Return the message header and the first n lines of the message.
     */
    public synchronized InputStream top(final int messageNumber, final int n) throws IOException {
        Pop3Response r = multilineCommand(Pop3Command.TOP + " " + messageNumber + " " + n, 0);

        return r.getBytes();
    }

    /**
     * Return the UIDL string for the message.
     */
    public synchronized String uidl(final int messageNumber) throws IOException {
        Pop3Response r = simpleCommand(Pop3Command.UIDL + " " + messageNumber);

        if (!r.isOk()) {
            return null;
        }

        int i = r.getData().indexOf(' ');

        if (i > 0) {
            return r.getData().substring(i + 1);
        }

        return null;
    }

    /**
     * Return the UIDL strings for all messages. The UID for msg #N is returned in uids[N-1].
     */
    public synchronized boolean uidl(final String[] uids) throws IOException {
        Pop3Response r = multilineCommand(Pop3Command.UIDL, 15 * uids.length);

        if (!r.isOk()) {
            return false;
        }

        try (LineInputStream lis = new LineInputStream(r.getBytes())) {
            String line = null;

            while ((line = lis.readLine()) != null) {
                int i = line.indexOf(' ');

                if ((i < 1) || (i >= line.length())) {
                    continue;
                }

                int n = Integer.parseInt(line.substring(0, i));

                if ((n > 0) && (n <= uids.length)) {
                    uids[n - 1] = line.substring(i + 1);
                }
            }
        }

        return true;
    }

    @Override
    protected String getDigest(final String password) {
        return super.getDigest(this.apopChallenge + password);
    }

    /**
     * Issue a POP3 command that expects a multi-line response. <code>size</code> is an estimate of the response size.
     */
    private Pop3Response multilineCommand(final String cmd, final int size) throws IOException {
        Pop3Response r = simpleCommand(cmd);

        if (!r.isOk()) {
            return (r);
        }

        try (SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size)) {
            int b;
            int lastb = '\n';

            while ((b = this.inputReader.read()) >= 0) {
                if ((lastb == '\n') && (b == '.')) {
                    b = this.inputReader.read();

                    if (b == '\r') {
                        // end of response, consume LF as well
                        this.inputReader.read();

                        break;
                    }
                }

                buf.write(b);

                lastb = b;
            }

            if (b < 0) {
                throw new EOFException("EOF on serverSocket");
            }

            r.setBytes(buf.toStream());
        }

        return r;
    }

    /**
     * Issue a simple POP3 command and return the response.
     */
    private Pop3Response simpleCommand(final String cmd) throws IOException {
        if (this.serverSocket == null) {
            throw new IOException("Folder is closed");
        }

        String mCmd = cmd;

        if (mCmd != null) {
            getLogger().debug("C: {}", mCmd);

            mCmd += Pop3Command.CRLF;
            this.outputWriter.write(mCmd);
            this.outputWriter.flush();
        }

        String line = this.inputReader.readLine();

        if (line == null) {
            getLogger().debug("S: EOF");

            throw new EOFException("EOF on serverSocket");
        }

        getLogger().debug("S: {}", line);

        Pop3Response r = new Pop3Response();

        if (line.startsWith(Pop3Command.OK)) {
            r.setOk(true);
        }
        else if (line.startsWith(Pop3Command.ERR)) {
            r.setOk(false);
        }
        else {
            throw new IOException("Unexpected response: " + line);
        }

        int i;

        if ((i = line.indexOf(' ')) >= 0) {
            // +OK/-ERR abschneiden
            r.setData(line.substring(i + 1));
        }

        return r;
    }
}
