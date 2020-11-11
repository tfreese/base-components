package de.freese.base.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.layout.GbcBuilder;
import de.freese.base.swing.task.SwingTask;
import de.freese.base.swing.task.TaskManager;

/**
 * Diese StatusBar registriert sich als Listener am {@link TaskManager} und reagiert auf Events des aktuellen ForegroundTasks.<br>
 * Eine {@link ResourceMap} mit dem Namen "statusbar" muss im {@link ApplicationContext} vorhanden sein.
 */
public class StatusBar extends JPanel implements PropertyChangeListener
{
    /**
     *
     */
    private static final long serialVersionUID = -5987325109823650807L;

    /**
     *
     */
    private final int busyAnimationRate;

    /**
     *
     */
    private int busyIconIndex = 0;

    /**
     *
     */
    private final Icon[] busyIcons = new Icon[15];

    /**
     *
     */
    private final Timer busyIconTimer;

    /**
     *
     */
    private final Icon idleIcon;

    /**
     *
     */
    private JLabel messageLabel;

    /**
     *
     */
    private final Timer messageTimer;

    /**
     *
     */
    private JProgressBar progressBar;

    /**
     *
     */
    private JLabel statusAnimationLabel;

    /**
     *
     */
    private final Insets zeroInsets = new Insets(0, 0, 0, 0);

    /**
     * Erstellt ein neues {@link StatusBar} Object.<br>
     * Die StatusBar reagiert auf Events des aktuell im {@link TaskManager} enthaltenen ForegroundTasks.
     *
     * @param context {@link ApplicationContext}
     */
    public StatusBar(final ApplicationContext context)
    {
        super();

        ResourceMap resourceMap = context.getResourceMap("statusbar");

        Objects.requireNonNull(resourceMap, "resourceMap required");

        Integer messageTimeout = resourceMap.getInteger("statusbar.message.timeout");
        this.messageTimer = new Timer(messageTimeout.intValue(), event -> this.messageLabel.setText(""));
        this.messageTimer.setRepeats(false);
        this.busyAnimationRate = resourceMap.getInteger("statusbar.animation.rate").intValue();
        this.idleIcon = resourceMap.getIcon("statusbar.icon.idle");

        for (int i = 0; i < this.busyIcons.length; i++)
        {
            this.busyIcons[i] = resourceMap.getIcon("statusbar.icon." + i);
        }

        this.busyIconTimer = new Timer(this.busyAnimationRate, event -> {
            this.busyIconIndex = (this.busyIconIndex + 1) % this.busyIcons.length;
            this.statusAnimationLabel.setIcon(this.busyIcons[this.busyIconIndex]);
        });

        context.getTaskManager().addPropertyChangeListener(this);
    }

    /**
     * Initialisiert die GUI.
     */
    public void initialize()
    {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(this.zeroInsets));

        this.messageLabel = new JLabel();

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setEnabled(false);

        this.statusAnimationLabel = new JLabel();
        this.statusAnimationLabel.setIcon(this.idleIcon);

        add(new JSeparator(), new GbcBuilder(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE).gridwidth(GridBagConstraints.REMAINDER).fillHorizontal()
                .insets(this.zeroInsets));

        add(this.messageLabel, new GbcBuilder(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE).insets(2, 6, 2, 3).fillHorizontal());

        add(this.progressBar, new GbcBuilder(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE).insets(2, 3, 2, 3));

        add(this.statusAnimationLabel, new GbcBuilder(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE).insets(2, 3, 2, 6));
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        String propertyName = evt.getPropertyName();

        if (SwingTask.PROPERTY_STARTED.equals(propertyName))
        {
            showBusyAnimation();
            this.progressBar.setEnabled(true);
            this.progressBar.setIndeterminate(true);
        }
        else if (SwingTask.PROPERTY_SUBTITLE.equals(propertyName))
        {
            String text = (String) (evt.getNewValue());
            setMessage(text);
        }
        else if (SwingTask.PROPERTY_PROGRESS.equals(propertyName))
        {
            int value = ((Integer) (evt.getNewValue())).intValue();
            this.progressBar.setEnabled(true);
            this.progressBar.setIndeterminate(false);
            this.progressBar.setValue(value);
        }
        else if (SwingTask.PROPERTY_CANCELLED.equals(propertyName) || SwingTask.PROPERTY_FAILED.equals(propertyName)
                || SwingTask.PROPERTY_SUCCEEDED.equals(propertyName))
        {
            // Kein Task in Ausführung
            stopBusyAnimation();
            this.progressBar.setIndeterminate(false);
            this.progressBar.setEnabled(false);
            this.progressBar.setValue(0);
        }
    }

    /**
     * Nachricht in der Fußleiste.
     *
     * @param s String
     */
    public void setMessage(final String s)
    {
        this.messageLabel.setText((s == null) ? "" : s);
        this.messageTimer.restart();
    }

    /**
     * Zeigt die Busy-Animation.
     */
    public void showBusyAnimation()
    {
        if (!this.busyIconTimer.isRunning())
        {
            this.statusAnimationLabel.setIcon(this.busyIcons[0]);
            this.busyIconIndex = 0;
            this.busyIconTimer.start();
        }
    }

    /**
     * Stoppt die Busy-Animation.
     */
    public void stopBusyAnimation()
    {
        this.busyIconTimer.stop();
        this.statusAnimationLabel.setIcon(this.idleIcon);
    }
}
