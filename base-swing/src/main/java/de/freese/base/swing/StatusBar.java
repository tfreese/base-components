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
import de.freese.base.core.model.Initializeable;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.task.TaskMonitor;
import de.freese.base.utils.GuiUtils;

/**
 * Diese StatusBar registriert sich als Listener am {@link TaskMonitor} und reagiert auf Events des aktuellen ForegroundTasks.<br>
 * Eine {@link IResourceMap} mit dem Namen "statusbar" muss im {@link ApplicationContext} vorhanden sein.
 */
public class StatusBar extends JPanel implements PropertyChangeListener, Initializeable
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
    private JLabel messageLabel = null;

    /**
     *
     */
    private final Timer messageTimer;

    /**
     *
     */
    private JProgressBar progressBar = null;

    /**
     *
     */
    private JLabel statusAnimationLabel = null;

    /**
     *
     */
    private final Insets zeroInsets = new Insets(0, 0, 0, 0);

    /**
     * Erstellt ein neues {@link StatusBar} Object.<br>
     * die StatusBar reagiert auf Events des aktuell im {@link TaskMonitor} enthaltenen ForegroundTasks.
     *
     * @param context {@link ApplicationContext}
     */
    public StatusBar(final ApplicationContext context)
    {
        super();

        IResourceMap resourceMap = context.getResourceMap("statusbar");

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

        context.getTaskMonitor().addPropertyChangeListener(this);
    }

    /**
     * Liefert den naechsten Satz der {@link GridBagConstraints}.
     *
     * @return {@link GridBagConstraints}
     */
    private GridBagConstraints getGBC()
    {
        GridBagConstraints gbc = GuiUtils.getGBC(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = this.zeroInsets;

        return gbc;
    }

    /**
     * @see de.freese.base.core.model.Initializeable#initialize()
     */
    @Override
    public void initialize()
    {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(this.zeroInsets)); // top, left, bottom, right

        this.messageLabel = new JLabel();

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setEnabled(false);

        this.statusAnimationLabel = new JLabel();
        this.statusAnimationLabel.setIcon(this.idleIcon);

        GridBagConstraints gbc = getGBC();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(new JSeparator(), gbc);

        gbc = getGBC();
        gbc.insets = new Insets(2, 6, 2, 3); // top, left, bottom, right;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(this.messageLabel, gbc);

        gbc = getGBC();
        gbc.insets = new Insets(2, 3, 2, 3); // top, left, bottom, right;
        add(this.progressBar, gbc);

        gbc = getGBC();
        gbc.insets = new Insets(2, 3, 2, 6); // top, left, bottom, right;
        add(this.statusAnimationLabel, gbc);
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        String propertyName = evt.getPropertyName();

        if ("started".equals(propertyName))
        {
            showBusyAnimation();
            this.progressBar.setEnabled(true);
            this.progressBar.setIndeterminate(true);
        }
        else if ("subtitle".equals(propertyName))
        {
            String text = (String) (evt.getNewValue());
            setMessage(text);
        }
        else if ("progress".equals(propertyName))
        {
            int value = ((Integer) (evt.getNewValue())).intValue();
            this.progressBar.setEnabled(true);
            this.progressBar.setIndeterminate(false);
            this.progressBar.setValue(value);
        }
        else if ("foregroundTask".equals(propertyName))
        {
            if (evt.getNewValue() == null)
            {
                // Kein Task in Ausfuehrung
                stopBusyAnimation();
                this.progressBar.setIndeterminate(false);
                this.progressBar.setEnabled(false);
                this.progressBar.setValue(0);
            }
        }
        // else
        // // if ("done".equals(propertyName))
        // {
        // // Fallback
        // stopBusyAnimation();
        // this.progressBar.setIndeterminate(false);
        // this.progressBar.setEnabled(false);
        // this.progressBar.setValue(0);
        // }
    }

    /**
     * Nachricht in der FussLeiste.
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
