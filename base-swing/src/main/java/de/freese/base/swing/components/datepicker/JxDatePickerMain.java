// Created: 13.11.22
package de.freese.base.swing.components.datepicker;

import java.awt.BorderLayout;
import java.time.LocalDate;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;

/**
 * Alternative: <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
public final class JxDatePickerMain {
    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel jLabel = new JLabel();

        final JFXPanel fxPanel = new JFXPanel();

        Platform.runLater(() -> {
            final Group root = new Group();
            final Scene scene = new Scene(root);

            final DatePicker datePicker = new DatePicker();
            datePicker.setShowWeekNumbers(true);
            datePicker.setPromptText("yyyy-mm-dd");
            datePicker.setEditable(true);
            datePicker.setOnAction(event -> {
                final LocalDate localDate = datePicker.getValue();

                jLabel.setText("localDate = " + localDate);
            });

            root.getChildren().add(datePicker);

            fxPanel.setScene(scene);
        });

        frame.getContentPane().add(BorderLayout.CENTER, fxPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, jLabel);

        frame.setSize(300, 300);
        // frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JxDatePickerMain() {
        super();
    }
}
