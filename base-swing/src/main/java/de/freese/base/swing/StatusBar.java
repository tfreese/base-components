package de.freese.base.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = -5987325109823650807L;

    private final Timer busyIconTimer;

    private final transient Icon[] busyIcons = new Icon[15];

    private final transient Icon idleIcon;

    private final Timer messageTimer;

    private final Insets zeroInsets = new Insets(0, 0, 0, 0);

    private int busyIconIndex;

    private JLabel messageLabel;

    private JProgressBar progressBar;

    private JLabel statusAnimationLabel;

    /**
     * Die StatusBar reagiert auf Events des aktuell im {@link TaskManager} enthaltenen ForegroundTasks.
     *
     * @param context {@link ApplicationContext}
     */
    public StatusBar(final ApplicationContext context)
    {
        super();

        ResourceMap resourceMap = context.getResourceMap("bundles/statusbar");

        Objects.requireNonNull(resourceMap, "resourceMap required");

        Integer messageTimeout = resourceMap.getInteger("statusbar.message.timeout");
        this.messageTimer = new Timer(messageTimeout, event -> this.messageLabel.setText(""));
        this.messageTimer.setRepeats(false);
        this.idleIcon = resourceMap.getIcon("statusbar.icon.idle");
        int busyAnimationRate = resourceMap.getInteger("statusbar.animation.rate");

        for (int i = 0; i < this.busyIcons.length; i++)
        {
            this.busyIcons[i] = resourceMap.getIcon("statusbar.icon." + i);
        }

        this.busyIconTimer = new Timer(busyAnimationRate, event ->
        {
            this.busyIconIndex = (this.busyIconIndex + 1) % this.busyIcons.length;
            this.statusAnimationLabel.setIcon(this.busyIcons[this.busyIconIndex]);
        });

        context.getTaskManager().addPropertyChangeListener(this);
    }

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
            int value = ((Integer) (evt.getNewValue()));
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

    public void setMessage(final String s)
    {
        this.messageLabel.setText((s == null) ? "" : s);
        this.messageTimer.restart();
    }

    public void showBusyAnimation()
    {
        if (!this.busyIconTimer.isRunning())
        {
            this.statusAnimationLabel.setIcon(this.busyIcons[0]);
            this.busyIconIndex = 0;
            this.busyIconTimer.start();
        }
    }

    public void stopBusyAnimation()
    {
        this.busyIconTimer.stop();
        this.statusAnimationLabel.setIcon(this.idleIcon);
    }
}
