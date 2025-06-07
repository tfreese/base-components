package de.freese.base.swing.task.inputblocker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.freese.base.swing.fontchange.SwingFontSizeChanger;
import de.freese.base.swing.layout.GbcBuilder;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.SwingTask;

/**
 * Blockiert die gesamte Anwendung mit einer GlassPane und zeigt den Titel des Tasks und den Progress-Wert an.
 *
 * @author Thomas Freese
 */
public class DefaultGlassPaneInputBlocker extends AbstractGlassPaneInputBlocker {
    private final JLabel labelSubTitle;
    private final JLabel labelTitle;
    private final JProgressBar progressBar;

    public DefaultGlassPaneInputBlocker(final Component target) {
        super(target);

        final Font defaultFont = SwingFontSizeChanger.getInstance().getFont();

        progressBar = new JProgressBar();
        progressBar.setFont(defaultFont.deriveFont(Font.BOLD, defaultFont.getSize()));
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        labelTitle = new JLabel();
        labelTitle.setFont(defaultFont.deriveFont(Font.BOLD, defaultFont.getSize() * 2F));

        labelSubTitle = new JLabel();
        labelSubTitle.setForeground(Color.BLACK);

        getGlassPane().setLayout(new GridBagLayout());

        // GridBagConstraints gbc = GuiUtils.getGBC(0, 0);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weightY = 0;
        // gbc.insets = new Insets(5, 5, 0, 5);
        getGlassPane().add(labelTitle, GbcBuilder.of(0, 0));

        // gbc = GuiUtils.getGBC(0, 1);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weightY = 0;
        // gbc.insets = new Insets(0, 5, 0, 5);
        getGlassPane().add(progressBar, GbcBuilder.of(0, 1));

        // gbc = GuiUtils.getGBC(0, 2);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weightY = 0;
        // gbc.insets = new Insets(0, 5, 0, 5);
        getGlassPane().add(labelSubTitle, GbcBuilder.of(0, 2));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String propertyName = event.getPropertyName();

        if (SwingTask.PROPERTY_PROGRESS.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            if (progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(false);
                progressBar.setStringPainted(true);
            }

            progressBar.setValue(Integer.parseInt(event.getNewValue().toString()));

            // final StringBuilder sb = new StringBuilder();
            // sb.append(task.getTitle() == null ? "" : task.getTitle());
            // sb.append(" ").append(evt.getNewValue().toString());
            // sb.append(" %");
            //
            // progressBar.setString(sb.toString());

        }
        else if (SwingTask.PROPERTY_TITLE.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            labelTitle.setText(task.getTitle() == null ? "" : task.getTitle());
        }
        else if (SwingTask.PROPERTY_SUBTITLE.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            labelSubTitle.setText(task.getSubTitle() == null ? "" : task.getSubTitle());
        }
    }
}
