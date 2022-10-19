package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlTransient;

/**
 * Basisimplementierung eines {@link GuiState}.
 *
 * @author Thomas Freese
 */
@XmlTransient
public abstract class AbstractGuiState implements GuiState
{
    @Serial
    private static final long serialVersionUID = 5367608044965230104L;
    /**
     * Um veraltete States auch mal löschen zu können.
     */
    private final long created = System.currentTimeMillis();

    private final transient Class<?>[] supportedTypes;

    private boolean enabled = true;

    private boolean visible = true;

    protected AbstractGuiState(final Class<?>... supportedTypes)
    {
        super();

        this.supportedTypes = supportedTypes;
    }

    public long getCreated()
    {
        return this.created;
    }

    /**
     * @see GuiState#restore(java.awt.Component)
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
     * @see GuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        Objects.requireNonNull(component, "component required");

        this.enabled = component.isEnabled();
        this.visible = component.isVisible();
    }

    /**
     * @see GuiState#supportsType(java.lang.Class)
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

    protected void setVisible(final boolean visible)
    {
        this.visible = visible;
    }
}
