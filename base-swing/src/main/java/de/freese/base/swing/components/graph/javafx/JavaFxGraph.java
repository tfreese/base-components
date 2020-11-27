// Created: 27.11.2020
package de.freese.base.swing.components.graph.javafx;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.swing.components.graph.demo.SinusValueSupplier;
import de.freese.base.swing.components.graph.javafx.painter.AbstractFxGraphPainter;
import de.freese.base.swing.components.graph.javafx.painter.BarFxGraphPainter;
import de.freese.base.swing.components.graph.javafx.painter.LineFxGraphPainter;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Geht momentan nicht aus der IDE, sondern nur per Console: mvn compile exec:java<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Konstruktor muss public sein
 * <li>VM-Parameter: --add-modules javafx.controls
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen
 * </ol>
 *
 * @author Thomas Freese
 */
public class JavaFxGraph extends Application
{
    /**
     * @author Thomas Freese
     */
    private static class CompositeGraphPainter extends AbstractFxGraphPainter
    {
        /**
            *
            */
        private final BarFxGraphPainter barPainter = new BarFxGraphPainter();

        /**
        *
        */
        private final LineFxGraphPainter linePainter = new LineFxGraphPainter();

        /**
         * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#addValue(float)
         */
        @Override
        public synchronized void addValue(final float value)
        {
            this.linePainter.addValue(value);
            this.barPainter.addValue(value);
        }

        /**
         * @see de.freese.base.swing.components.graph.javafx.painter.AbstractFxGraphPainter#paintGraph(javafx.scene.canvas.GraphicsContext, double, double)
         */
        @Override
        public void paintGraph(final GraphicsContext gc, final double width, final double height)
        {
            double halfHeight = height / 2D;

            this.linePainter.paintGraph(gc, width, halfHeight);

            gc.translate(0, halfHeight);
            this.barPainter.paintGraph(gc, width, halfHeight);
            gc.translate(0, -halfHeight);

            double fontSize = 11D;
            Font font = new Font("Arial", fontSize);
            // FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
            // float charHeight = metrics.getLineHeight();

            gc.setFont(font);

            gc.setStroke(Color.MAGENTA);
            gc.strokeText("strokeText", 10D, fontSize); // Bold

            gc.setFill(Color.MAGENTA);
            gc.fillText("fillText", 10D, fontSize * 2D);
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxGraph.class);

    /**
     * @param args final String[]
     */
    public static void main(final String[] args)
    {
        launch(args);
    }

    /**
    *
    */
    private GraphicsContext gc;

    /**
    *
    */
    private ScheduledExecutorService scheduledExecutorService;

    /**
    *
    */
    private Supplier<Float> valueSupplier;

    /**
     * Erstellt ein neues {@link JavaFxGraph} Object.
     */
    public JavaFxGraph()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        getLogger().info("init");
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        // gc.beginPath();
        // gc.moveTo(xOffset, yLast);
        // gc.lineTo(x, y);
        // gc.closePath();
        // gc.stroke();

        getLogger().info("start");

        this.valueSupplier = new SinusValueSupplier();

        Canvas canvas = new Canvas();
        this.gc = canvas.getGraphicsContext2D();

        Group pane = new Group();
        pane.getChildren().add(canvas);

        // GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        Scene scene = new Scene(pane, 335, 1060, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialising: {}", scene.getAntiAliasing());

        // Transparenz
        boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported)
        {
            // Fenster wird hierbei undecorated, aber der Graph wird normal gezeichnet.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, 0.5D));

            // For Containers
            // pane.setBackground(Background.EMPTY);
            // pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            // pane.setStyle("-fx-background-color: transparent;");

            // Das gesamte Fenster wird transparent, inklusive Titelleiste und Graph.
            // primaryStage.setOpacity(0.3D);
        }
        else
        {
            scene.setFill(Color.BLACK);
        }

        CompositeGraphPainter graphPainter = new CompositeGraphPainter();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            float value = this.valueSupplier.get();
            graphPainter.addValue(value);

            if (Platform.isFxApplicationThread())
            {
                graphPainter.paint(this.gc, canvas.getWidth(), canvas.getHeight());
            }
            else
            {
                Platform.runLater(() -> {
                    graphPainter.paint(this.gc, canvas.getWidth(), canvas.getHeight());
                });
            }
        }, 500, 40, TimeUnit.MILLISECONDS);

        primaryStage.setTitle("Graph Monitor");
        primaryStage.setScene(scene);

        List<Screen> screens = Screen.getScreens();

        // Auf dem 2. Monitor
        Screen screen = screens.get(screens.size() - 1);

        primaryStage.setX(screen.getVisualBounds().getMinX() + 1200);
        primaryStage.setY(10D);

        primaryStage.show();
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        getLogger().info("stop");

        this.scheduledExecutorService.shutdown();

        System.exit(0);
    }
}
