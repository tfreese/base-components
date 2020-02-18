/**
 * Created: 15.06.2012
 */

package de.freese.base.core.xml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Thomas Freese
 */
public class StaxPrettyPrintInvocationHandler implements InvocationHandler
{
    /**
     *
     */
    private static final String INDENT_CHAR = " ";

    /**
     *
     */
    private int depth = 0;

    /**
     *
     */
    private Map<Integer, Boolean> hasChildElement = new HashMap<>();

    /**
     *
     */
    private final String lineSeparator;

    /**
     *
     */
    private final XMLStreamWriter target;

    /**
     * Erstellt ein neues {@link StaxPrettyPrintInvocationHandler} Object.
     *
     * @param target {@link XMLStreamWriter}
     */
    public StaxPrettyPrintInvocationHandler(final XMLStreamWriter target)
    {
        super();

        this.target = target;

        this.lineSeparator = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("line.separator"));
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        String m = method.getName();

        // if (true)
        // {
        // System.out.println("StaxPrettyPrintHandler.invoke(): " + m);
        // }

        if ("writeStartElement".equals(m))
        {
            if (this.depth > 0)
            {
                this.hasChildElement.put(this.depth - 1, true);
            }

            this.hasChildElement.put(this.depth, false);

            this.target.writeCharacters(this.lineSeparator);
            this.target.writeCharacters(repeat(this.depth, INDENT_CHAR));

            this.depth++;
        }
        else if ("writeEndElement".equals(m))
        {
            this.depth--;

            if (this.hasChildElement.get(this.depth) == true)
            {
                this.target.writeCharacters(this.lineSeparator);
                this.target.writeCharacters(repeat(this.depth, INDENT_CHAR));
            }

        }
        else if ("writeEmptyElement".equals(m))
        {
            if (this.depth > 0)
            {
                this.hasChildElement.put(this.depth - 1, true);
            }

            this.target.writeCharacters(this.lineSeparator);
            this.target.writeCharacters(repeat(this.depth, INDENT_CHAR));

        }

        method.invoke(this.target, args);

        return null;
    }

    /**
     * @param amount int
     * @param indent String
     * @return String
     */
    private String repeat(final int amount, final String indent)
    {
        if (amount == 0)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (amount * 4); i++)
        {
            sb.append(indent);
        }

        return sb.toString();
    }
}
