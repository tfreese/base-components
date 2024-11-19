// Created: 19 Nov. 2024
package de.freese.base.swing.components.graph.javafx;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * @author Thomas Freese
 */
public final class JavaFxStarSimulation extends Application {

    private static final int STAR_COUNT = 5_000;

    private final double[] angles = new double[STAR_COUNT];
    private final Random random = new Random();
    private final Shape[] shapes = new Shape[STAR_COUNT];
    private final long[] start = new long[STAR_COUNT];

    @Override
    public void init() {
        for (int i = 0; i < STAR_COUNT; i++) {
            // shapes[i] = new Rectangle(1D, 1D, Color.WHITE);
            shapes[i] = new Circle(0.5D, Color.WHITE);
            angles[i] = Math.TAU * random.nextDouble();
            start[i] = random.nextInt(2_000_000_000);
        }
    }

    @Override
    public void start(final Stage primaryStage) {
        final Scene scene = new Scene(new Group(shapes), 800D, 600D, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(final long now) {
                final double width = 0.5D * primaryStage.getWidth();
                final double height = 0.5D * primaryStage.getHeight();

                final double radius = Math.sqrt(2D) * Math.max(width, height);

                for (int i = 0; i < STAR_COUNT; i++) {
                    final Shape shape = shapes[i];
                    final double angle = angles[i];
                    final long t = (now - start[i]) % 2_000_000_000;
                    final double d = t * radius / 2_000_000_000.0D;

                    shape.setTranslateX(Math.cos(angle) * d + width);
                    shape.setTranslateY(Math.sin(angle) * d + height);
                }
            }
        }.start();
    }
}
