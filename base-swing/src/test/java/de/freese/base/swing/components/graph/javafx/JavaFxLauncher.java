// Created: 13.11.22
package de.freese.base.swing.components.graph.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class JavaFxLauncher {
    static void main(final String[] args) {
        // Application.launch(JavaFxAnimation.class, args);
        // Application.launch(JavaFxGraphApplication.class, args);
        Application.launch(JavaFxStarSimulation.class, args);
    }

    private JavaFxLauncher() {
        super();
    }
}
