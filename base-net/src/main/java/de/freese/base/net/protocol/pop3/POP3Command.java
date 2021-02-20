package de.freese.base.net.protocol.pop3;

import de.freese.base.net.protocol.GeneralCommand;

/**
 * POP3 Protokoll Konstanten.
 *
 * @author Thomas Freese
 */
public final class POP3Command
{
    /**
     * Authorization.
     */
    public static final String APOP = "APOP";

    /**
     *
     */
    public static final String CRLF = GeneralCommand.CRLF;

    /**
     * Delete message(s).
     */
    public static final String DELE = "DELE";

    /**
     * Error Response.
     */
    public static final String ERR = "-ERR";

    /**
     * List message(s).
     */
    public static final String LIST = "LIST";

    /**
     *
     */
    public static final String NETASCII_EOL = GeneralCommand.NETASCII_EOL;

    /**
     * No operation. Used as a session keepalive.
     */
    public static final String NOOP = "NOOP";

    /**
     * OK Response.
     */
    public static final String OK = "+OK";

    /**
     * Send password.
     */
    public static final String PASS = "PASS";

    /**
     * Standard POP3 port
     */
    public static final int POP3_PORT = 110;

    /**
     * Quit session.
     */
    public static final String QUIT = "QUIT";

    /**
     * Retrieve message(s).
     */
    public static final String RETR = "RETR";

    /**
     * Reset session.
     */
    public static final String RSET = "RSET";

    /**
     * Get status.
     */
    public static final String STAT = "STAT";

    /**
     * Retrieve top number lines from message.
     */
    public static final String TOP = "TOP";

    /**
     * List unique message identifier(s).
     */
    public static final String UIDL = "UIDL";

    /**
     * Send user name.
     */
    public static final String USER = "USER";

    // /**
    // * OK Response Code.
    // */
    // public static final int OK_Code = 0;
    //
    // /**
    // * ERROR Response Code.
    // */
    // public static final int ERR_Code = 1;

    /**
     * Erstellt ein neues {@link POP3Command} Object.
     */
    private POP3Command()
    {
        super();
    }
}
