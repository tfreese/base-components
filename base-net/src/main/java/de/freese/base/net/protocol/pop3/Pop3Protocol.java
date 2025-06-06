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

import org.eclipse.angus.mail.util.LineInputStream;
import org.eclipse.angus.mail.util.SharedByteArrayOutputStream;

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

        final Properties myProperties = props != null ? props : new Properties();
        final int myPort = port > 0 ? port : Pop3Command.POP3_PORT;

        Pop3Response response = new Pop3Response();
        final String apop = myProperties.getProperty(propPrefix + ".apop.enable");
        final boolean enableAPOP = "true".equalsIgnoreCase(apop);

        try {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("connecting to host \"{}\", port {}, isSSL {}", host, myPort, isSSL);
            }

            if (isSSL) {
                final SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                serverSocket = socketFactory.createSocket(host, myPort);
                ((SSLSocket) serverSocket).setEnabledProtocols(new String[]{"TLSv3"});
            }
            else {
                serverSocket = new Socket(host, myPort);
            }

            // should be US-ASCII, but not all JDK's support
            inputReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.ISO_8859_1));
            outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.ISO_8859_1)));

            response = simpleCommand(null);
        }
        catch (IOException ioe) {
            try {
                serverSocket.close();
            }
            catch (Exception th) {
                throw new IOException("Connect failed: ", th);
            }
        }

        if (!response.isOk()) {
            try {
                serverSocket.close();
            }
            catch (IOException th) {
                throw new IOException("Connect failed");
            }
        }

        if (enableAPOP) {
            final int challStart = response.getData().indexOf('<'); // start of the challenge
            final int challEnd = response.getData().indexOf('>', challStart); // end of the challenge

            if (challStart != -1 && challEnd != -1) {
                apopChallenge = response.getData().substring(challStart, challEnd + 1);
            }

            getLogger().debug("APOP challenge: {}", apopChallenge);
        }
    }

    @Override
    public void close() {
        try {
            if (serverSocket != null) {
                // Forgot to log out?!
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
        final Pop3Response r = simpleCommand(Pop3Command.DELE + " " + messageNumber);

        return r.isOk();
    }

    /**
     * Return the size of the message using the LIST command.
     */
    public synchronized int list(final int messageNumber) throws IOException {
        final Pop3Response r = simpleCommand(Pop3Command.LIST + " " + messageNumber);
        int size = -1;

        if (r.isOk() && r.getData() != null) {
            try {
                final StringTokenizer st = new StringTokenizer(r.getData());
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

        if (apopChallenge != null) {
            dpw = getDigest(password);
        }

        if (apopChallenge != null && dpw != null) {
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
        final Pop3Response r = simpleCommand(Pop3Command.NOOP);

        return r.isOk();
    }

    /**
     * Close down the connection, sending the QUIT command if expunge is true.
     */
    public synchronized boolean quit() throws IOException {
        boolean ok = false;

        try {
            final Pop3Response r = simpleCommand(Pop3Command.QUIT);
            ok = r.isOk();
        }
        finally {
            try {
                serverSocket.close();
            }
            finally {
                serverSocket = null;
                inputReader = null;
                outputWriter = null;
            }
        }

        return ok;
    }

    /**
     * Retrieve the specified message. Given an estimate of the message's size, we can be more efficient, pre-allocating the array and returning a
     * ISharedInputStream to allow us to share the array.
     */
    public synchronized InputStream retr(final int messageNumber, final int size) throws IOException {
        final Pop3Response r = multilineCommand(Pop3Command.RETR + " " + messageNumber, size);

        return r.getBytes();
    }

    /**
     * Do an RESET.
     */
    public synchronized boolean rset() throws IOException {
        final Pop3Response r = simpleCommand(Pop3Command.RSET);

        return r.isOk();
    }

    /**
     * Return the total number of messages and mailbox size, using the STAT command.
     *
     * @return int[]; 0 = Messages, 1 = Size of Messages
     */
    public synchronized int[] stat() throws IOException {
        final Pop3Response r = simpleCommand(Pop3Command.STAT);
        final int[] result = new int[2];

        if (r.isOk() && r.getData() != null) {
            try {
                final StringTokenizer st = new StringTokenizer(r.getData());

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
        final Pop3Response r = multilineCommand(Pop3Command.TOP + " " + messageNumber + " " + n, 0);

        return r.getBytes();
    }

    /**
     * Return the UIDL string for the message.
     */
    public synchronized String uidl(final int messageNumber) throws IOException {
        final Pop3Response r = simpleCommand(Pop3Command.UIDL + " " + messageNumber);

        if (!r.isOk()) {
            return null;
        }

        final int i = r.getData().indexOf(' ');

        if (i > 0) {
            return r.getData().substring(i + 1);
        }

        return null;
    }

    /**
     * Return the UIDL strings for all messages. The UID for msg #N is returned in uids[N-1].
     */
    public synchronized boolean uidl(final String[] uids) throws IOException {
        final Pop3Response r = multilineCommand(Pop3Command.UIDL, 15 * uids.length);

        if (!r.isOk()) {
            return false;
        }

        try (LineInputStream lis = new LineInputStream(r.getBytes())) {
            String line = null;

            while ((line = lis.readLine()) != null) {
                final int i = line.indexOf(' ');

                if (i < 1 || i >= line.length()) {
                    continue;
                }

                final int n = Integer.parseInt(line.substring(0, i));

                if (n > 0 && n <= uids.length) {
                    uids[n - 1] = line.substring(i + 1);
                }
            }
        }

        return true;
    }

    @Override
    protected String getDigest(final String password) {
        return super.getDigest(apopChallenge + password);
    }

    /**
     * Issue a POP3 command that expects a multi-line response. <code>size</code> is an estimate of the response size.
     */
    private Pop3Response multilineCommand(final String cmd, final int size) throws IOException {
        final Pop3Response r = simpleCommand(cmd);

        if (!r.isOk()) {
            return r;
        }

        try (SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size)) {
            int b;
            int lastb = '\n';

            while ((b = inputReader.read()) >= 0) {
                if (lastb == '\n' && b == '.') {
                    b = inputReader.read();

                    if (b == '\r') {
                        // end of response, consume LF as well
                        inputReader.read();

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
        if (serverSocket == null) {
            throw new IOException("Folder is closed");
        }

        String mCmd = cmd;

        if (mCmd != null) {
            getLogger().debug("C: {}", mCmd);

            mCmd += Pop3Command.CRLF;
            outputWriter.write(mCmd);
            outputWriter.flush();
        }

        final String line = inputReader.readLine();

        if (line == null) {
            getLogger().debug("S: EOF");

            throw new EOFException("EOF on serverSocket");
        }

        getLogger().debug("S: {}", line);

        final Pop3Response r = new Pop3Response();

        if (line.startsWith(Pop3Command.OK)) {
            r.setOk(true);
        }
        else if (line.startsWith(Pop3Command.ERR)) {
            r.setOk(false);
        }
        else {
            throw new IOException("Unexpected response: " + line);
        }

        final int index = line.indexOf(' ');

        if (index >= 0) {
            // +OK/-ERR abschneiden
            r.setData(line.substring(index + 1));
        }

        return r;
    }
}
