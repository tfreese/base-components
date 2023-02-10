// Created: 13.11.22
package de.freese.base.swing.exception;

import java.util.logging.Level;

import org.jdesktop.swingx.error.ErrorInfo;

/**
 * @author Thomas Freese
 */
public final class ErrorPaneMain {
    public static void main(final String[] args) {
        try {
            Exception cause = new Exception("I'm the cause");
            throw new Exception("I'm a secondary exception", cause);
        }
        catch (Exception ex) {
            ErrorInfo errorInfo = new ErrorInfo("ErrorTitle", "basic error message", null, "category", ex, Level.ALL, null);

            ErrorPane.showDialog(null, errorInfo, false);
        }
    }

    private ErrorPaneMain() {
        super();
    }
}
