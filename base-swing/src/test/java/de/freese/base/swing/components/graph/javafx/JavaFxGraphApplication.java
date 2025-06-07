// Created: 27.11.2020
package de.freese.base.swing.components.graph.javafx;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.components.graph.javafx.painter.AbstractFxGraphPainter;
import de.freese.base.swing.components.graph.javafx.painter.BarFxGraphPainter;
import de.freese.base.swing.components.graph.javafx.painter.LineFxGraphPainter;
import de.freese.base.swing.components.graph.model.SinusValueSupplier;

/**
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen</li>s
 * </ol>
 *
 * @author Thomas Freese
 */
public final class JavaFxGraphApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxGraphApplication.class);

    /**
     * @author Thomas Freese
     */
    private static final class CompositeGraphPainter extends AbstractFxGraphPainter {
        private final BarFxGraphPainter barPainter = new BarFxGraphPainter();
        private final LineFxGraphPainter linePainter = new LineFxGraphPainter();

        public synchronized void addValue(final float value) {
            linePainter.getValues().addValue(value);
            barPainter.getValues().addValue(value);
        }

        @Override
        public void paintGraph(final GraphicsContext gc, final double width, final double height) {
            final double halfHeight = height / 2D;

            linePainter.paintGraph(gc, width, halfHeight);

            gc.translate(0D, halfHeight);
            barPainter.paintGraph(gc, width, halfHeight);
            gc.translate(0D, -halfHeight);

            final double fontSize = 11D;
            final Font font = new Font("Arial", fontSize);
            // final FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
            // float charHeight = metrics.getLineHeight();

            gc.setFont(font);

            gc.setStroke(Color.MAGENTA);
            gc.strokeText("strokeText", 10D, fontSize); // Bold

            gc.setFill(Color.MAGENTA);
            gc.fillText("fillText", 10D, fontSize * 2D);
        }
    }

    private GraphicsContext gc;
    private ScheduledExecutorService scheduledExecutorService;
    private Supplier<Float> valueSupplier;

    @Override
    public void init() {
        getLogger().info("init");
    }

    @Override
    public void start(final Stage primaryStage) {
        // gc.beginPath();
        // gc.moveTo(xOffset, yLast);
        // gc.lineTo(x, y);
        // gc.closePath();
        // gc.stroke();

        getLogger().info("start");

        valueSupplier = new SinusValueSupplier();

        final Canvas canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        final Group pane = new Group(canvas);
        // pane.getChildren().add(canvas);

        // final GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        final Scene scene = new Scene(pane, 335D, 1060D, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialiasing: {}", scene.getAntiAliasing());

        // Transparenz
        final boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported) {
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
        else {
            scene.setFill(Color.BLACK);
        }

        final CompositeGraphPainter graphPainter = new CompositeGraphPainter();

        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            final float value = valueSupplier.get();
            graphPainter.addValue(value);

            if (Platform.isFxApplicationThread()) {
                graphPainter.paint(gc, canvas.getWidth(), canvas.getHeight());
            }
            else {
                Platform.runLater(() -> graphPainter.paint(gc, canvas.getWidth(), canvas.getHeight()));
            }
        }, 500L, 40L, TimeUnit.MILLISECONDS);

        primaryStage.setTitle("Graph Monitor");
        primaryStage.setScene(scene);

        // Auf dem 2. Monitor
        // final List<Screen> screens = Screen.getScreens();
        // final Screen screen = screens.get(screens.size() - 1);
        // primaryStage.setX(screen.getVisualBounds().getMinX() + 1200);
        // primaryStage.setY(10D);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stop();
            }
            catch (Exception ex) {
                // Ignore
            }
        }, "ShutdownHook"));

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        getLogger().info("stop");

        scheduledExecutorService.close();

        scheduledExecutorService.awaitTermination(3L, TimeUnit.SECONDS);

        // System.exit(0); // Blockiert, wegen ShutdownHook.
    }

    private Logger getLogger() {
        return LOGGER;
    }
}
