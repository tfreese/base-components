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

        this.progressBar = new JProgressBar();
        this.progressBar.setFont(defaultFont.deriveFont(Font.BOLD, defaultFont.getSize()));
        this.progressBar.setIndeterminate(true);
        this.progressBar.setStringPainted(false);
        this.progressBar.setMinimum(0);
        this.progressBar.setMaximum(100);

        this.labelTitle = new JLabel();
        this.labelTitle.setFont(defaultFont.deriveFont(Font.BOLD, defaultFont.getSize() * 2F));

        this.labelSubTitle = new JLabel();
        this.labelSubTitle.setForeground(Color.BLACK);

        getGlassPane().setLayout(new GridBagLayout());

        // GridBagConstraints gbc = GuiUtils.getGBC(0, 0);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weighty = 0;
        // gbc.insets = new Insets(5, 5, 0, 5);
        getGlassPane().add(this.labelTitle, GbcBuilder.of(0, 0));

        // gbc = GuiUtils.getGBC(0, 1);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weighty = 0;
        // gbc.insets = new Insets(0, 5, 0, 5);
        getGlassPane().add(this.progressBar, GbcBuilder.of(0, 1));

        // gbc = GuiUtils.getGBC(0, 2);
        // gbc.fill = GridBagConstraints.NONE;
        // gbc.weighty = 0;
        // gbc.insets = new Insets(0, 5, 0, 5);
        getGlassPane().add(this.labelSubTitle, GbcBuilder.of(0, 2));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String propertyName = event.getPropertyName();

        if (SwingTask.PROPERTY_PROGRESS.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            if (this.progressBar.isIndeterminate()) {
                this.progressBar.setIndeterminate(false);
                this.progressBar.setStringPainted(true);
            }

            this.progressBar.setValue(Integer.parseInt(event.getNewValue().toString()));

            // final StringBuilder sb = new StringBuilder();
            // sb.append(task.getTitle() == null ? "" : task.getTitle());
            // sb.append(" ").append(evt.getNewValue().toString());
            // sb.append(" %");
            //
            // this.progressBar.setString(sb.toString());

        }
        else if (SwingTask.PROPERTY_TITLE.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            this.labelTitle.setText(task.getTitle() == null ? "" : task.getTitle());
        }
        else if (SwingTask.PROPERTY_SUBTITLE.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            if (task.isDone()) {
                return;
            }

            this.labelSubTitle.setText(task.getSubTitle() == null ? "" : task.getSubTitle());
        }
    }
}
