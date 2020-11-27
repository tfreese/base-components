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
public class BarFxGraphPainter extends AbstractFxGraphPainter
{
    /**
     * Erstellt ein neues {@link BarFxGraphPainter} Object.
     */
    public BarFxGraphPainter()
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

        gc.setFill(new LinearGradient(0D, 0D, 0D, height, false, CycleMethod.NO_CYCLE, stops));

        // Sinus: X-Achse auf halber Höhe
        double middle = height / 2D;

        for (int i = 0; i < values.size(); i++)
        {
            float value = values.get(i);

            double x = i + xOffset;
            double y = Math.abs(value * middle);

            if (value > 0D)
            {
                gc.fillRect(x, middle - y, 1, y);
            }
            else
            {
                gc.fillRect(x, middle, 1, y);
            }
        }
    }
}
