// Created: 15.06.2012
package de.freese.base.core.xml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Thomas Freese
 */
public class StaxPrettyPrintInvocationHandler implements InvocationHandler {
    private static final String INDENT_CHAR = " ";

    private final Map<Integer, Boolean> hasChildElement = new HashMap<>();
    private final String lineSeparator;
    private final XMLStreamWriter target;

    private int depth;

    public StaxPrettyPrintInvocationHandler(final XMLStreamWriter target) {
        super();

        this.target = target;

        this.lineSeparator = System.lineSeparator();
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String m = method.getName();

        // if (true) {
        // System.out.println("StaxPrettyPrintHandler.invoke(): " + m);
        // }

        switch (m) {
            case "writeStartElement" -> {
                if (depth > 0) {
                    hasChildElement.put(depth - 1, true);
                }

                hasChildElement.put(depth, false);
                target.writeCharacters(lineSeparator);
                target.writeCharacters(indent(depth, INDENT_CHAR));
                depth++;
            }
            case "writeEndElement" -> {
                depth--;

                if (hasChildElement.getOrDefault(depth, false)) {
                    target.writeCharacters(lineSeparator);
                    target.writeCharacters(indent(depth, INDENT_CHAR));
                }
            }
            case "writeEmptyElement" -> {
                if (depth > 0) {
                    hasChildElement.put(depth - 1, true);
                }

                target.writeCharacters(lineSeparator);
                target.writeCharacters(indent(depth, INDENT_CHAR));
            }
            default -> {
                // Empty
            }
        }

        method.invoke(target, args);

        return null;
    }

    private String indent(final int amount, final String indent) {
        if (amount == 0) {
            return null;
        }

        return indent.repeat(amount * 4);
    }
}
