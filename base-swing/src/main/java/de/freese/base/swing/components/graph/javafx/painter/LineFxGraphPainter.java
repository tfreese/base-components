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
public class LineFxGraphPainter extends AbstractFxGraphPainter
{
    /**
     * Erstellt ein neues {@link LineFxGraphPainter} Object.
     */
    public LineFxGraphPainter()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.components.graph.javafx.painter.AbstractFxGraphPainter#paintGraph(javafx.scene.canvas.GraphicsContext, double, double)
     */
    @Override
    public void paintGraph(final GraphicsContext gc, final double width, final double height)
    {
        List<Float> values = getLastValues((int) width);

        if (values.isEmpty())
        {
            return;
        }

        double xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // double xOffset = 0F; // Diagramm von links aufbauen.

        Stop[] stops = new Stop[]
        {
                new Stop(0D, Color.RED), new Stop(1D, Color.GREEN)
        };

        gc.setStroke(new LinearGradient(0D, 0D, 0D, height, false, CycleMethod.NO_CYCLE, stops));

        // Sinus: X-Achse auf halber Höhe
        double middle = height / 2D;
        double yLast = middle - (values.get(0) * middle);

        for (int i = 1; i < values.size(); i++)
        {
            float value = values.get(i);

            double x = i + xOffset;
            double y = middle - (value * middle);

            gc.strokeLine(x - 1, yLast, x, y);

            yLast = y;
        }
    }
}
