// Created: 27.11.2020
package de.freese.base.swing.components.graph.javafx.painter;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class LineFxGraphPainter extends AbstractFxGraphPainter {
    @Override
    public void paintGraph(final GraphicsContext gc, final double width, final double height) {
        final List<Float> values = getValues().getLastValues((int) width);

        if (values.isEmpty()) {
            return;
        }

        final double xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // final double xOffset = 0F; // Diagramm von links aufbauen.

        final Stop[] stops = {new Stop(0D, Color.RED), new Stop(1D, Color.GREEN)};

        gc.setStroke(new LinearGradient(0D, 0D, 0D, height, false, CycleMethod.NO_CYCLE, stops));

        // Sinus: x-Achse auf halber Höhe
        final double middle = height / 2D;
        double yLast = middle - (values.getFirst() * middle);

        for (int i = 1; i < values.size(); i++) {
            final float value = values.get(i);

            final double x = i + xOffset;
            final double y = middle - (value * middle);

            gc.strokeLine(x - 1D, yLast, x, y);

            yLast = y;
        }
    }
}
