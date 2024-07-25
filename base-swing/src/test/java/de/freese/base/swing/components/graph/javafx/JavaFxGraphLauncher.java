// Created: 18.01.2021
package de.freese.base.swing.components.graph.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class JavaFxGraphLauncher {
    public static void main(final String[] args) {
        Application.launch(JavaFxGraphApplication.class, args);
    }

    private JavaFxGraphLauncher() {
        super();
    }
}
