// Created: 22.09.2015
package de.freese.base.swing.components.graph.javafx;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen</li>
 * </ol>
 *
 * @author Thomas Freese
 */
public final class JavaFxAnimation extends Application {
    @Override
    public void start(final Stage stage) {
        final Group root = new Group();
        final Scene scene = new Scene(root, 500, 500, Color.BLACK);
        final Rectangle r = new Rectangle(0, 0, 250, 250);
        r.setFill(Color.BLUE);
        root.getChildren().add(r);

        final TranslateTransition translate = new TranslateTransition(Duration.millis(750));
        translate.setToX(390);
        translate.setToY(390);
        // translate.toXProperty().bind(stage.widthProperty());
        // translate.toYProperty().bind(stage.heightProperty());

        final FillTransition fill = new FillTransition(Duration.millis(750));
        fill.setToValue(Color.RED);

        final RotateTransition rotate = new RotateTransition(Duration.millis(750));
        rotate.setToAngle(360);

        final ScaleTransition scale = new ScaleTransition(Duration.millis(750));
        scale.setToX(0.1);
        scale.setToY(0.1);

        final ParallelTransition transition = new ParallelTransition(r, translate, fill, rotate, scale);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();

        stage.setTitle("JavaFX Scene Graph Demo");
        stage.setScene(scene);
        stage.show();
    }
}
