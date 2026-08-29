package de.freese.base.swing.components.graph.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 * @since 13.11.2022
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
