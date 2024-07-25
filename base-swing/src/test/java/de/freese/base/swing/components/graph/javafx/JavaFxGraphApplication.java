// Created: 25 Juli 2024
package de.freese.base.swing.components.graph.javafx;

import java.util.List;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.components.graph.model.SinusValueSupplier;
import de.freese.base.swing.components.graph.model.Values;

/**
 * @author Thomas Freese
 */
public final class JavaFxGraphApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxGraphApplication.class);

    private final Values<Float> values = new Values<>();

    private GraphicsContext gc;
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Public Constructor required by JavaFX.
     */
    public JavaFxGraphApplication() {
        super();
    }

    @Override
    public void init() throws Exception {
        LOGGER.info("init");
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        LOGGER.info("start");

        final Canvas canvas = new Canvas();
        this.gc = canvas.getGraphicsContext2D();

        final Group group = new Group();
        group.getChildren().add(canvas);

        // Scene
        final Scene scene = new Scene(group, 1280, 768, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        LOGGER.info("Anti-Aliasing: {}", scene.getAntiAliasing());

        // Transparenz
        final boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);

        // Decorated Frame
        //        isTransparentSupported = false;

        if (isTransparentSupported) {
            // Das Fenster wird hierbei undecorated, aber der Graph wird normal gezeichnet.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, 0.3D));

            // For Containers
            // pane.setBackground(Background.EMPTY);
            // group.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
            // pane.setStyle("-fx-background-color: transparent;");

            // Das gesamte Fenster wird transparent, inklusive Titelleiste und Graph.
            // primaryStage.setOpacity(0.3D);
        }
        else {
            scene.setFill(Color.BLACK);
        }

        final Supplier<Float> valueSupplier = new SinusValueSupplier();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            this.values.addValue(valueSupplier.get());

            final double width = this.gc.getCanvas().getWidth();
            final double height = this.gc.getCanvas().getHeight();

            Platform.runLater(() -> paintGraph(width, height));
        }, 500L, 40L, TimeUnit.MILLISECONDS);

        primaryStage.setTitle("JavaFX Graph Monitor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stop");

        this.scheduledExecutorService.shutdown();

        System.exit(0);
    }

    private void paintGraph(final double width, final double height) {
        // this.gc.fillRect(75D, 75D, 100D, 100D);

        this.gc.clearRect(0D, 0D, width, height);

        // Rahmen
        this.gc.setStroke(Color.RED);
        this.gc.strokeRect(0D, 0D, width, height);

        // this.gc.setFill(Color.BLACK);
        // // this.gc.strokeText(Float.toString(this.valueSupplier.get()), 100D, 80D); // Bold
        // this.gc.fillText(Float.toString(this.valueSupplier.get()), 100D, 100D); // Normal

        final List<Float> valueList = this.values.getLastValues((int) width);

        if (valueList.isEmpty()) {
            return;
        }

        final double xOffset = width - valueList.size(); // Diagramm von rechts aufbauen.
        // float xOffset = 0F; // Diagramm von links aufbauen.

        final Stop[] stops = new Stop[]{new Stop(0D, Color.RED), new Stop(1D, Color.GREEN)};

        // Für Balken
        this.gc.setFill(new LinearGradient(0D, 0D, 0D, height, false, CycleMethod.NO_CYCLE, stops));

        // Für Linien
        this.gc.setStroke(new LinearGradient(0D, 0D, 0D, height, false, CycleMethod.NO_CYCLE, stops));

        double yLast = valueList.getFirst();

        // Sinus: x-Achse auf halber Höhe
        final double middle = height / 2D;

        yLast = (yLast * middle) + middle;

        final boolean useBars = true;

        for (int i = 1; i < valueList.size(); i++) {
            final double value = valueList.get(i);

            final double x = i + xOffset;
            final double y;

            if (useBars) {
                y = Math.abs(value * middle);
                // y = middle - (value * middle);
                // this.gc.fillRect(x, middle, 1D, y);

                if (value > 0D) {
                    this.gc.fillRect(x, middle - y, 1D, y);
                }
                else {
                    this.gc.fillRect(x, middle, 1D, y);
                }
            }
            else {
                y = middle - (value * middle);
                this.gc.strokeLine(x - 1D, yLast, x, y);
                yLast = y;
            }
        }

        // this.gc.beginPath();
        // this.gc.moveTo(xOffset, yLast);
        // this.gc.lineTo(x, y); // gc.beginPath();gc.stroke()
        // this.gc.closePath();
        // this.gc.stroke();
    }
}
