package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlTransient;

/**
 * @author Thomas Freese
 */
@XmlTransient
public abstract class AbstractGuiState implements GuiState {
    @Serial
    private static final long serialVersionUID = 5367608044965230104L;

    private final long created = System.currentTimeMillis();
    private final transient Class<?>[] supportedTypes;

    private boolean enabled = true;
    private boolean visible = true;

    protected AbstractGuiState(final Class<?>... supportedTypes) {
        super();

        this.supportedTypes = supportedTypes;
    }

    public long getCreated() {
        return this.created;
    }

    @Override
    public void restore(final Component component) {
        if (component == null) {
            throw new NullPointerException("component");
        }

        component.setEnabled(this.enabled);
        component.setVisible(this.visible);
    }

    @Override
    public void store(final Component component) {
        Objects.requireNonNull(component, "component required");

        this.enabled = component.isEnabled();
        this.visible = component.isVisible();
    }

    @Override
    public boolean supportsType(final Class<?> type) {
        for (Class<?> supportedType : this.supportedTypes) {
            if (supportedType.equals(type)) {
                return true;
            }
        }

        return false;
    }

    protected void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
