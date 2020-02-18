package de.freese.base.net.protocol.pop3;

import de.freese.base.net.protocol.GeneralCommand;

/**
 * POP3 Protokoll Konstanten.
 *
 * @author Thomas Freese
 */
public interface POP3Command extends GeneralCommand
{
    /**
     * Send user name.
     */
    public static final String USER = "USER";

    /**
     * Send password.
     */
    public static final String PASS = "PASS";

    /**
     * Quit session.
     */
    public static final String QUIT = "QUIT";

    /**
     * Get status.
     */
    public static final String STAT = "STAT";

    /**
     * List message(s).
     */
    public static final String LIST = "LIST";

    /**
     * Retrieve message(s).
     */
    public static final String RETR = "RETR";

    /**
     * Delete message(s).
     */
    public static final String DELE = "DELE";

    /**
     * No operation. Used as a session keepalive.
     */
    public static final String NOOP = "NOOP";

    /**
     * Reset session.
     */
    public static final String RSET = "RSET";

    /**
     * Authorization.
     */
    public static final String APOP = "APOP";

    /**
     * Retrieve top number lines from message.
     */
    public static final String TOP = "TOP";

    /**
     * List unique message identifier(s).
     */
    public static final String UIDL = "UIDL";

    /**
     * OK Response.
     */
    public static final String OK = "+OK";

    /**
     * Error Response.
     */
    public static final String ERR = "-ERR";

    /**
     * Standard POP3 port
     */
    public static final int POP3_PORT = 110;

    //    /**
    //     * OK Response Code.
    //     */
    //    public static final int OK_Code = 0;
    //    
    //    /**
    //     * ERROR Response Code.
    //     */
    //    public static final int ERR_Code = 1;
}
