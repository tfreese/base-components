package de.freese.base.net.protocol;

/**
 * Allgemeine Netzwerkprotokoll-Konstanten.
 *
 * @author Thomas Freese
 */
public final class GeneralCommand
{
    /**
     * The end of line character sequence used by most IETF protocols. That is a carriage return followed by a newline: "\r\n" (NETASCII_EOL)
     */
    public static final String CRLF = "\r\n";

    public static final String NETASCII_EOL = CRLF;
    
    private GeneralCommand()
    {
        super();
    }
}
