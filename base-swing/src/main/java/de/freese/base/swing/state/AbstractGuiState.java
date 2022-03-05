package de.freese.base.swing.state;

import java.awt.Component;
import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Basisimplementierung eines {@link GUIState}.
 *
 * @author Thomas Freese
 */
@XmlTransient
public abstract class AbstractGuiState implements GUIState
{
    /**
     *
     */
    private static final long serialVersionUID = 5367608044965230104L;
    /**
     * Um veraltete States auch mal loeschen zu koennen.
     */
    private final long created = System.currentTimeMillis();
    /**
     *
     */
    private final transient Class<?>[] supportedTypes;
    /**
     *
     */
    private boolean enabled = true;
    /**
     *
     */
    private boolean visible = true;

    /**
     * Erstellt ein neues {@link AbstractGuiState} Object.
     *
     * @param supportedTypes Class[]
     */
    protected AbstractGuiState(final Class<?>... supportedTypes)
    {
        super();

        this.supportedTypes = supportedTypes;
    }

    /**
     * Liefert den Timestamp der Erzeugung.
     *
     * @return long
     */
    public long getCreated()
    {
        return this.created;
    }

    /**
     * @see de.freese.base.swing.state.GUIState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        if (component == null)
        {
            throw new NullPointerException("component");
        }

        component.setEnabled(this.enabled);
        component.setVisible(this.visible);
    }

    /**
     * @see de.freese.base.swing.state.GUIState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        Objects.requireNonNull(component, "component required");

        this.enabled = component.isEnabled();
        this.visible = component.isVisible();
    }

    /**
     * @see de.freese.base.swing.state.GUIState#supportsType(java.lang.Class)
     */
    @Override
    public boolean supportsType(final Class<?> type)
    {
        for (Class<?> supportedType : this.supportedTypes)
        {
            if (supportedType.equals(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param visible boolean
     */
    protected void setVisible(final boolean visible)
    {
        this.visible = visible;
    }
}
